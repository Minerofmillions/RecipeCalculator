package minerofmillions.recipeapp.components.defaults

import com.arkivanov.decompose.ComponentContext
import minerofmillions.recipeapp.components.IItemDetails
import minerofmillions.recipeapp.entities.saver.Item
import minerofmillions.recipeapp.entities.saver.Recipe

class ItemDetailsComponent(
	context: ComponentContext,
	override val item: Item,
	private val onFinished: () -> Unit,
	private val onItemSelected: (Int) -> Unit,
	private val onRecipeSelected: (Recipe) -> Unit,
) : IItemDetails, ComponentContext by context {
	override fun onBackClicked() = onFinished()
	override fun onItemClicked(item: Int) = onItemSelected(item)
	override fun onRecipeClicked(recipe: Recipe) = onRecipeSelected(recipe)
}
