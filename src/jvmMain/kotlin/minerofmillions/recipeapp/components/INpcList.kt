package minerofmillions.recipeapp.components

import minerofmillions.recipeapp.entities.saver.NPC

interface INpcList {
	val npcList: List<NPC>
	fun onNpcClicked(npc: NPC)
	fun onCloseClicked()
}
