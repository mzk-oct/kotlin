package library.my.dataStructure

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.streams.asStream
import kotlin.test.assertEquals

internal class RawBitSetTest {
    @ParameterizedTest
    @MethodSource
    fun test(seed: Int) {
        val random = Random(seed)
        val a = makeEmptySet(random)
        val b = makeEmptySet(random)
        repeat(REPUTATION) {
            val (target, opposite) = if (random.nextInt(4) != 0) a to b else b to a
            when(random.nextInt(13)) {
                0 -> {
                    val offset = random.nextInt(minOf(300, target.size))
                    target.shl(offset)
                }
                1 -> {
                    val offset = random.nextInt(minOf(300, target.size))
                    target.shr(offset)
                }
                2 -> target.and(opposite)
                3 -> target.or(opposite)
                4 -> target.xor(opposite)
                5 -> {
                    val firstIndex = random.nextInt(target.size)
                    target.nextBit(firstIndex)
                }
                6 -> {
                    val lastIndex = random.nextInt(target.size)
                    target.previousBit(lastIndex)
                }
                7 -> {
                    val index = random.nextInt(target.size)
                    target.get(index)
                }
                8 -> {
                    val position = random.nextInt(target.size)
                    val value = random.nextBoolean()
                    target.set(position, value)
                }
                else -> {
                    val range = random.nextRange(0 until target.size)
                    val value = random.nextBoolean()
                    target.set(range, value)
                }
            }
        }
    }
    fun makeEmptySet(random: Random): Sync {
        val size = random.nextInt(1 .. MAX_SIZE)
        return Sync(size)
    }

    class Oracle(val size: Int, val array: BooleanArray) {
        constructor(size: Int): this(size, BooleanArray((size)))
        infix fun shl(offset: Int): Oracle {
            val newArray = BooleanArray(size + offset)
            array.copyInto(newArray, offset)
            return Oracle(size + offset, newArray)
        }
        infix fun shr(offset: Int): Oracle {
            if (size <= offset) return Oracle(0)
            val newArray = BooleanArray(size - offset)
            array.copyInto(newArray, 0, offset)
            return Oracle(size - offset, newArray)
        }
        infix fun and(other: Oracle): Oracle {
            if (size < other.size) return other and this
            val newBytes = BooleanArray(size)
            val from = other.array
            for (i in from.indices) {
                newBytes[i] = array[i] and from[i]
            }
            return Oracle(size, newBytes)
        }
        infix fun or(other: Oracle): Oracle {
            if (size < other.size) return other or this
            val newBytes = array.copyOf()
            val from = other.array
            for (i in from.indices) {
                newBytes[i] = newBytes[i] or from[i]
            }
            return Oracle(size, newBytes)
        }
        infix fun xor(other: Oracle): Oracle {
            if (size < other.size) return other xor this
            val newBytes = array.copyOf()
            val from = other.array
            for (i in from.indices) {
                newBytes[i] = newBytes[i] xor from[i]
            }
            return Oracle(size, newBytes)
        }
        fun set(position: Int, value: Boolean = true) {
            array[position] = value
        }
        fun set(range: IntRange, value: Boolean = true) {
            for (i in range) {
                array[i] = value
            }
        }
        fun nextBit(firstIndex: Int = 0): Int {
            for (i in firstIndex until array.size) {
                if (array[i]) return i
            }
            return -1
        }
        fun previousBit(lastIndex: Int = size - 1): Int {
            for (i in lastIndex downTo 0) {
                if (array[i]) return i
            }
            return -1
        }
        operator fun get(index: Int): Boolean {
            return array[index]
        }
    }
    class Sync(var target: RawBitSet, var oracle: Oracle) {
        constructor(size: Int): this(RawBitSet(size), Oracle(size))
        var autoCheck: Boolean = true
        val size: Int
            get() = oracle.size
        fun shl(offset: Int) {
            val t = target
            val o = oracle
            val nextTarget = target shl offset
            val nextOracle = oracle shl offset
            target = nextTarget
            oracle = nextOracle
            checkIfAutoCheck()
            if (autoCheck) {
                for (i in 0 until t.size) {
                    assertEquals(nextTarget[i + offset], t[i])
                }
            }
        }
        fun shr(offset: Int) {
            val t = target
            val o = oracle
            val nextTarget = target shr offset
            val nextOracle = oracle shr offset
            target = nextTarget
            oracle = nextOracle
            checkIfAutoCheck()
            if (autoCheck) {
                for (i in 0 until nextTarget.size) {
                    assertEquals(nextTarget[i], t[i + offset])
                }
            }
        }
        fun and(sync: Sync) {
            val nextTarget = target and sync.target
            val nextOracle = oracle and sync.oracle
            target = nextTarget
            oracle = nextOracle
            checkIfAutoCheck()
        }
        fun or(sync: Sync) {
            val nextTarget = target or sync.target
            val nextOracle = oracle or sync.oracle
            target = nextTarget
            oracle = nextOracle
            checkIfAutoCheck()
        }
        fun xor(sync: Sync) {
            val nextTarget = target xor sync.target
            val nextOracle = oracle xor sync.oracle
            target = nextTarget
            oracle = nextOracle
            checkIfAutoCheck()
        }
        fun set(position: Int, value: Boolean = true) {
            val t = target
            val o = oracle
            target.set(position, value)
            oracle.set(position, value)
            checkIfAutoCheck()
        }
        fun set(range: IntRange, value: Boolean = true) {
            target.set(range, value)
            oracle.set(range, value)
            checkIfAutoCheck()
        }
        fun nextBit(firstIndex: Int = 0): Int {
            val a = target.nextBit(firstIndex)
            val b = oracle.nextBit(firstIndex)
            if (autoCheck) {
                assertEquals(b, a)
            }
            return b
        }
        fun previousBit(lastIndex: Int = size - 1): Int {
            val a = target.previousBit(lastIndex)
            val b = oracle.previousBit(lastIndex)
            if (autoCheck) {
                assertEquals(b, a)
            }
            return b
        }
        fun get(index: Int): Boolean {
            val a = target[index]
            val b = oracle[index]
            if (autoCheck) {
                assertEquals(b, a)
            }
            return b
        }
        private fun checkIfAutoCheck() {
            if (autoCheck) {
                checkState()
            }
        }
        fun checkState() {
            assert(contentEquals(target, oracle))
        }
        companion object {
            fun contentEquals(target: RawBitSet, oracle: Oracle): Boolean {
                if (target.size != oracle.size) return false
                for (i in 0 until target.size) {
                    if (target[i] != oracle[i])
                        return false
                }
                return true
            }
        }
    }
    companion object {
        const val MAX_SIZE = 300
        const val REPUTATION = 10000
        const val TEST_COUNT = 100
        const val SEED = 8
        fun Random.nextRange(range: IntRange): IntRange {
            val from = nextInt(range)
            val to = nextInt(from .. range.last)
            return from .. to
        }
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