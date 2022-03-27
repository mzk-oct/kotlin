package library.my

import kotlin.math.absoluteValue
import kotlin.math.sign
class Rational private constructor(val numerator: Long, val denominator: Long): Comparable<Rational> {
    operator fun plus(other: Rational): Rational {
        return invoke(numerator * other.denominator + denominator * other.numerator, denominator * other.denominator)
    }
    operator fun minus(other: Rational): Rational {
        return invoke(numerator * other.denominator - denominator * other.numerator, denominator * other.denominator)
    }
    operator fun times(other: Rational): Rational {
        return invoke(numerator * other.numerator, denominator * other.denominator)
    }
    operator fun div(other: Rational): Rational {
        return invoke(numerator * other.denominator, denominator * other.numerator)
    }
    operator fun unaryMinus(): Rational {
        return Rational(-numerator, denominator)
    }
    override fun compareTo(other: Rational): Int {
        return (this - other).numerator.sign
    }
    fun toDouble(): Double {
        return numerator.toDouble() / denominator
    }
    override fun toString(): String {
        return "$numerator / $denominator"
    }
    operator fun component1(): Long = numerator
    operator fun component2(): Long = denominator
    companion object {
        private tailrec fun gcd(a: Long, b: Long): Long {
            return when(b){
                0L -> a
                else -> gcd(b, a % b)
            }
        }
        operator fun invoke(num: Long, den: Long): Rational {
            val g = gcd(num.absoluteValue, den.absoluteValue)
            val n = num.absoluteValue / g
            val d = den.absoluteValue / g
            return Rational(numerator = n * num.sign * d.sign, denominator = d)
        }
        operator fun invoke(num: Int, den: Int): Rational = Rational(num.toLong(), den.toLong())
    }
}