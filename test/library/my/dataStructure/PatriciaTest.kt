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

    companion object {
        const val SEED = 0
        const val TEST_COUNT = 1000
        const val REPUTATION = 2000
        @JvmStatic
        fun test(): Stream<Int> {
            val random = Random(SEED)
            return sequence {
                repeat(TEST_COUNT) {
                    yield(random.nextInt())
                }
            }.asStream()
        }
    }
}