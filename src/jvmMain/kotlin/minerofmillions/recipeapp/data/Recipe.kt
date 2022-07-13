package minerofmillions.recipeapp.data

import androidx.compose.runtime.mutableStateListOf
import minerofmillions.recipeapp.util.contentsEqualOrderless

class Recipe(val name: String, input: Collection<ItemStack>, output: Collection<ItemStack>) {
	private val _inputs = mutableStateListOf<ItemStack>()
	private val _outputs = mutableStateListOf<ItemStack>()
	
	val inputs: List<ItemStack> = this._inputs
	val outputs: List<ItemStack> = this._outputs
	
	override fun equals(other: Any?): Boolean = when (other) {
		null -> false
		!is Recipe -> false
		else -> other.name == name && inputs.contentsEqualOrderless(other.inputs) && outputs.contentsEqualOrderless(other.outputs)
	}
	
	init {
		generateIO(input, output, this._inputs, this._outputs)
	}
	
	companion object {
		private fun generateIO(i: Collection<ItemStack>, o: Collection<ItemStack>, input: MutableList<ItemStack>, output: MutableList<ItemStack>) {
			val inputItems = i.mapTo(sortedSetOf(), ItemStack::item)
			val outputItems = o.mapTo(sortedSetOf(), ItemStack::item)
			
			input.addAll(i.filterNot { it.item in outputItems })
			output.addAll(o.filterNot { it.item in inputItems })
			
			(inputItems intersect outputItems).forEach { item ->
				val inputItem = i.first { it.item == item }
				val outputItem = o.first { it.item == item }
				
				if (inputItem.amount > outputItem.amount) input.add(ItemStack(item, inputItem.amount - outputItem.amount))
				else if (outputItem.amount > inputItem.amount) output.add(ItemStack(item, outputItem.amount - inputItem.amount))
			}
		}
	}
}
