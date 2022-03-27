package library.my.flow

typealias Capacity = Int
@kotlin.ExperimentalStdlibApi
class MaxFlow(val size: Int, val start: Int, val goal: Int) {
    class Edge(val to: Int, val pair: Int, var flow: Capacity) {
        override fun toString(): String {
            return "(to: $to, flow: $flow)"
        }
    }
    private val depth = IntArray(size)
    private val indices = IntArray(size)
    private val deque = ArrayDeque<Int>()
    private val graph = Array(size){ mutableListOf<Edge>()}
    private  fun bfs() {
        depth.fill(-1)
        depth[start] = 0
        indices.fill(0)
        deque.add(start)
        while (deque.isNotEmpty()) {
            val current = deque.removeFirst()
            for (next in graph[current]) {
                if (next.flow == 0) continue
                if (depth[next.to] != -1) continue
                depth[next.to] = depth[current] + 1
                deque.addLast(next.to)
            }
        }
    }
    private fun dfs(): Capacity {
        deque.addLast(start)
        var result: Capacity = 0
        while (true) {
            while (deque.isNotEmpty()) {
                val last = deque.last()
                if (last == goal) break
                while (indices[last] < graph[last].size) {
                    val e = graph[last][indices[last]++]
                    if (e.flow > 0 && depth[last] < depth[e.to]) {
                        deque.addLast(e.to)
                        break
                    }
                }
                if (last == deque.last()) {
                    deque.removeLast()
                }
            }
            if (deque.isEmpty()) return result
            deque.removeLast()
            var minFlow = Capacity.MAX_VALUE
            for (i in deque) {
                minFlow = minOf(minFlow, graph[i][--indices[i]].flow)
            }
            var firstBottleNeck = -1
            for (i in deque) {
                val e = graph[i][indices[i]]
                e.flow -= minFlow
                graph[e.to][e.pair].flow += minFlow
                if (firstBottleNeck == -1) {
                    ++indices[i]
                    if (e.flow == 0) {
                        firstBottleNeck = i
                    }
                }
            }
            while (deque.last() != firstBottleNeck) {
                deque.removeLast()
            }
            result += minFlow
        }
    }
    fun addDirectionalEdge(from: Int, to: Int, flow: Capacity) {
        graph[from].add(Edge(to, graph[to].size, flow))
        graph[to].add(Edge(from, graph[from].size - 1, 0))
    }
    fun addBiDirectionalEdge(from: Int, to: Int, capacity: Capacity) {
        graph[from].add(Edge(to, graph[to].size, capacity))
        graph[to].add(Edge(from, graph[from].size - 1, capacity))
    }
    fun pushFlow(): Capacity {
        var flow: Capacity = 0
        var hasFlow = true
        while (hasFlow) {
            bfs()
            val inc = dfs()
            hasFlow = inc > 0
            flow += inc
        }
        return flow
    }
}
