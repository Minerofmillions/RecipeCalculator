package minerofmillions.recipeapp.components.defaults

import com.arkivanov.decompose.ComponentContext
import minerofmillions.recipeapp.components.IItemDetails
import minerofmillions.recipeapp.entities.saver.Item
import minerofmillions.recipeapp.entities.saver.Recipe

class ItemDetailsComponent(
	context: ComponentContext,
	override val item: Item,
	private val onFinished: () -> Unit,
	private val onItemSelected: (Item) -> Unit,
	private val onRecipeSelected: (Recipe) -> Unit,
) : IItemDetails, ComponentContext by context {
	override fun onCloseClicked() = onFinished()
	override fun onItemClicked(item: Item) = onItemSelected(item)
	override fun onRecipeClicked(recipe: Recipe) = onRecipeSelected(recipe)
}
