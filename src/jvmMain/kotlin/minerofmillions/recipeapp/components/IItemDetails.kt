package minerofmillions.recipeapp.components

import minerofmillions.recipeapp.entities.saver.Item
import minerofmillions.recipeapp.entities.saver.Recipe

interface IItemDetails {
	val item: Item
	fun onBackClicked()
	fun onItemClicked(item: Int)
	fun onRecipeClicked(recipe: Recipe)
}
