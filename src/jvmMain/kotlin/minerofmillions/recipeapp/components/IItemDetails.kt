package minerofmillions.recipeapp.components

import minerofmillions.recipeapp.entities.saver.Item
import minerofmillions.recipeapp.entities.saver.Recipe

interface IItemDetails {
	val item: Item
	fun onCloseClicked()
	fun onItemClicked(item: Item)
	fun onRecipeClicked(recipe: Recipe)
}
