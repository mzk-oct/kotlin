package library.my.graph

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.streams.asStream

internal class DAGKtTest {


    fun verify(a: IntArray, b: IntArray): Boolean {
        if (a.size != b.size) return false
        val aMap = IntArray(a.size)
        var aCount = 0
        val bMap = IntArray(b.size)
        var bCount = 0
        for (i in a.indices) {
            if (aMap[a[i]] == 0) {
                aMap[a[i]] = ++aCount
            }
            if (bMap[b[i]] == 0) {
                bMap[b[i]] = ++bCount
            }
            if (aMap[a[i]] != bMap[b[i]]) {
                return false
            }
        }
        return true
    }


    @ParameterizedTest
    @MethodSource
    fun test(graph: List<List<Int>>) {
        val actual = dag(graph)
        val expected = DAG.dag(graph.size, graph)
        assert(verify(actual, expected))
    }
    companion object {
        const val SEED = 2
        fun generateGraph(seed: Int, vertex: Int, edge: Int): List<List<Int>> {
            val random = Random(seed)
            val graph = List(vertex){ hashSetOf<Int>() }
            repeat(edge) {
                val from = random.nextInt(0, vertex)
                val to = (from + random.nextInt(1, vertex)) % vertex
                graph[from].add(to)
            }
            return graph.map{it.toList()}
        }
        @JvmStatic
        fun test(): Stream<List<List<Int>>> {
            val random = Random(SEED)
            return sequence {
                repeat(10000) {
                    val size = 1000
                    val edge = 2500
                    val seed = random.nextInt()
                    yield(generateGraph(seed, size, edge))
                }
            }.asStream()
        }
    }
}