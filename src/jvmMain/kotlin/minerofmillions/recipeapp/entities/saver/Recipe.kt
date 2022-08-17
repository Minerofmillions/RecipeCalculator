package minerofmillions.recipeapp.entities.saver

import minerofmillions.recipeapp.entities.calculator.ItemStack

data class Recipe(
	val createItem: ItemStack,
	val conditions: List<String>,
	val requiredItems: List<ItemStack>,
	val requiredTiles: List<Int>,
	val mod: String,
	val acceptedGroups: List<Int>
) : Comparable<Recipe> {
	override fun compareTo(other: Recipe): Int = when {
		mod != other.mod -> mod.compareTo(other.mod)
		createItem != other.createItem -> createItem.compareTo(other.createItem)
		requiredItems.size != other.requiredItems.size -> requiredItems.size.compareTo(other.requiredItems.size)
		else -> requiredItems.zip(other.requiredItems).map { it.first.compareTo(it.second) }.firstOrNull { it != 0 } ?: 0
	}
}
