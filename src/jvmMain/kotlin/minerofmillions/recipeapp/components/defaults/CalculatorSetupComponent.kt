package minerofmillions.recipeapp.components.defaults

import androidx.compose.runtime.mutableStateListOf
import com.arkivanov.decompose.ComponentContext
import minerofmillions.recipeapp.components.ICalculatorSetup
import minerofmillions.recipeapp.entities.calculator.CalculationKey
import minerofmillions.recipeapp.entities.calculator.CalculatorConditions
import minerofmillions.recipeapp.entities.calculator.ItemStack
import minerofmillions.recipeapp.entities.saver.Group
import minerofmillions.recipeapp.entities.saver.Item
import minerofmillions.recipeapp.entities.saver.NPC
import minerofmillions.recipeapp.entities.saver.Recipe
import minerofmillions.recipeapp.entities.calculator.Recipe as CalculatorRecipe

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
		CalculatorConditions(items.flatMapTo(sortedSetOf()) { it.bagItems.conditions } + npcs.flatMapTo(mutableSetOf()) { it.drops.conditions })
	
	override fun onSolveClicked(isRate: Boolean) {
		onSolve(CalculationKey(generateUsefulRecipes(), products, primitives, isRate))
	}
	
	override fun onCloseClicked() = onFinished()
	
	private fun generateAllRecipes() = sequence {
		for (item in items) {
			if (item.bagItems.isNotEmpty()) yield(CalculatorRecipe.OpenRecipe(item, conditions))
			if (item.value > 0 && item.type !in 71..74) yield(CalculatorRecipe.SellRecipe(item))
		}
		for (npc in npcs) {
			if (npc.drops.isNotEmpty()) yield(CalculatorRecipe.KillRecipe(npc, conditions))
		}
		for (recipe in recipes) {
			val groups = groups.filter { it.key in recipe.acceptedGroups }
			yieldAll(CalculatorRecipe.CraftingRecipe.generateRecipes(recipe, groups))
		}
	}.toList()
	
	private fun generateUsefulRecipes(): List<CalculatorRecipe> = generateAllRecipes().let { allRecipes ->
		val productsToSolve = products.mapTo(mutableSetOf(), ItemStack::item)
		val usedRecipes = mutableListOf<CalculatorRecipe>()
		
		while (productsToSolve.isNotEmpty()) {
			val product = productsToSolve.first()
			productsToSolve.remove(product)
			
			val recipe = allRecipes.filter { product in it.outputItems }
			if (product !in primitives && recipe.isNotEmpty()) {
				usedRecipes.addAll(recipe)
				productsToSolve.addAll(recipe.flatMapTo(mutableSetOf(), CalculatorRecipe::inputItems))
			}
		}
		
		return usedRecipes
	}
}
