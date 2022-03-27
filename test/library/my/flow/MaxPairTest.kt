package library.my.flow

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.streams.asStream
import kotlin.test.assertEquals

internal class MaxPairTest {




    @kotlin.ExperimentalStdlibApi
    @ParameterizedTest
    @MethodSource
    fun test(seed: Int) {
        val graph = generateGraph(seed)
        assertEquals(pair(graph), flow(graph))
    }

    fun pair(graph: List<List<Int>>): Int {
        val pair = MaxPair(graph.size, graph.size, graph)
        val count = pair.calcMatching()
        val m1 = pair.matching1
        val m2 = pair.matching2
        var c = 0
        for (i in graph.indices) {
            if (m1[i] >= 0) {
                ++c
                assertEquals(m2[m1[i]], i)
            }
        }
        assertEquals(count, c)
        c = 0
        for (i in m2.indices) {
            if (m2[i] >= 0) {
                ++c
                assertEquals(m1[m2[i]], i)
            }
        }
        assertEquals(count, c)
        return count
    }
    @kotlin.ExperimentalStdlibApi
    fun flow(graph: List<List<Int>>): Int {
        val start = graph.size * 2
        val goal = start + 1
        val flowGraph = MaxFlow(start + 2, start, goal)
        for (i in graph.indices) {
            flowGraph.addDirectionalEdge(start, i, 1)
            flowGraph.addDirectionalEdge(i + graph.size, goal, 1)
            for (j in graph[i]) {
                flowGraph.addDirectionalEdge(i, j + graph.size, 1)
            }
        }
        return flowGraph.pushFlow()
    }
    companion object {
        @JvmStatic
        fun test(): Stream<Int> {
            val random = Random(1)
            return sequence {
                repeat(10000){
                    yield(random.nextInt())
                }
            }.asStream()
        }
        fun generateGraph(seed: Int): List<List<Int>> {
            val random = Random(seed)
            val size = random.nextInt(30 .. 200)
            val graph = List(size){ mutableListOf<Int>()}
            for (l in graph) {
                repeat(random.nextInt(3 .. 10)) {
                    l.add(random.nextInt(0 until size))
                }
            }
            return graph
        }
    }
}