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
import androidx.compose.ui.Modifier
import minerofmillions.recipeapp.components.IItemDetails
import minerofmillions.recipeapp.components.IItemList
import minerofmillions.recipeapp.entities.saver.Item
import minerofmillions.recipeapp.ui.util.LootView
import minerofmillions.recipeapp.ui.util.ScrollableColumn
import minerofmillions.recipeapp.ui.util.ScrollableRow
import minerofmillions.recipeapp.util.of

@Composable
fun ItemList(component: IItemList) {
	ScrollableColumn {
		items(component.itemList) {
			ItemBrief(it, Modifier.clickable { component.onItemClicked(it) })
		}
	}
}

@Composable
fun ItemBrief(item: Item, modifier: Modifier = Modifier) {
	Row(modifier.fillMaxWidth()) {
		Text(item.type.toString(), style = MaterialTheme.typography.body2)
		Text(item.namespacedName)
	}
}

@Composable
fun ItemDetails(component: IItemDetails) {
	val item = remember { component.item }
	Column {
		Text(item.tooltip.trim())
		if (item.bagItems.isNotEmpty) LootView(
			item.bagItems, Modifier.weight(1f), onItemClicked = component::onItemClicked
		)
		if (item.extractinatorItems.isNotEmpty()) ScrollableRow(
			Modifier.weight(1f)) {
			items(item.extractinatorItems.entries.toList()) { (item, count) ->
				Column(Modifier.clickable { component.onItemClicked(item) }) {
					Text(item.toString())
					Text(of(count, Item.extractinatorTests).toString())
				}
			}
		}
	}
}
