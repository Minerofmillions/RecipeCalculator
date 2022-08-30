package minerofmillions.recipeapp.components.defaults

import com.arkivanov.decompose.ComponentContext
import minerofmillions.recipeapp.components.INPCList
import minerofmillions.recipeapp.entities.saver.NPC

class NPCListComponent(
	context: ComponentContext,
	override val npcList: List<NPC>,
	private val onFinished: () -> Unit,
	private val onNPCSelected: (NPC) -> Unit,
) : INPCList, ComponentContext by context {
	override fun onBackClicked() = onFinished()
	override fun onNPCClicked(npc: NPC) = onNPCSelected(npc)
}
