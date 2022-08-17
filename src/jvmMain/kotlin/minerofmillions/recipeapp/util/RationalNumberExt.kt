package minerofmillions.recipeapp.util

import org.ojalgo.scalar.RationalNumber
import java.math.BigDecimal

private val EPSILON = of(1, 1000000)

fun of(n: Int, d: Int = 1): RationalNumber = RationalNumber.of(n.toLong(), d.toLong())
fun of(n: Long): RationalNumber = RationalNumber.of(n, 1L)

fun of(s: String): RationalNumber = if ('/' in s) s.split('/', limit = 2).let { (n, d) ->
	RationalNumber.valueOf(n) / RationalNumber.valueOf(d)
} else RationalNumber.valueOf(BigDecimal(s))

operator fun RationalNumber.unaryMinus(): RationalNumber = this.negate()

operator fun RationalNumber.plus(other: RationalNumber): RationalNumber = add(other)
operator fun RationalNumber.minus(other: RationalNumber): RationalNumber = subtract(other)
operator fun RationalNumber.times(other: RationalNumber): RationalNumber = multiply(other)
operator fun RationalNumber.div(other: RationalNumber): RationalNumber = divide(other)

operator fun RationalNumber.plus(other: Int): RationalNumber = add(of(other))
operator fun RationalNumber.minus(other: Int): RationalNumber = subtract(of(other))
operator fun RationalNumber.times(other: Int): RationalNumber = multiply(of(other))
operator fun RationalNumber.div(other: Int): RationalNumber = divide(of(other))

operator fun RationalNumber.plus(other: Long): RationalNumber = add(of(other))
operator fun RationalNumber.minus(other: Long): RationalNumber = subtract(of(other))
operator fun RationalNumber.times(other: Long): RationalNumber = multiply(of(other))
operator fun RationalNumber.div(other: Long): RationalNumber = divide(of(other))

fun RationalNumber.aboutZero() = -this <= EPSILON && EPSILON <= this

fun Collection<RationalNumber>.sumOf() = fold(RationalNumber.ZERO, RationalNumber::add)
fun <T> Collection<T>.sumOf(selector: (T) -> RationalNumber): RationalNumber =
	fold(RationalNumber.ZERO) { a, b -> a + selector(b) }

operator fun RationalNumber.rem(n: Int): RationalNumber = RationalNumber.valueOf(toBigDecimal() % BigDecimal.valueOf(n.toLong()))
operator fun RationalNumber.rem(n: Long): RationalNumber = RationalNumber.valueOf(toBigDecimal() % BigDecimal.valueOf(n))
operator fun RationalNumber.rem(n: RationalNumber): RationalNumber = RationalNumber.valueOf(toBigDecimal() % n.toBigDecimal())

fun RationalNumber.floor() = of(longValue())
fun RationalNumber.ceil() = floor() + if (rem(1) > RationalNumber.ZERO) 1 else 0
