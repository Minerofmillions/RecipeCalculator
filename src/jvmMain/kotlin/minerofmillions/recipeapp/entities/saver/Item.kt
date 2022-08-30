package minerofmillions.recipeapp.entities.saver

import kotlin.properties.Delegates

data class Item(
	val type: Int,
	val name: String,
	val mod: String,
	val value: Int,
	val createTile: Int?,
	val createWall: Int?,
	val tooltip: String,
	val bagItems: Loot,
	val extractinatorItems: Map<Int, Int>
) : Comparable<Item> {
	val namespacedName get() = "$mod:$name"
	
	override fun compareTo(other: Item) = when {
		type != other.type -> type - other.type
		else -> namespacedName.compareTo(other.namespacedName)
	}
	companion object {
		var extractinatorTests by Delegates.notNull<Int>()
	}
}
