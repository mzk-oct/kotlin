package library.my.dataStructure

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.streams.asStream
import kotlin.test.assertEquals

internal class LazyFlipSegTest {
    class Oracle private constructor(val size: Int, private val vec: Array<Range>) {
        constructor(size: Int): this(size, Array(size){ Range(1, 0) })
        constructor(initial: Array<Range>): this(initial.size, initial.clone())
        fun get(from: Int, until: Int): Range {
            return (from until until).map(vec::get).fold(Range(0, 0), Range::plus)
        }
        fun flip(from: Int, until: Int) {
            for (i in from until until) {
                vec[i] = vec[i].flip()
            }
        }
        fun getAll(): Range {
            return vec.fold(Range(0, 0), Range::plus)
        }
    }
    @ParameterizedTest
    @MethodSource
    fun propertyTest(seed: Int, target: LazyFlipSeg, oracle: Oracle) {
        val rand = Random(seed)
        val valueMax = Int.MAX_VALUE.toLong()
        repeat(3000) {
            val op = rand.nextInt(6)
            if (op == 1) {
                val from = rand.nextInt(oracle.size)
                val until = rand.nextInt(from, oracle.size) + 1
                assertEquals(target.get(from, until), oracle.get(from, until))
            }else if (op <= 4) {
                val from = rand.nextInt(oracle.size)
                val until = rand.nextInt(from, oracle.size) + 1
                target.flip(from, until)
                oracle.flip(from, until)
            }else {
                assertEquals(target.getAll(), oracle.getAll())
            }
        }
    }
    companion object {
        @JvmStatic
        fun propertyTest(): Stream<Arguments> {
            val rand = Random(0)
            val maxSize = 300
            return sequence {
                repeat(3000) {
                    val s = rand.nextInt()
                    val r = Random(s)
                    val seed = r.nextInt()
                    val size = r.nextInt(1, maxSize)
                    val init = Array(size){ Range(r.nextInt(50), r.nextInt(50)) }
                    val target = LazyFlipSeg(init)
                    val oracle = Oracle(init)
                    yield(Arguments.arguments(seed, target, oracle))
                }
            }.asStream()
        }
    }
}