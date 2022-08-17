package minerofmillions.recipeapp.components.defaults

import com.arkivanov.decompose.ComponentContext
import minerofmillions.recipeapp.components.ICalculatorResult
import minerofmillions.recipeapp.entities.calculator.CalculationResult
import minerofmillions.recipeapp.entities.calculator.Recipe

class CalculatorResultComponent(
	context: ComponentContext,
	override val result: CalculationResult,
	private val onFinished: () -> Unit,
	private val onClickItem: (String) -> Unit,
	private val onClickRecipe: (Recipe) -> Unit
) : ICalculatorResult, ComponentContext by context {
	override fun onCloseClicked() = onFinished()
	override fun onItemClicked(item: String) = onClickItem(item)
	override fun onRecipeClicked(recipe: Recipe) = onClickRecipe(recipe)
}
