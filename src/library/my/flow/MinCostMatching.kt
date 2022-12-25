package library.my.flow

import java.util.*

class MatchingEdge(val to: Int, val pair: Int, var flow: Boolean, val cost: Long) {
    override fun toString(): String {
        return "Edge(to: $to, pair: $pair, flow: $flow, cost: $cost)"
    }
}
class MinCostMatching(val sourceSide: Int, val sinkSide: Int) {
    private val source = sourceSide + sinkSide
    private val sink = source + 1
    private val graph = Array(sink + 1){ mutableListOf<MatchingEdge>()}
    init {
        for (i in 0 until sourceSide) {
            addEdgeInner(source, i, 0)
        }
        for (i in sourceSide until sourceSide + sinkSide) {
            addEdgeInner(i, sink, 0)
        }
    }
    private fun addEdgeInner(from: Int, to: Int, cost: Long) {
        graph[from].add(MatchingEdge(to, graph[to].size, true, cost))
        graph[to].add(MatchingEdge(from, graph[from].size - 1, false, -cost))
    }
    fun addEdge(a: Int, b: Int, cost: Long) {
        addEdgeInner(a, b + sourceSide, cost)
    }
    private fun initialPotential(): LongArray {
        val result = LongArray(graph.size){Long.MAX_VALUE}
        result[source] = 0L
        for (i in 0 until sourceSide) {
            result[i] = 0L
            for (e in graph[i]) {
                result[e.to] = minOf(result[e.to], e.cost)
            }
        }
        for (i in sourceSide until sourceSide + sinkSide) {
            result[sink] = minOf(result[sink], result[i])
        }
        return result
    }
    fun push(flowLimit: Int = Int.MAX_VALUE): List<Long> {
        val size = graph.size
        val potential = initialPotential()
        val minDistance = LongArray(size)
        val queue = PriorityQueue(compareBy(Pair<Int, Long>::second))
        val prevEdge = Array(size){MatchingEdge(-1, -1, false, -1)}
        val result = mutableListOf(0L)
        repeat(flowLimit) {
            minDistance.fill(Long.MAX_VALUE)
            minDistance[source] = 0
            queue.clear()
            queue.add(source to 0L)
            while (queue.isNotEmpty()) {
                val (current, cost) = queue.poll()
                if (minDistance[current] < cost) continue
                for (edge in graph[current]) {
                    if (!edge.flow) continue
                    val e = potential[current] - potential[edge.to] + edge.cost
                    if (minDistance[edge.to] > cost + e) {
                        minDistance[edge.to] = cost + e
                        prevEdge[edge.to] = edge
                        queue.add(edge.to to cost + e)
                    }
                }
            }
            if (minDistance[sink] == Long.MAX_VALUE) return result
            for (i in minDistance.indices) {
                if (minDistance[i] == Long.MAX_VALUE) continue
                potential[i] += minDistance[i]
            }
            val cost = potential[sink]
            var last = sink
            while (last != source) {
                val e = prevEdge[last]
                val r = graph[e.to][e.pair]
                e.flow = false
                r.flow = true
                last = r.to
            }
            result.add(cost)
        }
        return result
    }
}