package minerofmillions.recipeapp.components.defaults

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import minerofmillions.recipeapp.components.ICalculatorSolving
import minerofmillions.recipeapp.entities.calculator.*
import minerofmillions.recipeapp.util.*
import org.ojalgo.optimisation.ExpressionsBasedModel
import org.ojalgo.scalar.RationalNumber
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.system.measureTimeMillis

class CalculatorSolvingComponent(
	context: ComponentContext,
	override val key: CalculationKey,
	private val onCancelled: () -> Unit,
	private val onSolved: (CalculationResult) -> Unit,
) : ICalculatorSolving, ComponentContext by context {
	override fun onBackClicked() = onCancelled()
	
	private val recipes by key::recipes
	private val primitives = key.primitives + listOf("71", "72", "73", "74")
	private fun solveAsync() = coroutineScope(Dispatchers.IO).launch {
		val recipesToUse: ConcurrentMap<String, MutableSet<Recipe>> = ConcurrentHashMap()
		
		println("Overfilling recipesToMake/recipesToUse")
		val timeToOverfill = measureTimeMillis {
			recipes.forEachParallel { recipe ->
				recipe.outputItems.forEach { item ->
					recipesToMake.getOrPut(item) { mutableSetOf() }.add(recipe)
				}
				recipe.inputItems.forEach { item ->
					recipesToUse.getOrPut(item) { mutableSetOf() }.add(recipe)
				}
			}
		}
		println("Took ${Duration.of(timeToOverfill, ChronoUnit.MILLIS)}")
		
		println("Pruning recipesToMake/recipesToUse")
		val timeToPrune = measureTimeMillis {
			val recipesToRemove = mutableSetOf<Recipe>()
			recipesToUse.zip(recipesToMake).forEachParallel { (_, using, making) ->
				for ((use, make) in listOf(using, making).permutations().toList()) {
					if (use in recipesToRemove || make in recipesToRemove || !use.isReverseOf(make)) continue
					val useMakesLessThanMakeUses =
						use.outputs.all { output -> output.amount < make.inputs.first { it.item == output.item }.amount }
					val makeMakesLessThanUseUses =
						make.outputs.all { output -> output.amount < use.inputs.first { it.item == output.item }.amount }
					when {
						useMakesLessThanMakeUses -> recipesToRemove.add(use)
						makeMakesLessThanUseUses -> recipesToRemove.add(make)
						making.size > using.size -> recipesToRemove.add(use)
						using.size > making.size -> recipesToRemove.add(make)
					}
				}
			}
			recipesToRemove.forEach(recipesToMake::removeFromAll)
		}
		
		println("Took ${Duration.of(timeToPrune, ChronoUnit.MILLIS)}")
		
		val result = solve()
		withContext(Dispatchers.Default) {
			onSolved(result)
		}
	}
	
	private val recipesToMake: ConcurrentMap<String, MutableSet<Recipe>> = ConcurrentHashMap()
	
	init {
		solveAsync()
	}
	
	private fun recipesToMake(item: String) = recipesToMake[item] ?: emptySet()
	
	private val recursiveItems = mutableMapOf<String, Boolean>()
	private fun isRecursive(item: String) = recursiveItems.getOrPut(item) { item in allInputsFor(item) }
	
	private val inputsFor = mutableMapOf<String, Set<String>>()
	private fun allInputsFor(target: String): Set<String> =
		inputsFor.getOrPut(target) { allInputsFor(target, emptySet()) }
	
	private fun allInputsFor(target: String, memo: Collection<String>): Set<String> =
		recipesToMake(target).filterNot { it is Recipe.SellRecipe }.flatMapTo(sortedSetOf()) { recipe ->
			recipe.inputItems.let { inputs ->
				inputs + inputs.filterNot(memo::contains).flatMap { allInputsFor(it, memo + target) }
			}
		}
	
	private fun solve(): CalculationResult {
		val usedRecipes = sortedMapOf<Recipe, RationalNumber>()
		val inputs = key.products.toMutableList()
		val primitives = mutableListOf<ItemStack>()
		val outputs = mutableListOf<ItemStack>()
		
		fun getRatio(product: ItemStack, recipe: Recipe) =
			(product.amount / recipe.outputs.first { it.item == product.item }.amount).let {
				if (key.isRate) it else it.ceil()
			}
		
		while (inputs.isNotEmpty()) {
			val product = inputs.first()
			val recipes = recipesToMake(product.item)
			if (product.item in this.primitives || recipes.isEmpty()) {
				inputs.remove(product)
				primitives.addStack(product)
				continue
			}
			if (recipes.size == 1 && !isRecursive(product.item)) {
				val recipe = recipes.first()
				val ratio = getRatio(product, recipe)
				
				if (ratio.aboutZero()) {
					inputs.remove(product)
					primitives.addStack(product)
					continue
				}
				
				println("Tree recipe $recipe@$ratio")
				usedRecipes.add(recipe, ratio)
				recipe.inputs.forEach { inputs.addStack(it * ratio) }
				recipe.outputs.forEach { outputs.addStack(it * ratio) }
				
				Recipe.generateIO(inputs, outputs)
				
				continue
			}
			
			val matrixSolution = solveMatrixAround(product, inputs)
			if (matrixSolution is CalculationResult.ValidSolution && matrixSolution.recipes.isNotEmpty()) {
				matrixSolution.recipes.forEach { (recipe, ratio) ->
					usedRecipes.add(recipe, ratio)
					recipe.inputs.forEach { inputs.addStack(it * ratio) }
					recipe.outputs.forEach { outputs.addStack(it * ratio) }
				}
				Recipe.generateIO(inputs, outputs)
			} else {
				inputs.remove(product)
				primitives.addStack(product)
			}
		}
		
		return CalculationResult.ValidSolution(usedRecipes)
	}
	
	private fun solveMatrixAround(item: ItemStack, currentInputs: Collection<ItemStack>): CalculationResult {
		println("Matrix around ${item.item}")
		val recipes = mutableSetOf<Recipe>()
		val inputs = mutableSetOf(item.item)
		val outputs = mutableSetOf<String>()
		val solvedInputs = mutableSetOf<String>()
		
		while (inputs.isNotEmpty()) {
			val input = inputs.first()
			inputs.remove(input)
			solvedInputs.add(input)
			if (input in this.primitives) continue
			
			val recipesToMake = recipesToMake(input)
			if (recipesToMake.size < 2 && !isRecursive(input)) continue
			
			recipes.addAll(recipesToMake)
			inputs.addAll(recipesToMake.flatMap(Recipe::inputItems).filterNot(solvedInputs::contains))
			outputs.addAll(recipesToMake.flatMap(Recipe::outputItems))
			
			val currentUnsolvedInputs = currentInputs.map(ItemStack::item).filterNot(solvedInputs::contains)
			val currentSolvableInputs = currentUnsolvedInputs.filter { potentialProduct ->
				recipesToMake(potentialProduct).any { potentialRecipe -> potentialRecipe.inputs.any { it.item in inputs union solvedInputs } }
			}
			inputs.addAll(currentSolvableInputs)
		}
		
		return matrixSolve(recipes, currentInputs.filter { it.item in outputs }, key.isRate)
	}
	
	private fun matrixSolve(
		recipes: Collection<Recipe>,
		products: Collection<ItemStack>,
		isRate: Boolean,
	): CalculationResult {
		println(recipes)
		print("${recipes.size} recipes")
		val allInputs = recipes.flatMapTo(sortedSetOf(), Recipe::inputItems)
		val allOutputs = recipes.flatMapTo(sortedSetOf(), Recipe::outputItems)
		
		val usefulOutputs = allOutputs.filterTo(mutableSetOf()) { output -> products.any { it.item == output } }
		
		val allItems = allInputs + usefulOutputs
		print(", ${allItems.size} items")
		
		val slack = allInputs - allOutputs + primitives.filter(usefulOutputs::contains)
		val surplus = allItems - slack
		
		println(", ${slack.size} primitives, ${surplus.size} surplus")
		
		val criticalRatio = recipes.maxOf(Recipe::criticalNumerator) / recipes.minOf(Recipe::criticalDenominator)
		
		val model = ExpressionsBasedModel()
		
		val recipeVariables =
			recipes.associateWith { model.addVariable().integer(!isRate).weight(1).lower(0) }
		val slackVariables = slack.associateWith { model.addVariable().weight(criticalRatio).lower(0) }
		val surplusVariables = surplus.associateWith { model.addVariable().weight(criticalRatio).lower(0) }
		
		allItems.forEach { item ->
			val expression =
				model.addExpression().level(products.firstOrNull { it.item == item }?.amount ?: RationalNumber.ZERO)
			
			slackVariables[item]?.let { expression.set(it, 1) }
			surplusVariables[item]?.let { expression.set(it, -1) }
			
			recipeVariables.forEach { (recipe, variable) ->
				recipe.inputs.filter { it.item == item }.forEach { expression.add(variable, -it.amount) }
				recipe.outputs.filter { it.item == item }.forEach { expression.add(variable, it.amount) }
			}
		}
		
		val solution = model.minimise()
		println("Done. Feasible: ${solution.state.isFeasible}, Optimal: ${solution.state.isOptimal}")
		return if (!solution.state.isFeasible) CalculationResult.NoSolution else CalculationResult.ValidSolution.fromVariables(
			recipeVariables
		)
	}
}
