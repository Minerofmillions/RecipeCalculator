package minerofmillions.recipeapp.components

import minerofmillions.recipeapp.entities.calculator.CalculationKey

interface ICalculatorSolving {
	val key: CalculationKey
	
	fun onBackClicked()
}
