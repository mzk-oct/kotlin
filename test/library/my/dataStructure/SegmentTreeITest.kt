package library.my.dataStructure

import library.my.SegmentTreeI
import library.my.V
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.streams.asStream
import kotlin.test.assertEquals

internal class SegmentTreeITest {
    class TargetSum private constructor(size: Int, initial: IntArray): SegmentTreeI(size, initial) {
        constructor(size: Int): this(size, intArrayOf())
        constructor(initial: IntArray): this(initial.size, initial)
        override val e: V = 0
        override fun plus(left: V, right: V): V = left + right
        companion object {
            class Oracle private constructor(val size: Int, private val vec: IntArray) {
                constructor(initial: IntArray): this(initial.size, initial)
                fun get(from: Int, until: Int): V = (from until until).sumOf(vec::get)
                fun get(position: Int): V = vec[position]
                fun set(position: Int, value: V) {
                    vec[position] = value
                }
                fun searchRight(left: Int, predicate: (V) -> Boolean): Int? {
                    if (!predicate(0)) return null
                    var value = 0
                    for (i in left until size) {
                        value += vec[i]
                        if (!predicate(value)) return i
                    }
                    return size
                }
                fun searchLeft(right: Int, predicate: (V) -> Boolean): Int? {
                    if (!predicate(0)) return null
                    var value = 0
                    for (i in right - 1 downTo 0) {
                        value += vec[i]
                        if (!predicate(value)) return i + 1
                    }
                    return 0
                }
            }
        }
    }
    @ParameterizedTest
    @MethodSource
    fun test(seed: Int) {
        val random = Random(seed)
        val size = random.nextInt(sizeRange)
        val initial = IntArray(size){random.nextInt(valueRange)}
        val target = TargetSum(initial)
        val oracle = TargetSum.Companion.Oracle(initial)
        repeat(reputation) {
            when(random.nextInt(0 until 5)) {
                0 -> {
                    //println("GET RANGE")
                    val from = random.nextInt(0 until size)
                    val until = random.nextInt(from .. size)
                    val expected = oracle.get(from, until)
                    val actual = target.get(from, until)
                    assertEquals(expected, actual)
                }
                1 -> {
                    //println("GET")
                    val pos = random.nextInt(0 until size)
                    val expected = oracle.get(pos)
                    val action = target.get(pos)
                    assertEquals(expected, action)
                }
                2 -> {
                    //println("SEARCH RIGHT")
                    val left = random.nextInt(0 until size)
                    val sum = (valueRange.last * size * 1.1 * random.nextDouble()).toInt()
                    val expected = oracle.searchRight(left){it <= sum}
                    val actual = target.searchRight(left){it <= sum}
                    assertEquals(expected, actual)
                }
                3 -> {
                    //println("SEARCH LEFT")
                    val right = random.nextInt(0 until size)
                    val sum = (valueRange.last * size * 1.1 * random.nextDouble()).toInt()
                    val expected = oracle.searchLeft(right){it <= sum}
                    val actual = target.searchLeft(right){it <= sum}
                    assertEquals(expected, actual)
                }
                else -> {
                    //println("SET")
                    val pos = random.nextInt(0 until size)
                    val value = random.nextInt(valueRange)
                    oracle.set(pos, value)
                    target.set(pos, value)
                }
            }
        }
    }
    companion object {
        @JvmStatic
        fun test(): Stream<Int> {
            val random = Random(SEED)
            return sequence {
                repeat(1000){
                    yield(random.nextInt())
                }
            }.asStream()
        }
        const val SEED = 321
        val sizeRange = 10 .. 256
        val valueRange = 0 .. 1000
        const val reputation = 100000
    }
}