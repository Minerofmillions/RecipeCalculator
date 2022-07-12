package minerofmillions.recipeapp.data.recipeentities

import minerofmillions.recipeapp.data.ItemStack
import minerofmillions.recipeapp.util.compareTo

data class TerrariaRecipe(
	val acceptedGroups: List<Int>?,
	val createItem: TerrariaItemStack,
	val requiredItems: List<TerrariaItemStack>,
	val requiredTiles: List<Int>,
	val conditions: List<String>,
) : Comparable<TerrariaRecipe> {
	override fun compareTo(other: TerrariaRecipe): Int = comparator.compare(this, other)
	
	companion object {
		private val comparator = Comparator<TerrariaRecipe> { a, b ->
			when {
				a.createItem != b.createItem -> a.createItem compareTo b.createItem
				a.requiredItems != b.requiredItems -> a.requiredItems compareTo b.requiredItems
				else -> 0
			}
		}
	}
}
