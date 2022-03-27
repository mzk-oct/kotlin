package library.my.string

object LongestCommonPrefix {
    /**
     * @return array of the longest common prefix of suffix
     */
    fun longestCommonPrefix(str: String): IntArray {
        //result[i] => lcp(str, str[i..])
        val n = str.length
        val result = IntArray(n)
        var left = 1
        var right = 1
        for (i in 1 until n) {
            if (right < i) {
                left = i
                right = i
            }
            if (right <= i + result[i - left]) {
                var len = right - i
                while (right < n && str[right] == str[len]) {
                    ++len
                    ++right
                }
                result[i] = len
                left = i
            }else {
                result[i] = result[i - left]
            }
        }
        result[0] = n
        return result
    }
    fun String.longestCommonPrefixArray(target: String): IntArray {
        val n = length
        val result = IntArray(n)
        var left = 0
        var right = 0
        val lcp = longestCommonPrefix(target)
        for (i in 0 until n) {
            if (right < i) {
                left = i
                right = i
            }
            if (i - left == lcp.size || right <= i + lcp[i - left]) {
                var len = right - i
                while (len < target.length && right < n && target[len] == this[right]) {
                    ++len
                    ++right
                }
                result[i] = len
                left = i
            }else {
                result[i] = lcp[i - left]
            }
        }
        return result
    }
}