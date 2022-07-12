package minerofmillions.recipeapp.data

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import minerofmillions.recipeapp.util.approximatelyEquals
import minerofmillions.recipeapp.util.gcd
import minerofmillions.recipeapp.util.sign
import minerofmillions.recipeapp.util.toRational
import java.lang.reflect.Type
import java.math.BigDecimal
import kotlin.math.abs
import kotlin.math.pow

class Rational private constructor(val p: Long, val q: Long) : Comparable<Rational> {
    operator fun plus(n: Int) = of(p + n * q, q)
    operator fun plus(n: Long) = of(p + n * q, q)
    operator fun plus(n: Rational) = of(p * n.q + n.p * q, q * n.q)

    operator fun minus(n: Int) = of(p - n * q, q)
    operator fun minus(n: Long) = of(p - n * q, q)
    operator fun minus(n: Rational) = of(p * n.q - n.p * q, q * n.q)

    operator fun times(n: Int) = of(p * n, q)
    operator fun times(n: Long) = of(p * n, q)
    operator fun times(n: Rational) = of(p * n.p, q * n.q)

    operator fun div(n: Int) = of(p, q * n)
    operator fun div(n: Long) = of(p, q * n)
    operator fun div(n: Rational) = of(p * n.q, q * n.p)

    operator fun unaryMinus() = Rational(-p, q)
    fun reciprocal() = Rational(sign(p) * q, abs(p))
    fun abs() = Rational(abs(p), q)

    fun floor() = Rational(p / q, 1)
    fun ceil() = if (q == 1L) this else Rational(p / q + 1, 1)

    fun pow(n: Int) = of(p.toDouble().pow(n).toLong(), q.toDouble().pow(n).toLong())

    fun toDouble() = p.toDouble() / q.toDouble()

    fun approximatelyEquals(n: Int) = toDouble().approximatelyEquals(n)
    fun approximatelyEquals(n: Long) = toDouble().approximatelyEquals(n)
    fun approximatelyEquals(n: Double) = toDouble().approximatelyEquals(n)

    override fun equals(other: Any?): Boolean = when {
        other !is Rational -> false
        p == other.p && q == other.q -> true
        else -> false
    }

    override fun toString() = if (q != 1L) "$p/$q" else "$p"
    fun toString(digits: Int) = "%.${digits}f".format(toDouble())

    override fun compareTo(other: Rational): Int = when {
        p == other.p -> other.q.compareTo(q)
        q == other.q -> p.compareTo(other.p)
        else -> (toDouble().compareTo(other.toDouble()))
    }
    operator fun compareTo(other: Int) = compareTo(of(other))

    override fun hashCode(): Int {
        var result = p.hashCode()
        result = 31 * result + q.hashCode()
        return result
    }
    
    object Serializer : JsonDeserializer<Rational> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Rational {
            if (json.isJsonPrimitive) return Rational.parse(json.asString)
            return json.asJsonObject.let {
                if (it.has("p") && it.has("q")) Rational.of(it["p"].asLong, it["q"].asLong)
                else Rational.parse(it["value"].asString)
            }
        }
    }

    companion object {
        val ZERO = Rational(0L, 1L)
        val ONE = Rational(1L, 1L)

        fun of(p: Long, q: Long = 1L): Rational = when {
            q == 0L -> error("Cannot divide by zero.")
            p == 0L -> ZERO
            q < 0L -> of(-p, -q)
            else -> gcd(abs(p), abs(q)).let { Rational(p / it, q / it) }
        }
        fun of(p: Int, q: Int = 1) = of(p.toLong(), q.toLong())

        fun milli(p: Long, q: Long = 1L): Rational = of(p, q * 1000)
        fun milli(p: Int, q: Int = 1): Rational = of(p, q * 1000)

        fun percent(p: Long, q: Long = 1L): Rational = of(p, q * 100)
        fun percent(p: Int, q: Int = 1): Rational = of(p, q * 100)

        fun between(lower: Long, upper: Long): Rational = of(lower + upper, 2)
        fun between(lower: Int, upper: Int): Rational = of(lower + upper, 2)

        fun parse(string: String): Rational =
            if (!isValid(string)) error("Invalid rational: \"$string\"")
            else if ('/' !in string) BigDecimal(string).toRational()
            else string.split('/').let { (p, q) ->
                BigDecimal(p).toRational() / BigDecimal(q).toRational()
            }

        fun isValid(string: String): Boolean =
            string.count { it == '/' } < 2 &&
                    string.split('/').all {
                        try {
                            BigDecimal(it)
                            true
                        } catch (e: NumberFormatException) {
                            false
                        }
                    }
    }
}

operator fun Int.times(n: Rational): Rational = n * this


fun Collection<Rational>.sum() = fold(Rational.ZERO, Rational::plus)
fun <E> Collection<E>.sumOf(transform: (E) -> Rational) = fold(Rational.ZERO) { acc, e -> acc + transform(e) }

fun <E> MutableMap<E, Rational>.add(key: E, amount: Rational) = put(key, getOrDefault(key, Rational.ZERO) + amount)
