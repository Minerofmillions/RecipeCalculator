package minerofmillions.recipeapp.components

import minerofmillions.recipeapp.entities.saver.Recipe

interface IRecipeList {
	val recipeList: List<Recipe>
	fun onRecipeClicked(recipe: Recipe)
}
