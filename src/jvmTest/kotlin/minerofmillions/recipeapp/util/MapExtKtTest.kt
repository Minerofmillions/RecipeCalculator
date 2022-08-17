package minerofmillions.recipeapp.util

fun main() {
	val map = mapOf(
			"1" to listOf("a"),
			"2" to listOf("d"),
			"3" to listOf("g")
	)
	
	val permutations = map.permutations().toList()
	permutations.forEach(::println)
	println(permutations.size)
}
