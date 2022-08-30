package minerofmillions.recipeapp.ui.util

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScrollableColumn(modifier: Modifier = Modifier, content: LazyListScope.() -> Unit) {
	val state = rememberLazyListState()
	Row(modifier = modifier) {
		LazyColumn(modifier = Modifier.weight(1f), state = state, content = content)
		VerticalScrollbar(rememberScrollbarAdapter(state))
	}
}

@Composable
fun ScrollableRow(modifier: Modifier = Modifier, content: LazyListScope.() -> Unit) {
	val state = rememberLazyListState()
	Column(modifier = modifier) {
		LazyRow(modifier = Modifier.weight(1f), state = state, content = content)
		HorizontalScrollbar(rememberScrollbarAdapter(state))
	}
}
