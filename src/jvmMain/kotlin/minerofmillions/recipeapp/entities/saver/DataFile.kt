package minerofmillions.recipeapp.entities.saver

data class DataFile(val CurrentMods: List<Mod>, val ExtractinatorTests: Int) {
	constructor() : this(listOf(), 0)
}
