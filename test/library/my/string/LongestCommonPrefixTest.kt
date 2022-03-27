package library.my.string

import library.my.string.LongestCommonPrefix.longestCommonPrefixArray
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.streams.asStream

internal class LongestCommonPrefixTest {
    @ParameterizedTest
    @MethodSource
    fun test(seed: Int) {
        val random = Random(seed)
        val objective = List(random.nextInt(1 .. 128)){random.nextInt(5)}.joinToString("")
        val pattern = List(random.nextInt(1 .. 128)){random.nextInt(5)}.joinToString("")
        val expected = objective.longestCommonPrefixImpl(pattern)
        val actual = objective.longestCommonPrefixArray(pattern)
        assert(actual.contentEquals(expected))
    }
    fun maxMatch(a: String, b: String): Int {
        val len = minOf(a.length, b.length)
        for (i in 0 until len) {
            if (a[i] != b[i]) return i
        }
        return len
    }
    fun lcpImpl(str: String): IntArray {
        return str.indices.map { maxMatch(str, str.substring(it)) }.toIntArray()
    }
    fun String.longestCommonPrefixImpl(target: String): IntArray {
        return indices.map { maxMatch(substring(it), target) }.toIntArray()
    }
    companion object {
        const val SEED = 0
        const val ITERATION = 10000
        @JvmStatic
        fun test(): Stream<Int> {
            val random = Random(SEED)
            return sequence {
                repeat(ITERATION) {
                    yield(random.nextInt())
                }
            }.asStream()
        }
    }
}