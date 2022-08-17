package minerofmillions.recipeapp.components.defaults

import com.arkivanov.decompose.ComponentContext
import minerofmillions.recipeapp.components.IRecipeList
import minerofmillions.recipeapp.entities.saver.Recipe

class RecipeListComponent(context: ComponentContext, override val recipeList: List<Recipe>, private val onRecipeSelected: (Recipe) -> Unit) : IRecipeList, ComponentContext by context {
	override fun onRecipeClicked(recipe: Recipe) = onRecipeSelected(recipe)
}
