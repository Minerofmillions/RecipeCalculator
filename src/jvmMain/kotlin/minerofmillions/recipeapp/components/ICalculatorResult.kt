package minerofmillions.recipeapp.components

import minerofmillions.recipeapp.entities.calculator.CalculationResult
import minerofmillions.recipeapp.entities.calculator.Recipe

interface ICalculatorResult {
	val result: CalculationResult
	fun onCloseClicked()
	fun onItemClicked(item: String)
	fun onRecipeClicked(recipe: Recipe)
	
}
