package library.my

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.streams.asStream


internal class LazyAddMaxSegTest {
    class Oracle private constructor(val size: Int, private val vec: LongArray) {
        constructor(size: Int, default: Long): this(size, LongArray(size){default})
        constructor(size: Int): this(size, 0L)
        fun get(from: Int, until: Int): Long{
            return (from until until).maxOf(vec::get)
        }
        fun set(position: Int, value: Long) {
            vec[position] = value
        }
        fun add(from: Int, until: Int, value: Long) {
            for (i in from until until) {
                vec[i] += value
            }
        }
    }
    @ParameterizedTest
    @MethodSource
    fun propertyTest(seed: Int, target: LazyAddMaxSeg, oracle: Oracle) {
        val rand = Random(seed)
        val valueMax = 10000L
        repeat(3000){
            val op = rand.nextInt(5)
            if (op == 0) {// GET
                val from = rand.nextInt(target.size)
                val until = rand.nextInt(from, target.size) + 1
                assertEquals(target.get(from, until), oracle.get(from, until))
            }else if (op <= 3) {
                val position = rand.nextInt(target.size)
                val value = rand.nextLong(valueMax) - valueMax / 2
                target.set(position, value)
                oracle.set(position, value)
            }else {
                val from = rand.nextInt(target.size)
                val until = rand.nextInt(from, target.size) + 1
                val value = rand.nextLong(valueMax) - valueMax / 2
                target.add(from, until, value)
                oracle.add(from, until, value)
            }
        }
    }
    companion object {
        @JvmStatic
        fun propertyTest(): Stream<Arguments> {
            val rand = Random(0)
            val maxSize = 1000
            return sequence {
                repeat(3000){
                    val seed = rand.nextInt()
                    val size = rand.nextInt(1, maxSize)
                    val default = rand.nextLong(-10000L, 0L)
                    val target = LazyAddMaxSeg(size, default)
                    val oracle = Oracle(size, default)
                    yield(arguments(seed, target, oracle))
                }
            }.asStream()
        }
    }
}