package minerofmillions.recipeapp.data.recipeentities

data class TerrariaItem(val type: Int, val name: String, val value: Int, val createTile: Int?, val createWall: Int?, val bagItems: Map<Int, Int>) : Comparable<TerrariaItem> {
	override fun compareTo(other: TerrariaItem) = comparator.compare(this, other)
	
	companion object {
		val comparator = compareBy(TerrariaItem::type, TerrariaItem::name)
	}
}
