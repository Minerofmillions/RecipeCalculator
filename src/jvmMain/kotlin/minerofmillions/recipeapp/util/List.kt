package minerofmillions.recipeapp.util

import kotlin.collections.plus as originalPlus

infix operator fun <T: Comparable<T>> List<T>.compareTo(other: List<T>) =
	when {
		this.size != other.size -> this.size - other.size
		else -> this.zip(other).map { it.first compareTo it.second }.firstOrNull { it != 0 } ?: 0
	}

fun <E> List<List<E>>.permutations(): Sequence<List<E>> = if (isEmpty()) sequenceOf(emptyList())
else dropLast(1).permutations().flatMap { p -> last().map(p::originalPlus) }

fun <E> E.plus(list: Iterable<E>): List<E> = listOf(this).originalPlus(list)

fun <E> List<E>.contentsEqualOrderless(other: List<E>) = this.size == other.size && all(other::contains) && other.all(this::contains)
