package library.my.string

fun findMatch(target: String, pattern: String, table: IntArray): List<Int> {
    var i = 0
    var j = 0
    val result = mutableListOf<Int>()
    while (i + j in target.indices) {
        if (target[i + j] == pattern[j]) {
            ++j
            if (j == pattern.length) {
                result.add(i)
                i += j - table[j]
                j = maxOf(0, table[j])
            }
        }else {
            i += j - table[j]
            j = maxOf(0, table[j])
        }
    }
    return result
}
fun makeTable(pattern: String): IntArray {
    val result = IntArray(pattern.length + 1).also{it[0] = -1}
    for (i in 1 .. pattern.length) {
        var j = result[i - 1]
        while (j >= 0 && pattern[j] != pattern[i - 1]) {
            j = result[j]
        }
        result[i] = j + 1
    }
    for (i in 1 until pattern.length) {
        var j = result[i]
        while (j >= 0 && pattern[j] == pattern[i]) {
            j = result[j]
        }
        result[i] = j
    }
    return result
}