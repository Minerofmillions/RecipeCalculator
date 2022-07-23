package minerofmillions.recipeapp.data.recipeentities

import minerofmillions.recipeapp.data.Rational

class TerrariaItem(val type: Int, val name: String, val mod: String, val value: Int, val createTile: Int?, val createWall: Int?, val bagItems: TerrariaLoot, extractinatorItems: Map<Int, Int>) : Comparable<TerrariaItem> {
	val extractinatorItems = extractinatorItems.mapValues { (_, count) -> Rational.of(count, 10000) }
	val namespacedName get() = "${mod}:${name}"
	override fun compareTo(other: TerrariaItem) = comparator.compare(this, other)
	override fun toString() = "$name ($mod, $type) ($value, $createTile, $createWall, $bagItems)"
	companion object {
		val comparator = compareBy(TerrariaItem::type, TerrariaItem::name)
	}
}
