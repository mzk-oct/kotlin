package library.my.dataStructure

data class Range(val white: Int, val black: Int) {
    fun flip(): Range = Range(black, white)
    operator fun plus(other: Range): Range = Range(white + other.white, black + other.black)
}

class LazyFlipSeg private constructor(private val log: Int, private val vec: IntArray, private val sum: IntArray, private val lazy: BooleanArray) {
    private fun applyLazy(position: Int) {
        if (position == 0) return
        if (lazy[position]) {
            flip(position * 2)
            flip(position * 2 + 1)
            lazy[position] = false
        }
    }
    private fun flip(position: Int) {
        vec[position] = sum[position] - vec[position]
        if (position in lazy.indices) {
            lazy[position] = !lazy[position]
        }
    }
    private fun update(position: Int) {
        vec[position] = vec[position * 2] + vec[position * 2 + 1]
    }
    fun flip(from: Int, until: Int) {
        val left = from + lazy.size
        val right = until + lazy.size
        for (i in log downTo 1) {
            if ((left shr i) shl i != left) applyLazy(left shr i)
            if ((right shr i) shl i != right) applyLazy((right - 1) shr i)
        }
        var l = from + lazy.size
        var r = until + lazy.size
        while (l < r) {
            if (l and 1 == 1) {
                flip(l++)
            }
            if (r and 1 == 1) {
                flip(--r)
            }
            l = l shr 1
            r = r shr 1
        }
        for (i in 1 .. log) {
            if ((left shr i) shl i != left) update(left shr i)
            if ((right shr i) shl i != right) update((right - 1) shr i)
        }
    }
    fun get(from: Int, until: Int): Range {
        val left = from + lazy.size
        val right = until + lazy.size
        for (i in log downTo 1) {
            if ((left shr i) shl i != left) applyLazy(left shr i)
            if ((right shr i) shl i != right) applyLazy((right - 1) shr i)
        }
        var black = 0
        var sum = 0
        var l = from + lazy.size
        var r = until + lazy.size
        while (l < r) {
            if (l and 1 == 1) {
                sum += this.sum[l]
                black += vec[l++]
            }
            if (r and 1 == 1) {
                black += vec[--r]
                sum += this.sum[r]
            }
            l = l shr 1
            r = r shr 1
        }
        return Range(sum - black, black)
    }
    fun getAll(): Range {
        return Range(sum[1] - vec[1], vec[1])
    }
    companion object {
        fun log2Ceil(value: Int): Int {
            var result = 0
            var v = value
            while (v > 1) {
                ++result
                v = (v + 1) shr 1
            }
            return result
        }
        operator fun invoke(initial: Array<Range>): LazyFlipSeg {
            val log = log2Ceil(initial.size)
            val lazy = BooleanArray(1 shl log)
            val vec = IntArray(2 shl log)
            val sum = IntArray(2 shl log)
            for (i in initial.indices) {
                vec[i + lazy.size] = initial[i].black
                sum[i + lazy.size] = initial[i].white + initial[i].black
            }
            for (i in lazy.size - 1 downTo 1) {
                vec[i] = vec[i * 2] + vec[i * 2 + 1]
                sum[i] = sum[i * 2] + sum[i * 2 + 1]
            }
            return LazyFlipSeg(log, vec, sum, lazy)
        }
    }
}