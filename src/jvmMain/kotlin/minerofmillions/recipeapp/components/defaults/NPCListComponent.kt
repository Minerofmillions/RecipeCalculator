package minerofmillions.recipeapp.components.defaults

import com.arkivanov.decompose.ComponentContext
import minerofmillions.recipeapp.components.INpcList
import minerofmillions.recipeapp.entities.saver.NPC

class NPCListComponent(
	context: ComponentContext,
	override val npcList: List<NPC>,
	private val onFinished: () -> Unit,
	private val onNPCSelected: (NPC) -> Unit,
) : INpcList, ComponentContext by context {
	override fun onCloseClicked() = onFinished()
	override fun onNpcClicked(npc: NPC) = onNPCSelected(npc)
}
