package library.my.flow

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.streams.asStream
import kotlin.test.assertEquals

class MinCostMatchingTest {


    @ParameterizedTest
    @MethodSource
    fun test(seed: Int) {
        val random = Random(seed)
        val sourceSize = random.nextInt(3 .. 12)
        val sinkSize = random.nextInt(3 .. 12)
        val edges = generateGraph(random, sourceSize, sinkSize)
        val contestant = FastMinCostMatching(sourceSize, sinkSize)
        for ((from, to, cost) in edges) {
            contestant.addEdge(from, to, cost.toLong())
        }
        val expected = calcDp(sourceSize, sinkSize, edges)
        val actualMatching = contestant.pushFlow()
        val actual = actualMatching.sumOf { it.third }
        assertEquals(expected, actual)
    }
    @ParameterizedTest
    @MethodSource
    fun test2(seed: Int) {
        val random = Random(seed)
        val sourceSize = random.nextInt(36 .. 128)
        val sinkSize = random.nextInt(36 .. 128)
        val edges = generateGraph(random, sourceSize, sinkSize)
        val contestant = FastMinCostMatching(sourceSize, sinkSize)
        val oracle = MinCostMatching(sourceSize, sinkSize)
        for ((from, to, cost) in edges) {
            contestant.addEdge(from, to, cost.toLong())
            oracle.addEdge(from, to, cost.toLong())
        }
        val expectedFlow = oracle.push()
        val expected = expectedFlow.sum()
        val actualMatching = contestant.pushFlow()
        val actual = actualMatching.sumOf { it.third }
        assertEquals(expected, actual)
    }

    fun generateGraph(random: Random, sourceSide: Int, sinkSide: Int): List<Triple<Int, Int, Int>> {
        val result = mutableListOf<Triple<Int, Int, Int>>()
        for (i in 0 until sourceSide) {
            val r = random.nextDouble()
            for (j in 0 until sinkSide) {
                if (r < random.nextDouble()) continue
                result.add(Triple(i, j, random.nextInt()))
            }
        }
        result.shuffle(random)
        return result
    }
    fun generateGraph(seed: Int, sourceSide: Int, sinkSide: Int): List<Triple<Int, Int, Int>> {
        return generateGraph(Random(seed), sourceSide, sinkSide)
    }
    fun calcDp(sourceSide: Int, sinkSide: Int, edges: List<Triple<Int, Int, Int>>): Long {
        val memo = LongArray(1 shl sinkSide){Long.MAX_VALUE}.also { it[0] = 0 }
        val graph = Array(sourceSide){ mutableListOf<Pair<Int, Int>>() }
        for ((from, to, cost) in edges) {
            graph[from].add(to to cost)
        }
        for (list in graph) {
            for (opposite in memo.indices.reversed()) {
                if (memo[opposite] == Long.MAX_VALUE) continue
                for ((to, cost) in list) {
                    if ((opposite shr to) and 1 == 1) continue
                    memo[opposite or (1 shl to)] = minOf(memo[opposite or (1 shl to)], memo[opposite] + cost)
                }
            }
        }
        val maxPair = memo.indices.filter { memo[it] != Long.MAX_VALUE }.maxOf { it.countOneBits() }
        return memo.indices.filter { it.countOneBits() == maxPair }.minOf { memo[it] }
    }
    companion object {
        @JvmStatic
        fun test(): Stream<Int> {
            return sequence {
                repeat(1000) {
                    yield(it)
                }
            }.asStream()
        }
        @JvmStatic
        fun test2(): Stream<Int> {
            return sequence {
                repeat(1000) {
                    yield(it)
                }
            }.asStream()
        }
    }
}