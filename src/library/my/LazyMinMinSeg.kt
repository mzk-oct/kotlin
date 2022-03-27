package library.my

class LazyMinMinSeg private constructor(val size: Int, private val log: Int, private val vec: LongArray, private val lazy: LongArray) {
    private fun _applyLazy(position: Int) {
        if (position == 0) return
        _applyLazy(position shr 1)
        if (position in lazy.indices && lazy[position] != Long.MAX_VALUE) {
            _changeMin(position * 2, lazy[position])
            _changeMin(position * 2 + 1, lazy[position])
            lazy[position] = Long.MAX_VALUE
        }
    }
    private fun _changeMin(position: Int, value: Long) {
        vec[position] = minOf(vec[position], value)
        if (position in lazy.indices) {
            lazy[position] = minOf(lazy[position], value)
        }
    }
    private fun _update(position: Int) {
        if (position == 0) return
        vec[position] = minOf(lazy[position], minOf(vec[position * 2], vec[position * 2 + 1]))
        _update(position shr 1)
    }
    fun set(from: Int, until: Int, value: Long) {
        _applyLazy(from + lazy.size)
        _applyLazy(until - 1 + lazy.size)
        var l = from + lazy.size
        var r = until + lazy.size
        while (l < r) {
            if ((l and 1) == 1) {
                _changeMin(l++, value)
            }
            if ((r and 1) == 1) {
                _changeMin(--r, value)
            }
            l = l shr 1
            r = r shr 1
        }
        _update((from + lazy.size) shr 1)
        _update((until - 1 + lazy.size) shr 1)
    }
    fun get(from: Int, until: Int): Long {
        _applyLazy(from + lazy.size)
        _applyLazy(until - 1 + lazy.size)
        var result = Long.MAX_VALUE
        var l = from + lazy.size
        var r = until + lazy.size
        while (l < r) {
            if ((l and 1) == 1) {
                result = minOf(result, vec[l++])
            }
            if ((r and 1) == 1) {
                result = minOf(result, vec[--r])
            }
            l = l shr 1
            r = r shr 1
        }
        return result
    }
    fun set(position: Int, value: Long) {
        _applyLazy(position + lazy.size)
        _changeMin(position + lazy.size, value)
        _update((position + lazy.size) shr 1)
    }
    fun get(position: Int): Long {
        _applyLazy(position + lazy.size)
        return vec[position + lazy.size]
    }
    companion object {
        fun log2Ceil(value: Int): Int {
            var v = value
            var result = 0
            while (v > 1) {
                ++result
                v = (v + 1) shr 1
            }
            return result
        }
        operator fun invoke(size: Int): LazyMinMinSeg {
            val log = log2Ceil(size)
            val vec = LongArray(2 shl log){Long.MAX_VALUE}
            val lazy = LongArray(1 shl log){Long.MAX_VALUE}
            return LazyMinMinSeg(size, log, vec, lazy)
        }
    }
}
