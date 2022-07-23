package minerofmillions.recipeapp.util

fun <R, C, V> Map<R, Map<C, V>>.mergeOnto(other: Map<R, Map<C, V>>): Map<R, Map<C, V>> =
	(keys + other.keys).associateWith { r ->
		((this[r]?.keys ?: emptySet()) + (other[r]?.keys ?: emptySet())).associateWith { c ->
			this[r]?.get(c) ?: other[r]?.get(c)!!
		}
	}

fun <K, V, R : Any> Map<K, V>.mapValuesNotNull(transform: (Map.Entry<K, V>) -> R?): Map<K, R> =
	entries.mapNotNull { entry -> (entry.key to transform(entry)).takeIf { it.second != null } }
		.associate { it.first to it.second!! }
