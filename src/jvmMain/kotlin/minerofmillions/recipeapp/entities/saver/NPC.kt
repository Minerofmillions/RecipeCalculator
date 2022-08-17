package minerofmillions.recipeapp.entities.saver

import com.google.common.collect.ImmutableTable

data class NPC(
	val name: String,
	val type: Int,
	val mod: String,
	val drops: Loot,
	val banner: Int,
	val killsPerBanner: Int,
) {
	val namespacedName get() = "$mod:$name"
	constructor() : this(
		"", 0, "", Loot(ImmutableTable.of(), ImmutableTable.of(), ImmutableTable.of(), ImmutableTable.of()), 0, 0
	)
}
