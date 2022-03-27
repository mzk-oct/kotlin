package library.my.number

import org.junit.jupiter.api.Test
import kotlin.math.sqrt
import kotlin.test.assertEquals

internal class MillerRabinTest {
    fun calcPrimes(upper: Int): BooleanArray {
        val isPrime = BooleanArray(upper + 1){true}
        isPrime[0] = false
        isPrime[1] = false
        for (i in 2 .. sqrt(upper.toDouble()).toInt()) {
            if (isPrime[i]) {
                for (j in i * i .. upper step i) {
                    isPrime[j] = false
                }
            }
        }
        return isPrime
    }
    @Test
    fun testPrime() {
        val isPrime = calcPrimes(30_000_000)
        for (i in isPrime.indices) {
            assertEquals(isPrime[i], MillerRabin.checkPrime(i), i.toString())
        }
    }
}