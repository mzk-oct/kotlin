package library.my.number

object NextPermutation {
    fun IntArray.nextPermutation(): Boolean {
        val last = (0 until lastIndex).findLast { this[it] < this[it + 1] } ?: return false
        val nextHead = (last + 1 until size).findLast { this[last] < this[it] } ?: lastIndex
        val value = this[nextHead]
        this[nextHead] = this[last]
        var i = last + 1
        var j = lastIndex
        while (i < j) {
            val temp = this[i]
            this[i] = this[j]
            this[j] = temp
            i += 1
            j -= 1
        }
        this[last] = value
        return true
    }
}
