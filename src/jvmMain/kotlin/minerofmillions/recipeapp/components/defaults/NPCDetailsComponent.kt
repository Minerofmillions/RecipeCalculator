package minerofmillions.recipeapp.components.defaults

import com.arkivanov.decompose.ComponentContext
import minerofmillions.recipeapp.components.INpcDetails
import minerofmillions.recipeapp.entities.saver.NPC

class NPCDetailsComponent(
	context: ComponentContext,
	override val npc: NPC,
	private val onFinished: () -> Unit,
	private val onItemSelected: (String) -> Unit,
) : INpcDetails, ComponentContext by context {
	override fun onCloseClicked() = onFinished()
	override fun onItemClicked(item: String) = onItemSelected(item)
}
