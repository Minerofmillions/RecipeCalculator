package minerofmillions.recipeapp.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

fun LazyListScope.itemsChunked(count: Int, chunkSize: Int, itemContent: @Composable (Int) -> Unit) {
	items(count / chunkSize + if (count % chunkSize == 0) 0 else 1) {
		Row {
			for (i in 0 until chunkSize) {
				val index = it * chunkSize + i
				if (index < count) {
					Box(Modifier.weight(1f)) { itemContent(index) }
				} else Text("", Modifier.weight(1f))
			}
		}
	}
}

inline fun <E> LazyListScope.itemsChunked(items: List<E>, chunkSize: Int, crossinline itemContent: @Composable (E) -> Unit) =
	itemsChunked(items.size, chunkSize) { itemContent(items[it]) }

inline fun <E> LazyListScope.itemsChunked(items: Array<E>, chunkSize: Int, crossinline itemContent: @Composable (E) -> Unit) =
	itemsChunked(items.count(), chunkSize) { itemContent(items[it]) }
