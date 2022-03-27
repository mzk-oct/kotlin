package library

import java.math.BigInteger
import kotlin.math.absoluteValue
import kotlin.math.sign


data class Rational(val numerator: BigInteger, val denominator: BigInteger): Comparable<Rational> {
    operator fun plus(other: Rational): Rational {
        return Rational.make(numerator * other.denominator + denominator * other.numerator, denominator * other.denominator)
    }
    operator fun minus(other: Rational): Rational {
        return Rational.make(numerator * other.denominator - denominator * other.numerator, denominator * other.denominator)
    }
    operator fun times(other: Rational): Rational {
        return Rational.make(numerator * other.numerator, denominator * other.denominator)
    }
    operator fun div(other: Rational): Rational {
        return Rational.make(numerator * other.denominator, denominator * other.numerator)
    }
    override fun compareTo(other: Rational): Int {
        return (this - other).numerator.compareTo(BigInteger.ZERO)
    }
    companion object {
        fun make(numerator: Long, denominator: Long): Rational {
            val n = numerator.absoluteValue.toBigInteger()
            val d = denominator.absoluteValue.toBigInteger()
            val g = n.gcd(d)
            return Rational(n / g * (numerator.sign * denominator.sign).toBigInteger(), d / g)
        }
        fun make(numerator: BigInteger, denominator: BigInteger): Rational {
            val n = numerator.abs()
            val d = denominator.abs()
            val g = n.gcd(d)
            return Rational(n / g * (numerator.signum() * denominator.signum()).toBigInteger(), d / g)
        }
    }
}