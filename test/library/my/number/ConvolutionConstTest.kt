package library.my.number

import library.my.number.ConvolutionConst
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.streams.asStream

const val MOD = ConvolutionConst.MOD
internal class ConvolutionConstTest    {
    @ParameterizedTest
    @MethodSource
    fun convolution(listA: IntArray, listB: IntArray) {
        val expected = brute(listA, listB)
        val actual = ConvolutionConst().convolution(listA.map{it.toLong()}.toLongArray(), listB.map{it.toLong()}.toLongArray())
        assert(expected.contentEquals(actual)) {
            "expected: ${expected.joinToString(", ")}\n"+
                    "actual: ${actual.joinToString(", ")}"

        }
    }

    fun brute(listA: IntArray, listB: IntArray): LongArray {
        val result = LongArray(listA.size + listB.size - 1)
        for (i in listA.indices) {
            for (j in listB.indices) {
                result[i + j] = (result[i + j] + listA[i].toLong() * listB[j]) % MOD
            }
        }
        for (i in result.indices) {
            result[i] = result[i] % MOD
        }
        return result
    }
    companion object {
        const val SEED = 1
        @JvmStatic
        fun convolution(): Stream<Arguments> {
            val random = Random(SEED)
            val sizeMax = 500
            val valueMax = MOD
            return sequence {
                var prev = List(random.nextInt(2 .. sizeMax)) { random.nextInt(valueMax) }.toIntArray()
                repeat(5000) {
                    val current = List(random.nextInt(2 .. sizeMax)) { random.nextInt(valueMax) }.toIntArray()
                    yield(Arguments.of(prev, current))
                    prev = current
                }
            }.asStream()
        }
    }
}
