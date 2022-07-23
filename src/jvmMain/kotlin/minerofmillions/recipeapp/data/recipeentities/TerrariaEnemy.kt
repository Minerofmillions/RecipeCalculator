package minerofmillions.recipeapp.data.recipeentities

data class TerrariaEnemy(
	val name: String,
	val type: Int,
	val drops: TerrariaLoot
) : Comparable<TerrariaEnemy> {
	override fun toString(): String =
		"\"$name\": $drops"
	override fun compareTo(other: TerrariaEnemy): Int = comparator.compare(this, other)
	
	companion object {
		private val comparator = compareBy(TerrariaEnemy::type, TerrariaEnemy::name)
	}
}
