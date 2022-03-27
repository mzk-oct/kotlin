package library.my.string

object ZAlgorithm {
    fun zAlgorithm(str: String): IntArray {
        val n = str.length
        if (n == 0) return intArrayOf()
        if (n == 1) return intArrayOf(1)
        val result = IntArray(n)
        var left = 1
        var right = 1
        for (i in 1 until n) {
            right = maxOf(right, i)
            if (result[i - left] + i >= right) {
                left = i
                while (right < n && str[right - left] == str[right]) {
                    ++right
                }
                result[i] = right - i
            }else {
                result[i] = result[i - left]
            }
        }
        result[0] = n
        return result
    }
}