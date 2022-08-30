package minerofmillions.recipeapp.components

import minerofmillions.recipeapp.entities.saver.NPC

interface INPCDetails {
	val npc: NPC
	fun onBackClicked()
	fun onItemClicked(item: Int)
}
