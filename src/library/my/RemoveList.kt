package library.my

class RemoveList(val size: Int) {
    private val next = (1 .. size + 2).toList().toIntArray()
    private val prev = (-1 .. size).toList().toIntArray()
    fun remove(node: Int) {
        val i = node + 1
        prev[next[i]] = prev[i]
        next[prev[i]] = next[i]
    }
    fun nextNode(node: Int): Int {
        return next[node + 1] - 1
    }
    fun prevNode(node: Int): Int {
        return prev[node + 1] - 1
    }
}