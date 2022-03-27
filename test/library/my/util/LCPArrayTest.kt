package library.my.util

import library.my.string.LCPArray
import library.my.string.SuffixArray
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.streams.asStream

internal class LCPArrayTest {
    object Oracle {
        fun lcpArray(str: String, suffixArray: IntArray): IntArray {
            return (0 until suffixArray.lastIndex).map{idx ->
                val i = suffixArray[idx]
                val j = suffixArray[idx + 1]
                var len = 0
                while (i + len < str.length && j + len < str.length && str[i + len] == str[j + len]) {
                    ++len
                }
                len
            }.toIntArray()
        }
    }
    private fun same(a: IntArray, b: IntArray): Boolean {
        return a.size == b.size && a.indices.all{a[it] == b[it]}
    }
    @ParameterizedTest
    @MethodSource("lcpSource1", "lcpSource2")
    fun lcpTest(str: String) {
        val sa = SuffixArray.suffixArray(str)
        val expected = Oracle.lcpArray(str, sa)
        val actual = LCPArray.lcpArrayKasai(str, sa)
        assert(same(expected, actual)) {
            "str: $str\n" +
                    "expected: ${expected.joinToString(", ")}\n" +
                    "actual: ${actual.joinToString(", ")}"
        }
    }
    companion object {
        @JvmStatic
        fun lcpSource1(): Stream<String> {
            val random = Random(1)
            val charSet = '0' .. '1'
            val sizeRange = 1 .. 300
            return sequence {
                repeat(1000) {
                    val size = random.nextInt(sizeRange)
                    val str = StringBuilder(size).let { sb ->
                        repeat(size){
                            sb.append(charSet.random(random))
                        }
                        sb.toString()
                    }
                    yield(str)
                }
            }.asStream()
        }
        @JvmStatic
        fun lcpSource2(): Stream<String> {
            val random = Random(0)
            val charSet = '0' .. '9'
            val sizeRange = 1 .. 300
            return sequence {
                repeat(1000) {
                    val size = random.nextInt(sizeRange)
                    val str = StringBuilder(size).let { sb ->
                        repeat(size){
                            sb.append(charSet.random(random))
                        }
                        sb.toString()
                    }
                    yield(str)
                }
            }.asStream()
        }
        @JvmStatic
        fun lcpTest(): Stream<String> {
            val random = Random(0)
            val charSet = '0' .. '1'
            val sizeRange = 1 .. 300
            return sequence {
                repeat(1000) {
                    val size = random.nextInt(sizeRange)
                    val str = StringBuilder(size).let { sb ->
                        repeat(size){
                            sb.append(charSet.random(random))
                        }
                        sb.toString()
                    }
                    yield(str)
                }
            }.asStream()
        }
    }
}