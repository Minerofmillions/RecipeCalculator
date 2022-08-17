package minerofmillions.recipeapp.entities.saver

data class DataFile(val CurrentMods: List<Mod>, val ExtractinatorTests: Int, val FishingTests: Int) {
	constructor() : this(listOf(), 0, 0)
}
