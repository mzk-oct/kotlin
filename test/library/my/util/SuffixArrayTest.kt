package library.my.util

import library.my.string.SuffixArray
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.streams.asStream

internal class SuffixArrayTest {
    object Oracle {
        fun suffixArray(str: String): IntArray {
            val result = MutableList(str.length){it}
            val substrings = str.indices.map(str::substring)
            result.sortBy(substrings::get)
            return result.toIntArray()
        }
    }
    fun same(a: IntArray, b: IntArray): Boolean {
        return a.size == b.size && a.contentEquals(b)
    }
    @ParameterizedTest
    @MethodSource
    fun suffixArrayTest(str: String) {
        val sa1 = SuffixArray.suffixArray(str)
        val sa2 = Oracle.suffixArray(str)
        assert(same(sa1, sa2)){
            "for: ${str}\n" +
            "actual: ${sa1.joinToString(", ")}\n" +
            "expected: ${sa2.joinToString(", ")}"
        }
    }
    companion object {
        @JvmStatic
        fun suffixArrayTest(): Stream<String> {
            val random = Random(3)
            val sizeRange = 1 until 200
            //val charSet = ('a' .. 'z') + ('A' .. 'Z') + ('0' .. '9')
            val charSet = ('0' .. '4')
            return sequence<String> {
                repeat(10000) {
                    val size = random.nextInt(sizeRange)
                    val str = StringBuilder(size).let{builder ->
                        repeat(size) {
                            builder.append(charSet.random(random))
                        }
                        builder.toString()
                    }
                    yield(str)
                }
            }.asStream()
        }
    }
}