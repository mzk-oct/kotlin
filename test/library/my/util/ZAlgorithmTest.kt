package library.my.util

import library.my.string.ZAlgorithm
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.streams.asStream

internal class ZAlgorithmTest {
    object Oracle {
        fun zAlgorithm(str: String): IntArray {
            return IntArray(str.length){i ->
                var len = 0
                while (i + len < str.length && str[len] == str[i + len]) {
                    ++len
                }
                len
            }
        }
    }
    private fun same(a: IntArray, b: IntArray): Boolean {
        return a.contentEquals(b)
    }
    @ParameterizedTest
    @MethodSource("stringSource0", "stringSourceSmall", "stringSource")
    fun test(str: String) {
        val actual = ZAlgorithm.zAlgorithm(str)
        val expected = Oracle.zAlgorithm(str)
        assert(same(actual, expected)){
            "$str\n" +
                    "expected: ${expected.joinToString(", ")}\n" +
                    "actual: ${actual.joinToString(", ")}\n"
        }
    }
    companion object {
        val seed = 2
        @JvmStatic
        fun stringSource0(): Stream<String> {
            return sequence {
                repeat(100){
                    yield("0".repeat(it))
                }
            }.asStream()
        }
        @JvmStatic
        fun stringSourceSmall(): Stream<String> {
            val random = Random(seed)
            val set = '0' .. '1'
            val lenRange = 10 .. 30
            return sequence {
                repeat(10000){
                    val length = random.nextInt(lenRange)
                    val str  = StringBuilder(length).let { sb ->
                        repeat(length) {
                            sb.append(set.random(random))
                        }
                        sb.toString()
                    }
                    yield(str)
                }
            }.asStream()
        }
        @JvmStatic
        fun stringSource(): Stream<String> {
            val random = Random(seed)
            val set = '0' .. '3'
            val lenRange = 1 .. 300
            return sequence {
                repeat(10000){
                    val length = random.nextInt(lenRange)
                    val str  = StringBuilder(length).let { sb ->
                        repeat(length) {
                            sb.append(set.random(random))
                        }
                        sb.toString()
                    }
                    yield(str)
                }
            }.asStream()
        }
    }
}