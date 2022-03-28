package library.my.number

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.streams.asStream

const val MOD1 = 167772161L
const val MOD2 = 469762049L
internal class ConvolutionTest {
    @ParameterizedTest
    @MethodSource
    fun convolution(listA: IntArray, listB: IntArray, mod: Long) {
        val expected = brute(listA, listB, mod)
        val actual = ConvolutionRaw(mod.toInt()).convolution(listA.map{it.toLong()}.toLongArray(), listB.map{it.toLong()}.toLongArray())
        assert(expected.contentEquals(actual)) {
            "expected: ${expected.joinToString(", ")}\n"+
                    "actual: ${actual.joinToString(", ")}"

        }
    }

    fun brute(listA: IntArray, listB: IntArray, mod: Long): LongArray {
        val result = LongArray(listA.size + listB.size - 1)
        for (i in listA.indices) {
            for (j in listB.indices) {
                result[i + j] += listA[i].toLong() * listB[j] % mod
            }
        }
        for (i in result.indices) {
            result[i] %= mod
        }
        return result
    }
    companion object {
        @JvmStatic
        fun convolution(): Stream<Arguments> {
            val random = Random(0)
            val sizeMax = 10000
            val valueMax = 200000
            return sequence{
                var prev = List(random.nextInt(2 .. sizeMax)){random.nextInt(valueMax)}.toIntArray()
                repeat(10) {
                    val current = List(random.nextInt(2 .. sizeMax)){random.nextInt(valueMax)}.toIntArray()
                    yield(Arguments.of(prev, current, MOD1))
                    yield(Arguments.of(prev, current, MOD2))
                    prev = current
                }
            }.asStream()
        }
    }
}