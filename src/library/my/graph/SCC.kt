package library.my.graph

object SCC {
    fun scc(graph: List<List<Int>>): IntArray {
        val n = graph.size
        val stack = IntArray(n)
        val inSearchStack = IntArray(n)
        val order = IntArray(n){-1}
        val low = IntArray(n)
        val result = IntArray(n){-1}
        val indices = IntArray(n)
        var i = 0
        var si = 0
        var count = 0
        for (root in 0 until n) {
            if (order[root] >= 0) continue
            stack[i++] = root
            while (i > 0) {
                val top = stack[--i]
                if (indices[top] == 0) {
                    inSearchStack[si++] = top
                    order[top] = count++
                }
                if (indices[top] < graph[top].size) {
                    var idx = indices[top]
                    while (idx < graph[top].size && order[graph[top][idx]] >= 0) {
                        ++idx
                    }
                    stack[i++] = top
                    if (idx < graph[top].size) {
                        stack[i++] = graph[top][idx++]
                    }
                    indices[top] = idx
                }else {
                    var currentLow = order[top]
                    for (next in graph[top]) {
                        if (result[next] >= 0) continue
                        currentLow = if (order[next] < order[top]) {
                            minOf(currentLow, order[next])
                        }else {
                            minOf(currentLow, low[next])
                        }
                    }
                    low[top] = currentLow
                    if (currentLow == order[top]) {
                        result[top] = top
                        while (inSearchStack[--si] != top) {
                            result[inSearchStack[si]] = top
                        }
                    }
                }
            }
        }
        return result
    }
}