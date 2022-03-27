package library.my

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.streams.asStream
import kotlin.test.assertEquals

internal class LazyMinMinSegTest {
    class Oracle private constructor(val size: Int, private val vec: LongArray) {
        constructor(size: Int): this(size, LongArray(size){Long.MAX_VALUE})
        fun get(from: Int, until: Int): Long {
            return (from until until).minOf(vec::get)
        }
        fun get(position: Int): Long {
            return vec[position]
        }
        fun set(from: Int, until: Int, value: Long) {
            for (i in from until until) {
                vec[i] = minOf(vec[i], value)
            }
        }
        fun set(position: Int, value: Long) {
            vec[position] = minOf(vec[position], value)
        }
    }
    @ParameterizedTest
    @MethodSource
    fun propertyTest(seed: Int, target: LazyMinMinSeg, oracle: Oracle) {
        val rand = Random(seed)
        val valueMax = Int.MAX_VALUE.toLong()
        repeat(3000) {
            val op = rand.nextInt(6)
            if (op == 0) {
                val from = rand.nextInt(target.size)
                val until = rand.nextInt(from, target.size) + 1
                assertEquals(target.get(from, until), oracle.get(from, until))
            }else if (op == 1) {
                val position = rand.nextInt(target.size)
                assertEquals(target.get(position), oracle.get(position))
            }else if (op <= 3) {
                val from = rand.nextInt(target.size)
                val until = rand.nextInt(from, target.size) + 1
                val value = rand.nextLong(valueMax)
                target.set(from, until, value)
                oracle.set(from, until, value)
            }else {
                val position = rand.nextInt(target.size)
                val value = rand.nextLong(valueMax)
                target.set(position, value)
                oracle.set(position, value)
            }
        }
    }
    companion object {
        @JvmStatic
        fun propertyTest(): Stream<Arguments> {
            val rand = Random(0)
            val maxSize = 1000
            return sequence {
                repeat(3000) {
                    val s = rand.nextInt()
                    val r = Random(s)
                    val seed = r.nextInt()
                    val size = r.nextInt(1, maxSize)
                    val target = LazyMinMinSeg(size)
                    val oracle = Oracle(size)
                    yield(Arguments.arguments(seed, target, oracle))
                }
            }.asStream()
        }
    }
}