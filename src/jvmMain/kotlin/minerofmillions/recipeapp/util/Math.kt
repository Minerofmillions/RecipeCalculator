package minerofmillions.recipeapp.util

import minerofmillions.recipeapp.data.Rational
import java.math.BigDecimal
import kotlin.math.abs

private const val epsilon = 1e-10

fun gcd(a: Int, b: Int): Int = if (a == 0) b else gcd(b % a, a)
fun gcd(a: Long, b: Long): Long = if (a == 0L) b else gcd(b % a, a)

fun sign(n: Int) = when {
	n > 0 -> 1
	n < 0 -> -1
	else -> 0
}

fun sign(n: Long) = when {
	n > 0 -> 1
	n < 0 -> -1
	else -> 0
}

fun BigDecimal.toRational() = Rational.of(unscaledValue().toLong()) *
		if (scale() < 0) (1 .. -scale()).fold(Rational.ONE) { acc, _ -> acc * 10 }
		else (1 .. scale()).fold(Rational.ONE) { acc, _ -> acc / 10 }

fun Double.approximatelyEquals(n: Int) = abs(minus(n)) < epsilon
fun Double.approximatelyEquals(n: Long) = abs(minus(n)) < epsilon
fun Double.approximatelyEquals(n: Double) = abs(minus(n)) < epsilon
