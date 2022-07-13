package minerofmillions.recipeapp.data.recipeentities

data class TerrariaItem(val type: Int, val name: String, val mod: String, val value: Int, val createTile: Int?, val createWall: Int?, val bagItems: Map<Int, Int>) : Comparable<TerrariaItem> {
	val namespacedName get() = "${mod}:${name}"
	override fun compareTo(other: TerrariaItem) = comparator.compare(this, other)
	override fun toString() = "$name ($mod, $type) ($value, $createTile, $createWall, $bagItems)"
	companion object {
		val comparator = compareBy(TerrariaItem::type, TerrariaItem::name)
	}
}
