package minerofmillions.recipeapp.util

fun <E> Collection<Collection<E>>.permutations(): Sequence<List<E>> = if (isEmpty()) sequenceOf(emptyList())
else first().let { first ->
	drop(1).permutations().flatMap { permutation ->
		first.map { listOf(it) + permutation }
	}
}

fun <K, V> Collection<Map.Entry<K, V>>.toMap() = associate { it.key to it.value }
