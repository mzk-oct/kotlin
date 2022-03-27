package library.my.dataStructure

class UnionFind(val size: Int) {
    private val vec = IntArray(size){-1}
    fun find(a: Int): Int {
        return if (vec[a] < 0) {
            a
        }else {
            vec[a] = find(vec[a])
            vec[a]
        }
    }
    fun same(a: Int, b: Int): Boolean {
        return find(a) == find(b)
    }
    fun unite(a: Int, b: Int): Boolean {
        val ar = find(a)
        val br = find(b)
        if (ar == br) return false
        if (vec[ar] < vec[br]) {
            vec[ar] += vec[br]
            vec[br] = ar
        }else {
            vec[br] += vec[ar]
            vec[ar] = br
        }
        return true
    }
    fun sizeOf(a: Int): Int {
        return -vec[find(a)]
    }
}