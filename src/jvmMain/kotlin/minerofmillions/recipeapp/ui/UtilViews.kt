package minerofmillions.recipeapp.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> ListView(
	list: List<T>,
	onItemClick: (T) -> Unit = {},
	modifier: Modifier = Modifier,
	content: @Composable (T) -> Unit,
) {
	Row(modifier) {
		val state = rememberLazyListState()
		LazyColumn(Modifier.weight(1f), state) {
			items(list) {
				Box(Modifier.clickable { onItemClick(it) }) { content(it) }
			}
		}
		VerticalScrollbar(rememberScrollbarAdapter(state), modifier.fillMaxHeight())
	}
}
