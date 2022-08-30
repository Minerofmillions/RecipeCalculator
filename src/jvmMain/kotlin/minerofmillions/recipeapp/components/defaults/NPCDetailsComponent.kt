package minerofmillions.recipeapp.components.defaults

import com.arkivanov.decompose.ComponentContext
import minerofmillions.recipeapp.components.INPCDetails
import minerofmillions.recipeapp.entities.saver.NPC

class NPCDetailsComponent(
	context: ComponentContext,
	override val npc: NPC,
	private val onFinished: () -> Unit,
	private val onItemSelected: (Int) -> Unit,
) : INPCDetails, ComponentContext by context {
	override fun onBackClicked() = onFinished()
	override fun onItemClicked(item: Int) = onItemSelected(item)
}
