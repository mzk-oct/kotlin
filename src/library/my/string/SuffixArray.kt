package library.my.string

object SuffixArray {
    fun suffixArray(str: String): IntArray {
        val n = str.length
        if (n == 0) return intArrayOf()
        if (n == 1) return intArrayOf(0)
        val intArray = IntArray(n)
        val sorted = str.indices.sortedBy(str::get)
        var typeCount = 0
        for (i in 1 until sorted.size) {
            if (str[sorted[i]] != str[sorted[i - 1]]) ++typeCount
            intArray[sorted[i]] = typeCount
        }
        return suffixArray(intArray, typeCount)
    }
    private fun suffixArray(str: IntArray, typeCount: Int): IntArray {
        val n = str.size
        if (n < 10) return suffixArray(str)
        else if (n < 40) return suffixArrayDoubling(str)
        val result = IntArray(n)
        val isL = BooleanArray(n)
        for (i in n - 2 downTo 0) {
            isL[i] = if (str[i] == str[i + 1]) isL[i + 1] else str[i] < str[i + 1]
        }
        val sumL = IntArray(typeCount + 1)
        val sumS = IntArray(typeCount + 1)
        for (i in str.indices) {
            if (isL[i]) {
                ++sumL[str[i] + 1]
            }else {
                ++sumS[str[i]]
            }
        }
        for (i in sumL.indices) {
            sumS[i] += sumL[i]
            if (i < typeCount) sumL[i + 1] += sumS[i]
        }
        val lmsMap = IntArray(n + 1){-1}
        var lmsCount = 0
        for (i in 1 until n) {
            if (!isL[i - 1] && isL[i]) {
                lmsMap[i] = lmsCount++
            }
        }
        val lms = ArrayList<Int>(lmsCount)
        for (i in 1 until n) {
            if (!isL[i - 1] && isL[i]) {
                lms.add(i)
            }
        }
        val buffer = IntArray(n)
        fun induceSort(lms: List<Int>) {
            result.fill(-1)
            sumS.copyInto(buffer)
            for (s in lms) {
                result[buffer[str[s]]++] = s
            }
            sumL.copyInto(buffer)
            result[buffer[str[n - 1]]++] = n - 1
            for (v in result) {
                if (v >= 1 && !isL[v - 1]) {
                    result[buffer[str[v - 1]]++] = v - 1
                }
            }
            sumL.copyInto(buffer)
            for (i in result.indices.reversed()) {
                val v = result[i]
                if (v >= 1 && isL[v - 1]) {
                    result[--buffer[str[v - 1] + 1]] = v - 1
                }
            }
        }
        induceSort(lms)
        if (lmsCount > 0) {
            val sorted = ArrayList<Int>(lmsCount)
            for (v in result) {
                if (lmsMap[v] != -1) sorted.add(v)
            }
            val lmsSubstring = IntArray(lmsCount)
            var recTypeCount = 0
            lmsSubstring[lmsMap[sorted[0]]] = 0
            for (i in 1 until lmsCount) {
                var l = sorted[i - 1]
                var r = sorted[i]
                val endL = if (lmsMap[l] + 1 < lmsCount) lms[lmsMap[l] + 1] else n
                val endR = if (lmsMap[r] + 1 < lmsCount) lms[lmsMap[r] + 1] else n
                var same = true
                if (endL - l != endR - r) {
                    same = false
                }else {
                    while (l < endL) {
                        if (str[l] != str[r]) {
                            break
                        }
                        ++l
                        ++r
                    }
                    if (l == n || str[l] != str[r]) same = false
                }
                if (!same) ++recTypeCount
                lmsSubstring[lmsMap[sorted[i]]] = recTypeCount
            }
            val sa = suffixArray(lmsSubstring, recTypeCount)
            for (i in 0 until lmsCount) {
                sorted[i] = lms[sa[i]]
            }
            induceSort(sorted)
        }
        return result
    }
    private fun suffixArrayDoubling(str: IntArray): IntArray {
        val n = str.size
        val result = MutableList(n){it}
        var rank = str.clone()
        var rankTemp = IntArray(n)
        var len = 1
        while (len < n) {
            val comparator: (o1: Int, o2: Int) -> Int = { o1, o2 ->
                if (rank[o1] != rank[o2]) rank[o1].compareTo(rank[o2])
                else {
                    val v1 = if (o1 + len < n) rank[o1 + len] else -1
                    val v2 = if (o2 + len < n) rank[o2 + len] else -1
                    v1.compareTo(v2)
                }
            }
            result.sortWith(comparator)
            var r = 0
            rankTemp[result[0]] = 0
            for (i in 1 until n) {
                if (comparator(result[i - 1], result[i]) != 0) ++r
                rankTemp[result[i]] = r
            }
            val temp = rank
            rank = rankTemp
            rankTemp = temp
            len = len shl 1
        }
        return result.toIntArray()
    }
    private fun suffixArray(str: IntArray): IntArray {
        val n = str.size
        val result = MutableList(n){it}
        result.sortWith{ o1, o2 ->
            var i1 = o1
            var i2 = o2
            while (i1 < n && i2 < n && str[i1] == str[i2]) {
                ++i1
                ++i2
            }
            when {
                i1 == n -> -1
                i2 == n -> 1
                else -> str[i1].compareTo(str[i2])
            }
        }
        return result.toIntArray()
    }
}