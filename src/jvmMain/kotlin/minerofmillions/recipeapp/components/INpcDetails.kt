package minerofmillions.recipeapp.components

import minerofmillions.recipeapp.entities.saver.NPC

interface INpcDetails {
	val npc: NPC
	fun onCloseClicked()
	fun onItemClicked(item: String)
}
