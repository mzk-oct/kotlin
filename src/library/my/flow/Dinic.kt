package library.my.flow

typealias FlowType = Int
class Dinic(val size: Int, val start: Int, val goal: Int) {
    class Edge(val to: Int, val pair: Int, var flow: FlowType) {
        override fun toString(): String {
            return "(to: $to, flow: $flow)"
        }
    }
    private val depth = IntArray(size)
    private val indices = IntArray(size)
    private val deque = IntArray(size)
    private val graph = Array(size){ ArrayList<Edge>() }
    private  fun bfs() {
        depth.fill(-1)
        depth[start] = 0
        indices.fill(0)
        var begin = 0
        var end = 0
        deque[end++] = start
        while (begin < end) {
            val current = deque[begin++]
            if (depth[current] == depth[goal]) break
            for (next in graph[current]) {
                if (next.flow == 0) continue
                if (depth[next.to] != -1) continue
                depth[next.to] = depth[current] + 1
                deque[end++] = next.to
            }
        }
    }
    private fun dfs(): Int {
        var end = 0
        deque[end++] = start
        var result = 0
        while (true) {
            while (0 < end) {
                val last = deque[end - 1]
                if (last == goal) break
                while (indices[last] < graph[last].size) {
                    val e = graph[last][indices[last]++]
                    if (e.flow > 0 && depth[last] < depth[e.to]) {
                        deque[end++] = e.to
                        break
                    }
                }
                if (last == deque[end - 1]) {
                    --end
                }
            }
            if (end-- == 0) break
            var minFlow = FlowType.MAX_VALUE
            for (idx in 0 until end) {
                val i = deque[idx]
                minFlow = minOf(minFlow, graph[i][--indices[i]].flow)
            }
            var firstBottleNeck = -1
            for (idx in 0 until end) {
                val i = deque[idx]
                val e = graph[i][indices[i]]
                e.flow -= minFlow
                graph[e.to][e.pair].flow += minFlow
                if (firstBottleNeck == -1) {
                    ++indices[i]
                    if (e.flow == 0) {
                        firstBottleNeck = idx
                    }
                }
            }
            end = firstBottleNeck
            result += minFlow
        }
        return result
    }
    fun addDirectionalEdge(from: Int, to: Int, flow: FlowType) {
        graph[from].add(Edge(to, graph[to].size, flow))
        graph[to].add(Edge(from, graph[from].size - 1, 0))
    }
    fun addBiDirectionalEdge(from: Int, to: Int, flow: FlowType) {
        graph[from].add(Edge(to, graph[to].size, flow))
        graph[to].add(Edge(from, graph[from].size - 1, flow))
    }
    fun pushFlow(): FlowType {
        var flow = 0
        while (true) {
            bfs()
            val inc = dfs()
            if (inc == 0) break
            flow += inc
        }
        return flow
    }
}
object DinicMatrix {
    fun pushFlow(graph: Array<LongArray>, source: Int, sink: Int): Pair<Long, Array<LongArray>> {
        val n = graph.size
        val flow = Array(n){graph[it].clone()}
        val indices = IntArray(n)
        val depth = IntArray(n)
        val queue = ArrayDeque<Int>()
        fun bfs() {
            depth.fill(-1)
            depth[source] = 0
            queue.addLast(source)
            while (queue.isNotEmpty()) {
                val top = queue.removeFirst()
                for (i in 0 until n) {
                    if (flow[top][i] > 0 && depth[i] == -1) {
                        depth[i] = depth[top] + 1
                        queue.addLast(i)
                    }
                }
            }
        }
        fun dfs(current: Int, minFlow: Long): Long {
            if (current == sink)
                return minFlow
            while (indices[current] < n) {
                val next = indices[current]++
                if (depth[next] > depth[current] && flow[current][next] > 0) {
                    val result = dfs(next, minOf(minFlow, flow[current][next]))
                    if (result > 0) {
                        indices[current]--
                        flow[current][next] -= result
                        flow[next][current] += result
                        return result
                    }
                }
            }
            return 0
        }
        var result = 0L
        var hasFlow = true
        while (hasFlow) {
            hasFlow = false
            indices.fill(0)
            bfs()
            var f = dfs(source, Long.MAX_VALUE)
            while (f > 0) {
                result += f
                hasFlow = true
                f = dfs(source, Long.MAX_VALUE)
            }
        }
        return result to flow
    }
}
class DinicBoolean(val size: Int, val start: Int, val goal: Int) {
    class Edge(val to: Int, val pair: Int, var capacity: Boolean) {
        override fun toString(): String {
            return "(to: $to, flow: $capacity)"
        }
    }
    private val depth = IntArray(size)
    private val indices = IntArray(size)
    private val deque = IntArray(size)
    private val graph = Array(size){ ArrayList<Edge>() }
    private  fun bfs() {
        depth.fill(-1)
        depth[start] = 0
        indices.fill(0)
        var begin = 0
        var end = 0
        deque[end++] = start
        while (begin < end) {
            val current = deque[begin++]
            if (depth[current] == depth[goal]) break
            for (next in graph[current]) {
                if (!next.capacity) continue
                if (depth[next.to] != -1) continue
                depth[next.to] = depth[current] + 1
                deque[end++] = next.to
            }
        }
    }
    private fun dfs(): Int {
        var end = 0
        deque[end++] = start
        var result = 0
        while (true) {
            while (0 < end) {
                val last = deque[end - 1]
                if (last == goal) break
                while (indices[last] < graph[last].size) {
                    val e = graph[last][indices[last]++]
                    if (e.capacity && depth[last] < depth[e.to]) {
                        deque[end++] = e.to
                        break
                    }
                }
                if (last == deque[end - 1]) {
                    --end
                }
            }
            if (end-- == 0) break
            for (idx in 0 until end) {
                val i = deque[idx]
                val e = graph[i][indices[i]]
                e.capacity = false
                graph[e.to][e.pair].capacity = true
            }
            end = 0
            result += 1
        }
        return result
    }
    fun addDirectionalEdge(from: Int, to: Int) {
        graph[from].add(Edge(to, graph[to].size, true))
        graph[to].add(Edge(from, graph[from].size - 1, false))
    }
    fun addBiDirectionalEdge(from: Int, to: Int) {
        graph[from].add(Edge(to, graph[to].size, true))
        graph[to].add(Edge(from, graph[from].size - 1, true))
    }
    fun pushFlow(): Int {
        var flow = 0
        while (true) {
            bfs()
            val inc = dfs()
            if (inc == 0) break
            flow += inc
        }
        return flow
    }
}