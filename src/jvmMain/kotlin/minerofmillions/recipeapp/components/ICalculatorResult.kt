package minerofmillions.recipeapp.components

import minerofmillions.recipeapp.entities.calculator.CalculationResult
import minerofmillions.recipeapp.entities.calculator.Recipe

interface ICalculatorResult {
	val result: CalculationResult
	fun onBackClicked()
	fun onItemClicked(item: String)
	fun onRecipeClicked(recipe: Recipe)
	
}
