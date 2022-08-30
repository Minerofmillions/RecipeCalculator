package minerofmillions.recipeapp.components.defaults

import androidx.compose.runtime.mutableStateListOf
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import minerofmillions.recipeapp.components.ICalculatorSetup
import minerofmillions.recipeapp.entities.calculator.CalculationKey
import minerofmillions.recipeapp.entities.calculator.CalculatorConditions
import minerofmillions.recipeapp.entities.calculator.ItemStack
import minerofmillions.recipeapp.entities.saver.Group
import minerofmillions.recipeapp.entities.saver.Item
import minerofmillions.recipeapp.entities.saver.NPC
import minerofmillions.recipeapp.entities.saver.Recipe
import minerofmillions.recipeapp.util.dispatcher
import minerofmillions.recipeapp.util.flatMapParallel
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue
import minerofmillions.recipeapp.entities.calculator.Recipe as CalculatorRecipe

@OptIn(ExperimentalTime::class)
class CalculatorSetupComponent(
	context: ComponentContext,
	override val items: List<Item>,
	override val recipes: List<Recipe>,
	override val npcs: List<NPC>,
	override val groups: Map<Int, Group>,
	private val onFinished: () -> Unit,
	private val onSolve: (CalculationKey) -> Unit,
) : ICalculatorSetup, ComponentContext by context {
	override val products = mutableStateListOf<ItemStack>()
	override val primitives = mutableStateListOf<String>()
	override val conditions =
		CalculatorConditions((items.flatMapTo(mutableSetOf()) { it.bagItems.conditions } + npcs.flatMapTo(mutableSetOf()) { it.drops.conditions }).flatMapTo(
			sortedSetOf()
		) { it.split("&&") }.toList()
		)
	
	override fun onSolveClicked(isRate: Boolean) {
		onSolve(CalculationKey(generateAllRecipes(), products, primitives, isRate))
	}
	
	override fun onBackClicked() = onFinished()
	
	private fun generateAllRecipes() = sequence {
		for (item in items) {
			if (item.bagItems.isNotEmpty) yield(CalculatorRecipe.OpenRecipe(item, conditions))
		}
		for (npc in npcs) {
			if (npc.drops.isNotEmpty) {
				val killRecipe = CalculatorRecipe.KillRecipe(npc, conditions)
				yield(killRecipe)
			}
		}
		yieldAll(runBlocking { constantItemRecipesAsync.await() })
		yieldAll(runBlocking { constantCraftingRecipesAsync.await() })
		println("Finished generating recipes.")
	}
	
	init {
	}
	
	private val constantItemRecipesAsync = scope.async {
		val value = measureTimedValue {
			items.filter { it.extractinatorItems.isNotEmpty() }.map(CalculatorRecipe::ExtractRecipe) +
					items.filter { it.value > 0 && it.type !in 71..74 }.map(CalculatorRecipe::SellRecipe) +
					groups.filterKeys { it in listOf(14, 29, 30, 31, 32) }.values.flatMap { group ->
						group.validItems.filterNot { it == group.iconicItem }.map {
							CalculatorRecipe(
								"Corrupt ${group.iconicItem} to $it",
								listOf(ItemStack(group.iconicItem.toString())),
								listOf(ItemStack(it.toString()))
							)
						}
					}
		}
		println("${value.value.size} item recipes generated in ${value.duration}")
		value.value
	}
	
	private val constantCraftingRecipesAsync = scope.async {
		val value = measureTimedValue {
			recipes.flatMapParallel { CalculatorRecipe.CraftingRecipe.generateRecipes(it, groups) }
		}
		println("${value.value.size} crafting recipes generated in ${value.duration}")
		value.value
	}
	
	companion object {
		private val scope = CoroutineScope(dispatcher)
	}
}
