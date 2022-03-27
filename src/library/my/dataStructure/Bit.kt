package library.my.dataStructure



abstract class Bit(val size: Int, private val vec: IntArray) {
    abstract fun plus(left: Int, right: Int): Int
    protected abstract val e: Int
    fun get(position: Int): Int {
        var result = e
        var pos = position
        while (pos >= 0) {
            result = plus(vec[pos], result)
            pos -= pos.inv() and (pos + 1)
        }
        return result
    }
    fun add(position: Int, value: Int) {
        var pos = position
        while (pos < size) {
            vec[pos] = plus(value, vec[pos])
            pos += pos.inv() and (pos + 1)
        }
    }

    fun upperBound(value: Int): Int {
        var left = Int.MAX_VALUE
        val log = size.toString(2).length - 1
        var pos = 0
        for (i in log downTo 0) {
            if ((pos + (1 shl i)) > size) continue
            val v = plus(left, vec[pos + (1 shl i) - 1])
            if (v <= value) {
                pos += 1 shl i
                left = v
            }
        }
        return pos
    }
}
