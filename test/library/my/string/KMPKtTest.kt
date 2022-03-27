package library.my.string

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.streams.asStream

internal class KMPKtTest {


    @ParameterizedTest
    @MethodSource
    fun test(target: String, pattern: String) {
        val eTable = expectedTable(pattern)
        val aTable  = actualTable(pattern)
        assert(eTable.contentEquals(aTable)) {
            System.err.println(eTable.joinToString(" "))
            System.err.println(aTable.joinToString(" "))
        }
        val eMatch = expectedMatch(target, pattern)
        val aMatch = actualMatch(target, pattern)
        assert(eMatch.toIntArray().contentEquals(aMatch.toIntArray())) {
            System.err.println(eMatch)
            System.err.println(aMatch)
        }
        System.err.println(eMatch.size)
    }
    fun expectedTable(pattern: String): IntArray {
        val result = IntArray(pattern.length + 1)
        for (i in 0 .. pattern.length) {
            val suffix = pattern.substring(0 until i)
            result[i] = -1
            for (j in 0 until i) {
                if (suffix.endsWith(pattern.substring(0 until j)) && pattern.getOrNull(i) != pattern[j]) {
                    result[i] = j
                }
            }
        }
        return result
    }
    fun actualTable(pattern: String): IntArray {
        return makeTable(pattern)
    }
    fun expectedMatch(target: String, pattern: String): List<Int> {
        val result = mutableListOf<Int>()
        for (i in 0 .. target.length - pattern.length) {
            if (pattern == target.substring(i until i + pattern.length)) {
                result.add(i)
            }
        }
        return result
    }
    fun actualMatch(target: String, pattern: String): List<Int> {
        val table = makeTable(pattern)
        return findMatch(target, pattern, table)
    }
    companion object {
        const val SEED = 0
        @JvmStatic
        fun test(): Stream<Arguments> {
            val random = Random(SEED)
            return sequence {
                yield(Arguments.of("ABAABABAABBA", "ABAABABAABBA"))
                repeat(10000) {
                    val targetSeed = random.nextInt()
                    val patternSeed = random.nextInt()
                    val targetSize = random.nextInt(300 .. 1000)
                    val patternSize = random.nextInt(3 .. 10)
                    val target = makeRandom(targetSeed, targetSize)
                    val pattern = makeRandom(patternSeed, patternSize)
                    yield(Arguments.of(target, pattern))
                }
            }.asStream()
        }
        fun makeRandom(seed: Int, length: Int): String {
            val sb = StringBuilder(length)
            val random = Random(seed)
            repeat(length) {
                sb.append(('A' .. 'B').random(random))
            }
            return sb.toString()
        }
    }
}