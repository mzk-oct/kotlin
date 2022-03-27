package library.my

import java.util.*

object DAG {
    fun dag(n: Int, graph: List<List<Int>>): IntArray {
        val visit = BooleanArray(n)
        val stack = ArrayDeque<Int>()
        var count = 0
        val history = mutableListOf<Int>()
        for (start in graph.indices) {
            stack.addLast(start)
            while (stack.isNotEmpty()) {
                val current = stack.removeLast()
                if (current >= 0) {
                    if (visit[current]) continue
                    visit[current] = true
                    stack.addLast(-1 - current)
                    for (next in graph[current]) {
                        stack.addLast(next)
                    }
                } else {
                    history.add(-1-current)
                }
            }
        }
        val reverse = List(graph.size){ mutableListOf<Int>()}
        for (from in graph.indices) {
            for (to in graph[from]) {
                reverse[to].add(from)
            }
        }
        count = 0
        val group = IntArray(graph.size){-1}
        for (start in history.reversed()) {
            if (group[start] != -1) continue
            stack.addLast(start)
            while (stack.isNotEmpty()) {
                val current = stack.removeLast()
                if (group[current] != -1) continue
                group[current] = count
                for (next in reverse[current]) {
                    stack.addLast(next)
                }
            }
            ++count
        }
        return group
    }
}

//Fast
fun dag(graph: List<List<Int>>): IntArray {
    val n = graph.size
    val indices = IntArray(n)
    val stack = IntArray(n)
    val size = IntArray(n)
    var pos = 0
    var outPos = n - 1
    for (i in 0 until n) {
        if (indices[i] != 0) continue
        stack[pos++] = i
        while (pos > 0) {
            val top = stack[--pos]
            val edge = graph[top]
            if (indices[top] == edge.size) {
                indices[top] = -1
                stack[outPos--] = top
            }else {
                while (indices[top] < edge.size && indices[edge[indices[top]]] != 0) {
                    ++size[edge[indices[top]++]]
                }
                ++pos
                if (indices[top] < edge.size) {
                    stack[pos++] = edge[indices[top]]
                    ++size[edge[indices[top]++]]
                }
            }
        }
    }
    val reverseGraph = List(n){ IntArray(size[it]--) }
    for (f in graph.indices) {
        for (t in graph[f]) {
            reverseGraph[t][size[t]--] = f
        }
    }
    for (v in stack) {
        if (indices[v] >= 0) continue
        indices[v] = ++outPos
        size[pos++] = v
        while (pos > 0) {
            val top = size[--pos]
            for (next in reverseGraph[top]) {
                if (indices[next] >= 0) continue
                indices[next] = outPos
                size[pos++] = next
            }
        }
    }
    return indices
}