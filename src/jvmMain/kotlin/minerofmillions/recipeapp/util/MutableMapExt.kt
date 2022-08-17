package minerofmillions.recipeapp.util

import org.ojalgo.scalar.RationalNumber

fun <K> MutableMap<K, Int>.add(key: K, value: Int) {
	this[key] = getOrDefault(key, 0) + value
}

fun <K> MutableMap<K, Long>.add(key: K, value: Long) {
	this[key] = getOrDefault(key, 0L) + value
}

fun <K> MutableMap<K, Float>.add(key: K, value: Float) {
	this[key] = getOrDefault(key, 0f) + value
}

fun <K> MutableMap<K, Double>.add(key: K, value: Double) {
	this[key] = getOrDefault(key, 0.0) + value
}

fun <K> MutableMap<K, RationalNumber>.add(key: K, value: RationalNumber) {
	this[key] = getOrDefault(key, RationalNumber.ZERO) + value
}
