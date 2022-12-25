package library.my.flow

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.streams.asStream
import kotlin.test.assertEquals

internal class DinicTest() {
    @ParameterizedTest
    @MethodSource
    fun test(seed: Int) {
        val random = Random(seed)
        val size = random.nextInt(10, 300)
        val ratio = random.nextDouble(0.05, 0.9)
        val graph = generateGraph(size, ratio, random)
        val expected = target(0, 1, graph)
        val actual = contestant(0, 1, graph)
        assertEquals(expected, actual)
    }
    fun contestant(start: Int, goal: Int, graph: List<List<Pair<Int, Int>>>): Long {
        val flowGraph = Dinic(graph.size, start, goal)
        for ((i, edges) in graph.withIndex()) {
            for ((j, flow) in edges) {
                flowGraph.addDirectionalEdge(i, j, flow)
            }
        }
        return flowGraph.pushFlow().toLong()
    }
    fun target(start: Int, goal: Int, graph: List<List<Pair<Int, Int>>>): Long {
        val matrix = Array(graph.size){LongArray(graph.size)}
        for ((i, edges) in graph.withIndex()) {
            for ((j, flow) in edges) {
                matrix[i][j] += flow.toLong()
            }
        }
        return DinicMatrix.pushFlow(matrix, start, goal).first
    }
    fun generateGraph(size: Int, ratio: Double, random: Random): List<List<Pair<Int, Int>>> {
        val result = List(size){ mutableListOf<Pair<Int, Int>>() }
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (i == j) continue
                if (random.nextDouble() <= ratio) {
                    result[i].add(j to random.nextInt(1, 10))
                }
            }
        }
        return result
    }
    companion object {
        @JvmStatic
        fun test(): Stream<Int> {
            val random = Random(0)
            return sequence {
                repeat(3000) {
                    yield(random.nextInt())
                }
            }.asStream()
        }
    }
}