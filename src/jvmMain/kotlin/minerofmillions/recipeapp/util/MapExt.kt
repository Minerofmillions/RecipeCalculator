package minerofmillions.recipeapp.util

fun <K, V> Map<K, V?>.filterValuesNotNull(): Map<K, V> = filterValues { it != null } as Map<K, V>

fun <K, V> Map<K, List<V>>.permutations(): Sequence<Map<K, V>> =
	if (isEmpty()) sequenceOf(emptyMap()) else entries.first().let { (firstKey, firstValue) ->
		entries.drop(1).toMap().permutations().flatMap { permutation ->
			firstValue.map { mapOf(firstKey to it) + permutation }
		}
	}
