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

class CalculatorSolvingComponent(
	context: ComponentContext,
	override val key: CalculationKey,
	private val onCancelled: () -> Unit,
	private val onSolved: (CalculationResult) -> Unit,
) : ICalculatorSolving, ComponentContext by context {
	init {
		solveAsync()
	}
	
	override fun onCancel() = onCancelled()
	
	private fun solveAsync() {
		coroutineScope(Dispatchers.IO).launch {
			val result = solve()
			withContext(Dispatchers.Default) {
				onSolved(result)
			}
		}
	}
	
	private val recipes = key.recipes
	
	private val rtm = mutableMapOf<String, Set<Recipe>>()
	private fun recipesToMake(item: String) = rtm.getOrPut(item) {
		recipes.filter { item in it.outputItems }.filterNotTo(mutableSetOf()) { possible ->
			recipes.filter { item in it.inputItems }.any { outgoing ->
				possible.inputs.all { input ->
					outgoing.outputs.any { input.item == it.item && input.amount >= it.amount }
				} && possible.outputs.any { output ->
					outgoing.inputs.any { output.item == it.item && output.amount <= it.amount }
				} && outgoing.inputs.all { input ->
					possible.outputs.any { input.item == it.item }
				} && outgoing.outputs.all { output ->
					possible.inputs.any { output.item == it.item }
				}
			}
		}
	}
	
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
		val usedRecipes = mutableMapOf<Recipe, RationalNumber>()
		val inputs = key.products.toMutableList()
		val primitives = mutableListOf<ItemStack>()
		val outputs = mutableListOf<ItemStack>()
		
		fun getRatio(product: ItemStack, recipe: Recipe) =
			(product.amount / recipe.outputs.first { it.item == product.item }.amount).let {
				if (key.isRate) it else it.ceil()
			}
		
		while (inputs.isNotEmpty()) {
			val product = inputs.first()
			val recipesToMake = recipesToMake(product.item)
			if (product.item in key.primitives || recipesToMake.isEmpty()) {
				inputs.remove(product)
				primitives.addStack(product)
				continue
			}
			if (recipesToMake.size == 1 && !isRecursive(product.item)) {
				val recipe = recipesToMake.first()
				val ratio = getRatio(product, recipe)
				
				usedRecipes.add(recipe, ratio)
				recipe.inputs.forEach { inputs.addStack(it * ratio) }
				recipe.outputs.forEach { outputs.addStack(it * ratio) }
				
				Recipe.generateIO(inputs, outputs)
				
				continue
			}
			
			val matrixSolution = solveMatrixAround(product)
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
	
	private fun solveMatrixAround(item: ItemStack): CalculationResult {
		val recipes = mutableSetOf<Recipe>()
		val inputs = mutableSetOf(item.item)
		val outputs = mutableSetOf<String>()
		val solvedInputs = mutableSetOf<String>()
		
		while (inputs.isNotEmpty()) {
			val input = inputs.first()
			inputs.remove(input)
			solvedInputs.add(input)
			if (input in key.primitives) continue
			
			val recipesToMake = recipesToMake(input)
			if (recipesToMake.size < 2 && !isRecursive(input)) continue
			
			recipes.addAll(recipesToMake)
			inputs.addAll(recipes.flatMap(Recipe::inputItems).filterNot(solvedInputs::contains))
			outputs.addAll(recipes.flatMap(Recipe::outputItems))
			
			inputs.addAll(
				key.products.map(ItemStack::item).filterNot(solvedInputs::contains).filter { potentialProduct ->
						recipesToMake(potentialProduct).any { potentialRecipe -> potentialRecipe.inputs.all { it.item in inputs union solvedInputs } }
					})
		}
		
		return matrixSolve(recipes, key.products.filter { it.item in outputs }, key.primitives, key.isRate)
	}
	
	private fun matrixSolve(
		recipes: Collection<Recipe>,
		products: Collection<ItemStack>,
		primitives: Collection<String>,
		isRate: Boolean,
	): CalculationResult {
		val allInputs = recipes.flatMapTo(sortedSetOf(), Recipe::inputItems)
		val allOutputs = recipes.flatMapTo(sortedSetOf(), Recipe::outputItems)
		
		val allItems = allInputs + allOutputs
		
		val slack = allInputs - allOutputs + primitives.filter(allOutputs::contains)
		val surplus = allOutputs - slack
		
		val criticalRatio = (recipes.maxOfOrNull(Recipe::criticalNumerator)
			?: RationalNumber.ONE) / (recipes.minOfOrNull(Recipe::criticalDenominator) ?: RationalNumber.ONE)
		
		val model = ExpressionsBasedModel()
		
		val recipeVariables = recipes.associateWith { model.addVariable().integer(!isRate).weight(criticalRatio).lower(0) }
		val slackVariables = slack.associateWith { model.addVariable().weight(1).lower(0) }
		val surplusVariables = surplus.associateWith { model.addVariable().weight(criticalRatio.invert()).lower(0) }
		
		allItems.forEach { item ->
			val expression = model.addExpression()
				.level(products.firstOrNull { it.item == item }?.amount ?: RationalNumber.ZERO)
			
			slackVariables[item]?.let { expression.set(it, 1) }
			surplusVariables[item]?.let { expression.set(it, -1) }
			
			recipeVariables.forEach { (recipe, variable) ->
				recipe.inputs.filter { it.item == item }.forEach { expression.add(variable, -it.amount) }
				recipe.outputs.filter { it.item == item }.forEach { expression.add(variable, it.amount) }
			}
		}
		
		val solution = model.minimise()
		return if (!solution.state.isFeasible) CalculationResult.NoSolution else CalculationResult.ValidSolution.fromVariables(recipeVariables)
	}
}
