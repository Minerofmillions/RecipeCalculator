package minerofmillions.recipeapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ListView(
	list: List<T>,
	onItemShortClick: (T) -> Unit = {},
	onItemLongClick: (T) -> Unit = {},
	modifier: Modifier = Modifier,
	content: @Composable (T) -> Unit
) {
	Row(modifier) {
		val state = rememberLazyListState()
		LazyColumn(Modifier.weight(1f), state) {
			items(list) {
				Box(Modifier.combinedClickable(onLongClick = { onItemLongClick(it) }, onClick = { onItemShortClick(it) })) { content(it) }
			}
		}
		VerticalScrollbar(rememberScrollbarAdapter(state), modifier.fillMaxHeight())
	}
}

@Composable
fun <T> FilteredListView(
	list: List<T>,
	filter: (T) -> Boolean,
	onItemShortClick: (T) -> Unit = {},
	onItemLongClick: (T) -> Unit = {},
	modifier: Modifier = Modifier,
	content: @Composable (T) -> Unit,
) {
	val filteredList by derivedStateOf { list.filter(filter) }
	ListView(filteredList, onItemShortClick, onItemLongClick, modifier, content)
}
