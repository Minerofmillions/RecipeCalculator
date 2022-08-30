package minerofmillions.recipeapp.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun <E> Selector(
	elements: List<E>,
	selected: E?,
	onSelect: (E) -> Unit,
	filterMatches: (E, filter: String) -> Boolean,
	modifier: Modifier = Modifier,
	elementLayout: @Composable (E) -> Unit,
) {
	var filter by remember { mutableStateOf("") }
	val filteredElements by remember { derivedStateOf { elements.filter { filterMatches(it, filter) } } }
	Column(modifier) {
		TextField(value = filter, onValueChange = {
			filter = it
			if (filteredElements.size == 1) onSelect(filteredElements.first())
		})
		ScrollableColumn {
			items(filteredElements) {
				Box(Modifier.clickable { onSelect(it) }
					.background(if (it == selected) MaterialTheme.colors.primary else MaterialTheme.colors.background)) {
					elementLayout(it)
				}
			}
		}
	}
}
