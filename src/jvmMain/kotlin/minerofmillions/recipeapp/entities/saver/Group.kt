package minerofmillions.recipeapp.entities.saver

data class Group(val validItems: List<Int>, val iconicItem: Int) {
	constructor() : this(emptyList(), 0)
}
