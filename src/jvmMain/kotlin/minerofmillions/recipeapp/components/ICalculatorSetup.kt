package minerofmillions.recipeapp.components

import minerofmillions.recipeapp.entities.calculator.CalculatorConditions
import minerofmillions.recipeapp.entities.calculator.ItemStack
import minerofmillions.recipeapp.entities.saver.Group
import minerofmillions.recipeapp.entities.saver.Item
import minerofmillions.recipeapp.entities.saver.NPC
import minerofmillions.recipeapp.entities.saver.Recipe

interface ICalculatorSetup {
	val items: List<Item>
	val recipes: List<Recipe>
	val npcs: List<NPC>
	val groups: Map<Int, Group>
	
	val products: List<ItemStack>
	val primitives: List<String>
	val conditions: CalculatorConditions
	
	fun onSolveClicked(isRate: Boolean)
	fun onCloseClicked()
}
