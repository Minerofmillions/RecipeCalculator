package minerofmillions.recipeapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import minerofmillions.recipeapp.components.INPCDetails
import minerofmillions.recipeapp.components.INPCList
import minerofmillions.recipeapp.entities.saver.NPC
import minerofmillions.recipeapp.ui.util.LootView
import minerofmillions.recipeapp.ui.util.ScrollableColumn

@Composable
fun NPCList(component: INPCList) {
	ScrollableColumn {
		items(component.npcList) {
			NPCBrief(it, Modifier.clickable { component.onNPCClicked(it) })
		}
	}
}

@Composable
fun NPCBrief(npc: NPC, modifier: Modifier = Modifier) {
	Row(modifier.fillMaxWidth()) {
		Text(
			npc.type.toString(),
			style = MaterialTheme.typography.body2,
			modifier = Modifier.align(Alignment.CenterVertically)
		)
		Text(npc.namespacedName)
	}
}

@Composable
fun NPCDetails(component: INPCDetails) = Column {
	val npc = remember { component.npc }
	if (npc.banner > 0) Text("Banner: ${npc.banner} @ ${npc.killsPerBanner}", Modifier.clickable { component.onItemClicked(npc.banner) })
	LootView(npc.drops, onItemClicked = component::onItemClicked)
}
