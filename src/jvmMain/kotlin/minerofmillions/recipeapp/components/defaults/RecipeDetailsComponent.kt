package minerofmillions.recipeapp.components.defaults

import com.arkivanov.decompose.ComponentContext
import minerofmillions.recipeapp.components.IRecipeDetails
import minerofmillions.recipeapp.entities.saver.Recipe

class RecipeDetailsComponent(context: ComponentContext, override val recipe: Recipe, private val onItemSelected: (String) -> Unit, private val onFinished: () -> Unit) : IRecipeDetails, ComponentContext by context {
	override fun onCloseClicked() = onFinished()
	override fun onItemClicked(item: String) = onItemSelected(item)
}
