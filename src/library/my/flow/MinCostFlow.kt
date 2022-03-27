package library.my

import java.util.*

class Edge(val to: Int, val pair: Int, var flow: Boolean, val cost: Long) {
    override fun toString(): String {
        return "Edge(to: $to, pair: $pair, flow: $flow, cost: $cost)"
    }
}
class MinCostFlow(val size: Int) {
    val vec = Array(size){ mutableListOf<Edge>()}
    fun addEdge(from: Int, to: Int, cost: Long) {
        vec[from].add(Edge(to, vec[to].size, true, cost))
        vec[to].add(Edge(from, vec[from].size - 1, false, -cost))
    }
    fun push(source: Int, sink: Int, flowLimit: Int = Int.MAX_VALUE): List<Long> {
        val potential = LongArray(size)
        val minDistance = LongArray(size)
        val queue = PriorityQueue(compareBy(Pair<Int, Long>::second))
        val prevEdge = Array(size){Edge(-1, -1, false, -1)}
        val result = mutableListOf(0L)
        repeat(flowLimit){
            minDistance.fill(Long.MAX_VALUE)
            minDistance[source] = 0
            queue.clear()
            queue.add(source to 0L)
            while (queue.isNotEmpty()) {
                val (current, cost) = queue.poll()
                if (minDistance[current] < cost) continue
                for (next in vec[current]) {
                    if (!next.flow) continue
                    val e = potential[current] - potential[next.to] + next.cost
                    if (minDistance[next.to] > cost + e) {
                        minDistance[next.to] = cost + e
                        prevEdge[next.to] = next
                        queue.add(next.to to cost + e)
                    }
                }
            }
            if (minDistance[sink] == Long.MAX_VALUE) return result
            for (i in minDistance.indices) {
                potential[i] += minDistance[i]
            }
            result.add(result.last() + potential[sink])
            var last = sink
            while (last != source) {
                val e = prevEdge[last]
                val r = vec[e.to][e.pair]
                e.flow = false
                r.flow = true
                last = r.to
            }
        }
        return result
    }
}