package minerofmillions.recipeapp.components.defaults

import com.arkivanov.decompose.ComponentContext
import minerofmillions.recipeapp.components.IMainScreen

class MainScreenComponent(
	context: ComponentContext,
	private val onRecipesShown: () -> Unit,
	private val onItemsShown: () -> Unit,
	private val onNPCsShown: () -> Unit,
	private val onCalculatorShown: () -> Unit,
) : IMainScreen, ComponentContext by context {
	override fun onShowRecipes() = onRecipesShown()
	override fun onShowItems() = onItemsShown()
	override fun onShowNPCs() = onNPCsShown()
	override fun onShowCalculator() = onCalculatorShown()
}
