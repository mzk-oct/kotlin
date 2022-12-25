package library.my.dataStructure
typealias V = Long
typealias M = Long
class LazySeg(val size: Int, initial: IntArray) {
    constructor(initial: IntArray): this(initial.size, initial)
    constructor(size: Int): this(size, intArrayOf())
    private val log = log2Ceil(size)
    private val lazy = LongArray(1 shl log){EF}
    private val vec = LongArray(2 shl log){ES}.also{
        if (initial.isNotEmpty()) {
            for (i in 0 until size) {
                it[i + (1 shl log)] = initial[i].toLong()
            }
        }
    }
    init {
        for (i in (1 shl log) - 1 downTo 1) {
            update(i)
        }
    }
    fun set(position: Int, value: V) {
        val pos = position + (1 shl log)
        for (i in log downTo 1) {
            applyLazy(pos shr i)
        }
        vec[pos] = value
        for (i in 1 .. log) {
            update(pos shr i)
        }
    }
    fun update(position: Int, func: M) {
        val pos = position + (1 shl log)
        for (i in log downTo 1) {
            applyLazy(pos shr i)
        }
        vec[pos] = map(vec[pos], func)
        for (i in 1 .. log) {
            update(pos shr i)
        }
    }
    fun update(from: Int, until: Int, func: M) {
        if (from == until) return
        var l = from + (1 shl log)
        var r = until + (1 shl log)
        for (i in log downTo 1) {
            if ((l shr i) shl i != l) applyLazy(l shr i)
            if ((r shr i) shl i != r) applyLazy(r shr i)
        }
        while (l < r) {
            if (l and 1 == 1) appendLazy(l++, func)
            if (r and 1 == 1) appendLazy(--r, func)
            l = l shr 1
            r = r shr 1
        }
        l = from + (1 shl log)
        r = until + (1 shl log)
        for (i in 1 .. log) {
            if ((l shr i) shl i != l) update(l shr i)
            if ((r shr i) shl i != r) update(r shr i)
        }
    }
    fun get(position: Int): V {
        val pos = position + (1 shl log)
        for (i in log downTo 1) {
            applyLazy(pos shr i)
        }
        return vec[pos]
    }
    fun get(from: Int, until: Int): V {
        if (from == until) return ES
        var l = from + (1 shl log)
        var r = until + (1 shl log)
        for (i in log downTo 1) {
            if ((l shr i) shl i != l) applyLazy(l shr i)
            if ((r shr i) shl i != r) applyLazy(r shr i)
        }
        var sumLeft = ES
        var sumRight = ES
        while (l < r) {
            if (l and 1 == 1) sumLeft = plus(sumLeft, vec[l++])
            if (r and 1 == 1) sumRight = plus(vec[--r], sumRight)
            l = l shr 1
            r = r shr 1
        }
        return plus(sumLeft, sumRight)
    }
    fun searchRight(left: Int, predicate: (V) -> Boolean): Int? {
        if (!predicate(ES)) return null
        if (left == size) return size
        var idx = left + (1 shl log)
        for (i in log downTo 1) {
            if ((idx shr i) shl i != idx) applyLazy(idx shr i)
        }
        var value = ES
        do {
            while (idx and 1 == 0) idx = idx shr 1
            val nextValue = plus(value, vec[idx])
            if (predicate(nextValue)) {
                value = nextValue
                ++idx
            }else {
                while (idx < 1 shl log) {
                    applyLazy(idx)
                    idx = idx shl 1
                    val next = plus(value, vec[idx])
                    if (predicate(next)) {
                        value = next
                        ++idx
                    }
                }
                return idx - (1 shl log)
            }
        }while (idx and -idx != idx)
        return size
    }
    fun searchLeft(right: Int, predicate: (V) -> Boolean): Int? {
        if (!predicate(ES)) return null
        if (right == 0) return 0
        var idx = right + (1 shl log)
        for (i in log downTo 1) {
            if ((idx shr i) shl i != idx) applyLazy(idx shr i)
        }
        var value = ES
        do {
            while (idx and 1 == 0) idx = idx shr 1
            val nextValue = plus(vec[--idx], value)
            if (predicate(nextValue)) {
                value = nextValue
            }else {
                while (idx < 1 shl log) {
                    applyLazy(idx)
                    idx = (idx shl 1) + 1
                    val next = plus(vec[idx], value)
                    if (predicate(next)) {
                        value = next
                        --idx
                    }
                }
                return idx + 1 - (1 shl log)
            }
        }while (idx and -idx != idx)
        return 0
    }
    fun getAll(): V {
        return vec[1]
    }
    private fun update(position: Int) {
        vec[position] = plus(vec[position shl 1], vec[(position shl 1) + 1])
    }
    private fun applyLazy(position: Int) {
        val func = lazy[position]
        appendLazy(position shl 1, func)
        appendLazy((position shl 1) + 1, func)
        lazy[position] = EF
    }
    private fun appendLazy(position: Int, func: M) {
        if (position < 1 shl log) {
            lazy[position] = composite(func, lazy[position])
        }
        vec[position] = map(vec[position], func)
    }
    private fun map(value: V, func: M): V = TODO()
    private fun plus(left: V, right: V): V = TODO()
    private fun composite(new: M, old: M): M = TODO()
    companion object {
        fun log2Ceil(value: Int): Int {
            var result = 0
            while (1 shl result < value) {
                ++result
            }
            return result
        }
        private val ES: V = TODO()
        private val EF: M = TODO()
    }
}