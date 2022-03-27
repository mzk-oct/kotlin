package library.my.flow

class MaxPair(val size1: Int, val size2: Int, graph: List<List<Int>>) {
    private val depth = IntArray(size1 + size2)
    private val indices = IntArray(size1)
    private val queue = IntArray(size1 + size2)
    private val freeNode = IntArray(size1){it}
    private var freeLast = size1
    val matching1 = IntArray(size1){-1}
    val matching2 = IntArray(size2){-1}
    private val outEdge = graph.map{it.toIntArray()}
    private fun bfs() {
        var begin = 0
        var end = 0
        depth.fill(-1)
        for (i in 0 until freeLast) {
            queue[end++] = freeNode[i]
            depth[freeNode[i]] = 0
        }
        while (begin < end) {
            val top = queue[begin++]
            if (top < size1) {
                for (to in outEdge[top]) {
                    if (to < 0 || depth[to + size1] != -1) continue
                    depth[to + size1] = depth[top] + 1
                    queue[end++] = to + size1
                }
            }else {
                val pair = matching2[top - size1]
                if (pair == -1) continue
                depth[pair] = depth[top] + 1
                queue[end++] = pair
            }
        }
    }
    private fun dfs(): Int {
        indices.fill(0)
        var target = 0
        var flow = 0
        while (target < freeLast) {
            val begin = 0
            var end = 0
            queue[end++] = freeNode[target]
            while (begin < end) {
                val top = queue[end - 1]
                if (top < size1) {
                    while (indices[top] < outEdge[top].size) {
                        val to = outEdge[top][indices[top]++]
                        if (to >= 0 && depth[to + size1] > depth[top]) {
                            queue[end++] = to + size1
                            break
                        }
                    }
                    if (top == queue[end - 1]) {
                        end -= 2
                    }
                }else {
                    if (matching2[top - size1] == -1) break
                    if (depth[top] < depth[matching2[top - size1]]) {
                        queue[end++] = matching2[top - size1]
                    }else {
                        --end
                    }
                }
            }
            if (begin < end) {
                ++flow
                for (i in 0 until end step 2) {
                    val node1 = queue[i]
                    val node2 = queue[i + 1] - size1
                    outEdge[node1].swap(indices[node1] - 1, outEdge[node1].lastIndex)
                    outEdge[node1][outEdge[node1].lastIndex] = matching1[node1]
                    matching1[node1] = node2
                    matching2[node2] = node1
                }
                freeNode.swap(target, --freeLast)
            }else {
                ++target
            }
        }
        return flow
    }
    fun calcMatching(): Int {
        var sum = 0
        while (true) {
            bfs()
            sum += dfs().takeIf{it > 0} ?: break
        }
        return sum
    }
    companion object {
        private fun IntArray.swap(i: Int, j: Int) {
            val temp = this[i]
            this[i] = this[j]
            this[j] = temp
        }
    }
}