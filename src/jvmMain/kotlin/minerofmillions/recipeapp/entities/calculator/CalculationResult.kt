package minerofmillions.recipeapp.entities.calculator

import minerofmillions.recipeapp.util.aboutZero
import org.ojalgo.optimisation.Variable
import org.ojalgo.scalar.RationalNumber

sealed class CalculationResult {
	object NoSolution : CalculationResult()
	data class ValidSolution(val recipes: Map<Recipe, RationalNumber>) : CalculationResult() {
		val combinedInputs: List<ItemStack>
		val combinedOutputs: List<ItemStack>
		
		init {
			val inputs = mutableListOf<ItemStack>()
			val outputs = mutableListOf<ItemStack>()
			
			val ins = recipes.flatMap { (recipe, amount) -> recipe.inputs.map { it * amount } }
			val outs = recipes.flatMap { (recipe, amount) -> recipe.outputs.map { it * amount } }
			
			Recipe.generateIO(ins, outs, inputs, outputs)
			
			combinedInputs = inputs.sorted()
			combinedOutputs = outputs.sorted()
		}
		
		companion object {
			fun fromVariables(recipeVariables: Map<Recipe, Variable>): ValidSolution =
				ValidSolution(recipeVariables.mapValues { (_, ratio) -> RationalNumber.valueOf(ratio.value) }
					.filterValues { !it.aboutZero() })
		}
	}
}
