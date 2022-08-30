package minerofmillions.recipeapp.ui.util

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.google.common.collect.Table
import minerofmillions.recipeapp.entities.saver.Loot
import minerofmillions.recipeapp.entities.saver.LootTable
import minerofmillions.recipeapp.util.toString
import org.checkerframework.checker.units.qual.C

@Composable
fun LootView(loot: Loot, modifier: Modifier = Modifier, onItemClicked: (Int) -> Unit = {}) {
	ScrollableColumn(modifier.fillMaxHeight()) {
		val tableModifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
		if (loot.hasNormalLoot) item {
			LootTableView(
				remember { loot.getNormalLootTable() },
				onItemClicked = onItemClicked,
				labelLabel = "Normal loot",
				modifier = tableModifier
			)
		}
		if (loot.hasExpertLoot) item {
			LootTableView(
				remember { loot.getExpertLootTable() },
				onItemClicked = onItemClicked,
				labelLabel = "Expert loot",
				modifier = tableModifier
			)
		}
		if (loot.hasMasterLoot) item {
			LootTableView(
				remember { loot.getMasterLootTable() },
				onItemClicked = onItemClicked,
				labelLabel = "Master loot",
				modifier = tableModifier
			)
		}
	}
}

@Composable
fun LootTableView(
	lootTable: LootTable,
	modifier: Modifier = Modifier,
	labelLabel: String = "ItemIDs",
	onItemClicked: (Int) -> Unit,
) {
	TableView(
		table = lootTable,
		modifier = modifier,
		onItemClicked = { item: Int, _: String -> onItemClicked(item) },
		columnLabel = { it.ifBlank { "Default" } },
		valueLabel = { it.toString(digits = 3) },
		labelLabel = labelLabel
	)
}

@Composable
fun <R : Comparable<R>, C : Comparable<C>, V> TableView(
	table: Table<R, C, V>,
	modifier: Modifier = Modifier,
	onItemClicked: (R, C) -> Unit = { _, _ -> },
	rowLabel: (R) -> String = { it.toString() },
	columnLabel: (C) -> String = { it.toString() },
	valueLabel: (V) -> String = { it.toString() },
	labelLabel: String = "",
) {
	val sortedRows = remember { table.rowKeySet().filterNotNull().sorted() }
	val sortedColumns = remember { table.columnKeySet().filterNotNull().sorted() }
	
	val borderModifier = Modifier.fillMaxSize().border(1.dp, MaterialTheme.colors.primaryVariant).padding(2.dp)
	
	val columnHeights = remember { mutableStateMapOf<C?, Int>() }
	fun Modifier.getColumnHeight(column: C?): Modifier = padding(2.dp).layout { measurable, constraints ->
		val placable = measurable.measure(constraints)
		
		val existingHeight = columnHeights[column] ?: 0
		val maxHeight = maxOf(placable.height, existingHeight)
		
		if (maxHeight > existingHeight) columnHeights[column] = maxHeight
		
		layout(width = placable.width, height = maxHeight) {
			placable.placeRelative(0, 0)
		}
	}
	
	LazyRow(modifier) {
		item {
			Column(Modifier.width(IntrinsicSize.Min)) {
				Text(labelLabel, borderModifier.getColumnHeight(null))
				sortedColumns.forEach {
					Text(columnLabel(it), borderModifier.getColumnHeight(it))
				}
			}
		}
		items(sortedRows) { row ->
			Column(Modifier.width(IntrinsicSize.Min)) {
				Text(rowLabel(row), borderModifier.getColumnHeight(null))
				sortedColumns.forEach {
					val value = table[row, it]
					Text(
						if (value == null) "" else valueLabel(value),
						borderModifier.clickable { onItemClicked(row, it) }.getColumnHeight(it)
					)
				}
			}
		}
	}
}
