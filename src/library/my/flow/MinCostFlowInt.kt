package library.my.flow

import java.util.*

class Edge(val to: Int, val pair: Int, var flow: Int, val cost: Long) {
    override fun toString(): String {
        return "Edge(to: $to, pair: $pair, flow: $flow, cost: $cost)"
    }
}
class MinCostFlowInt(val size: Int) {
    val vec = Array(size){ mutableListOf<Edge>()}
    fun addEdge(from: Int, to: Int, flow: Int, cost: Long) {
        vec[from].add(Edge(to, vec[to].size, flow, cost))
        vec[to].add(Edge(from, vec[from].size - 1, 0, -cost))
    }
    fun push(source: Int, sink: Int, flowLimit: Int = Int.MAX_VALUE): List<Pair<Int, Long>> {
        val potential = LongArray(size)
        val minDistance = LongArray(size)
        val queue = PriorityQueue(compareBy(Pair<Int, Long>::second))
        val prevEdge = Array(size){Edge(-1, -1, 0, -1)}
        val maxFlow = IntArray(size)
        val result = mutableListOf(0 to 0L)
        var sumFlow = 0
        while (sumFlow < flowLimit) {
            minDistance.fill(Long.MAX_VALUE)
            minDistance[source] = 0
            maxFlow[source] = flowLimit - sumFlow
            queue.clear()
            queue.add(source to 0L)
            while (queue.isNotEmpty()) {
                val (current, cost) = queue.poll()
                if (minDistance[current] < cost) continue
                for (next in vec[current]) {
                    if (next.flow == 0) continue
                    val e = potential[current] - potential[next.to] + next.cost
                    if (minDistance[next.to] > cost + e) {
                        minDistance[next.to] = cost + e
                        prevEdge[next.to] = next
                        maxFlow[next.to] = minOf(maxFlow[current], next.flow)
                        queue.add(next.to to cost + e)
                    }
                }
            }
            if (minDistance[sink] == Long.MAX_VALUE) break
            for (i in minDistance.indices) {
                potential[i] += minDistance[i]
            }
            val cost = potential[sink]
            var last = sink
            val flow = maxFlow[sink]
            while (last != source) {
                val e = prevEdge[last]
                val r = vec[e.to][e.pair]
                e.flow -= flow
                r.flow += flow
                last = r.to
            }
            result.add(flow to cost)
            sumFlow += flow
        }
        return result
    }
}