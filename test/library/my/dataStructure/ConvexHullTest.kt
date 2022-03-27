package library.my.dataStructure

import library.my.dataStructure.ConvexHull.Line
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.streams.asStream
import kotlin.test.assertEquals

internal class ConvexHullTest {


    @ParameterizedTest
    @MethodSource
    fun test(seed: Int, size: Int, reputation: Int) {
        val random = Random(seed)
        val xPosition = DoubleArray(size){random.nextDouble(-10000.0, 10000.0)}.also { it.sort() }
        val oracle = Oracle(xPosition)
        val target = ConvexHull.ConvexHullMin(xPosition)
        repeat(reputation) {
            when(random.nextInt(2)) {
                0 -> {
                    val a = random.nextDouble(-1000000.0, 1000000.0)
                    val b = random.nextDouble(-1000000.0, 1000000.0)
                    oracle.add(Line(a, b))
                    target.add(Line(a, b))
                }
                1 -> {
                    val position = random.nextInt(size)
                    val actual = target.getMin(position)
                    val expected = oracle.getMin(position)
                    assertEquals(expected, actual)
                }
            }
        }
    }
    class Oracle(private val xPosition: DoubleArray) {
        private val lines = mutableListOf<Line>()
        fun add(line: Line) = lines.add(line)
        fun getMin(position: Int): Double? = lines.minOfOrNull { it(xPosition[position]) }
    }
    companion object {
        const val REPUTATION = 3000
        @JvmStatic
        fun test(): Stream<Arguments> {
            val random = Random(1)
            return sequence {
                repeat(3000) {
                    yield(Arguments.of(random.nextInt(), random.nextInt(3, 1000), REPUTATION))
                }
            }.asStream()
        }
    }
}