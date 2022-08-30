package minerofmillions.recipeapp.util

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table

fun <R, C, V> Map<R, Map<C, V?>>.toTable(): Table<R, C, V> = HashBasedTable.create<R, C, V>().apply {
	forEach { (row, columnValues) ->
		columnValues.forEach { (column, value) ->
			if (value != null) put(row, column, value)
		}
	}
}

operator fun <R, C, V> Table<R, C, V>.plus(other: Table<R, C, V>): Table<R, C, V> =
	(rowKeySet() + other.rowKeySet()).associateWith { row ->
		(columnKeySet() + other.columnKeySet()).associateWith { column ->
			get(row, column) ?: other.get(row, column)
		}
	}.toTable()
