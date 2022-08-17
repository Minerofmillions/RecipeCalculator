package minerofmillions.recipeapp.components

import minerofmillions.recipeapp.entities.saver.Recipe

interface IRecipeDetails {
	val recipe: Recipe
	fun onCloseClicked()
	fun onItemClicked(item: String)
}
