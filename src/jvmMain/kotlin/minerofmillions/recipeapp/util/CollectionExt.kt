package minerofmillions.recipeapp.util

fun <E> Collection<Collection<E>>.permutations(): Sequence<List<E>> = if (isEmpty()) sequenceOf(emptyList())
else first().let { first ->
	drop(1).permutations().flatMap { permutation ->
		first.map { listOf(it) + permutation }
	}
}

fun <K, V> Collection<Map.Entry<K, V>>.toMap() = associate { it.key to it.value }

fun <E> Collection<E>.contentsEqual(other: Collection<E>, ignoreOrder: Boolean = false) =
	size == other.size && if (ignoreOrder) all(other::contains) && other.all(this::contains) else zip(other).all { it.first == it.second }
