package library.my.string

object LCPArray {
    fun lcpArray(str: String, suffixArray: IntArray): IntArray {
        val n = str.length
        require(n > 0)
        if (n == 1) return intArrayOf()
        val length = IntArray(n)
        for (i in 0 until n - 1) {
            length[suffixArray[i]] = suffixArray[i + 1]
        }
        length[suffixArray.last()] = n
        var len = 0
        for (i in 0 until n) {
            if (len > 0) --len
            val j = length[i]
            while (i + len < n && j + len < n && str[i + len] == str[j + len]) {
                ++len
            }
            length[i] = len
        }
        return IntArray(n - 1){length[suffixArray[it]]}
    }
    fun lcpArrayKasai(str: String, suffixArray: IntArray): IntArray {
        val n = str.length
        val rank = IntArray(n)
        for (i in suffixArray.indices) {
            rank[suffixArray[i]] = i
        }
        val result = IntArray(n - 1)
        var len = 0
        for (i in 0 until n) {
            if (len > 0) --len
            if (rank[i] == 0) continue
            val j = suffixArray[rank[i] - 1]
            while (i + len < n && j + len < n && str[i + len] == str[j + len]) {
                ++len
            }
            result[rank[i] - 1] = len
        }
        return result
    }
}