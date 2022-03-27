package library.my

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.streams.asStream
import kotlin.test.assertEquals


internal class RBTree2Test {
    @ParameterizedTest
    @MethodSource
    fun test(seed: Int) {
        val random = Random(seed)
        val range = 1 .. 10000
        val oracle = TreeSet<Int>() as MutableSet<Int>
        val target = RBTree2<Int>() as MutableSet<Int>
        repeat(50000) {
            val value = random.nextInt(range)
            if (random.nextInt(0 .. 2) < 2) {
                assertEquals(oracle.add(value), target.add(value))
            }else {
                assertEquals(value in oracle, value in target)
            }
        }
    }
    companion object {
        const val SEED = 0
        @JvmStatic
        fun test(): Stream<Int> {
            val random = Random(SEED)
            return sequence {
                repeat(1000) {
                    yield(random.nextInt())
                }
            }.asStream()
        }
    }
}