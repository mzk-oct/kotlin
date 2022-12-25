package library.my.dataStructure

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.streams.asStream
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class PatriciaTest {
    @ParameterizedTest
    @MethodSource
    fun test(seed: Int) {
        val random = Random(seed)
        var contestant = Patricia()
        val target = sortedSetOf<Int>()
        repeat(REPUTATION) {
            when(random.nextInt(3)) {
                0 -> {
                    val value = random.nextInt(-128 .. 128)
                    assert(value in contestant == value in target){"valur: $value, ${contestant.joinToString(",")} : ${target.joinToString(",")}"}
                    contestant += value
                    target.add(value)
                }
                1 -> {
                    val value = random.nextInt(-128 .. 128)
                    assert(value in contestant == value in target){"value: $value, ${contestant.joinToString(",")} : ${target.joinToString(",")}"}
                    contestant -= value
                    target.remove(value)
                }
                2 -> {
                    val value = random.nextInt(-129 .. 129)
                    assertEquals(contestant.greaterThan(value), target.higher(value))
                    assertEquals(contestant.greaterThanOrEqual(value), target.higher(value - 1))
                    assertEquals(contestant.lessThan(value), target.lower(value))
                    assertEquals(contestant.lessThanOrEqual(value), target.lower(value + 1))
                }
            }
            assertContentEquals(contestant, target, "\nactual: ${contestant.joinToString(", ")}\nexpected: ${target.joinToString(", ")}")
        }
    }
    @ParameterizedTest
    @MethodSource
    fun test2(seed:Int) {
        val random = Random(seed)
        var contestant = Patricia()
        val target = sortedSetOf<Int>()
        val numbers = (Int.MIN_VALUE .. Int.MIN_VALUE + random.nextInt(30)) + (random.nextInt(-10 .. 0) .. random.nextInt(0 .. 10)) + (Int.MAX_VALUE - random.nextInt(30) .. Int.MAX_VALUE)
        repeat(REPUTATION) {
            when(random.nextInt(3)) {
                0 -> {
                    val value = numbers.random(random)
                    assert(value in contestant == value in target){"valur: $value, ${contestant.joinToString(",")} : ${target.joinToString(",")}"}
                    contestant += value
                    target.add(value)
                }
                1 -> {
                    val value = numbers.random(random)
                    assert(value in contestant == value in target){"value: $value, ${contestant.joinToString(",")} : ${target.joinToString(",")}"}
                    contestant -= value
                    target.remove(value)
                }
                2 -> {
                    val value = numbers.random(random)
                    assertEquals(contestant.greaterThan(value), target.higher(value))
                    if (value != Int.MIN_VALUE) assertEquals(contestant.greaterThanOrEqual(value), target.higher(value - 1))
                    assertEquals(contestant.lessThan(value), target.lower(value))
                    if (value != Int.MAX_VALUE) assertEquals(contestant.lessThanOrEqual(value), target.lower(value + 1))
                }
            }
            assertContentEquals(contestant, target, "\nactual: ${contestant.joinToString(", ")}\nexpected: ${target.joinToString(", ")}")
        }
    }

    companion object {
        const val SEED = 1
        const val TEST_COUNT = 3000
        const val REPUTATION = 5000
        @JvmStatic
        fun test(): Stream<Int> {
            val random = Random(SEED)
            return sequence {
                repeat(TEST_COUNT) {
                    yield(random.nextInt())
                }
            }.asStream()
        }
        @JvmStatic
        fun test2(): Stream<Int> {
            val random = Random(SEED)
            return sequence {
                repeat(TEST_COUNT) {
                    yield(random.nextInt())
                }
            }.asStream()
        }
    }
}