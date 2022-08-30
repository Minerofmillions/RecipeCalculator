package minerofmillions.recipeapp.components

import minerofmillions.recipeapp.entities.saver.NPC

interface INPCList {
	val npcList: List<NPC>
	fun onNPCClicked(npc: NPC)
	fun onBackClicked()
}
