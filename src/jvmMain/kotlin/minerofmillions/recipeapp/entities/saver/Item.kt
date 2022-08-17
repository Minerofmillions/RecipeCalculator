package minerofmillions.recipeapp.entities.saver

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
) {
	val namespacedName get() = "$mod:$name"
}
