package minerofmillions.recipeapp.util

fun <K, V> Map<K, V?>.filterValuesNotNull(): Map<K, V> = filterValues { it != null } as Map<K, V>

fun <K, V> Map<K, List<V>>.permutations(): Sequence<Map<K, V>> =
	if (isEmpty()) sequenceOf(emptyMap()) else entries.first().let { (firstKey, firstValue) ->
		entries.drop(1).toMap().permutations().flatMap { permutation ->
			firstValue.map { mapOf(firstKey to it) + permutation }
		}
	}

fun <K, V> Map<K, MutableCollection<V>>.removeFromAll(element: V) = values.forEach { it.remove(element) }

fun <K, V1, V2> Map<K, V1>.zip(other: Map<K, V2>): Sequence<Triple<K, V1, V2>> =
	keys.asSequence().filter(other::contains).map { Triple(it, get(it)!!, other[it]!!) }

fun <K, V1, V2> Map<K, V1>.zipIncludeUnmatched(other: Map<K, V2>): Sequence<Triple<K, V1?, V2?>> =
	(keys + other.keys).asSequence().map { Triple(it, get(it), other[it]) }
