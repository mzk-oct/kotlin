package library.my

class LazyAddMinSeg private constructor(val size: Int, private val vec: LongArray) {
    private val half: Int = vec.size shr 1
    private val lazy = LongArray(vec.size)
    constructor(size: Int, default: Long): this(size, LongArray(2 shl log2Ceil(size)){default})
    constructor(size: Int): this(size, Long.MAX_VALUE)
    constructor(initial: LongArray): this(initial.size) {
        for (i in initial.indices) {
            vec[i + half] = initial[i]
        }
        for (i in half shr 1 until half) {
            update(i)
        }
    }
    fun getMin(from: Int, until: Int): Long {
        var l = from + half
        var r = until + half
        apply(l)
        apply(r - 1)
        var result = Long.MAX_VALUE
        while (l < r) {
            if (l and 1 == 1) {
                result = minOf(result, vec[l] + lazy[l])
                ++l
            }
            if (r and 1 == 1) {
                --r
                result = minOf(result, vec[r] + lazy[r])
            }
            l = l shr 1
            r = r shr 1
        }
        return result
    }
    fun getValue(position: Int): Long {
        update(position + half)
        return vec[position + half]
    }
    fun set(position: Int, value: Long) {
        apply(half + position)
        vec[half + position] = value
        lazy[half + position] = 0
        update((half + position) shr 1)
    }
    fun add(from: Int, until: Int, value: Long) {
        var l = half + from
        var r = half + until
        apply(l)
        apply(r - 1)
        do {
            if (l and 1 == 1) {
                lazy[l++] += value
            }
            if (r and 1 == 1) {
                lazy[--r] += value
            }
            l = l shr 1
            r = r shr 1
        }while (l < r)
        update((half + from) shr 1)
        update((half + until - 1) shr 1)
    }
    private fun apply(position: Int) {
        if (position == 0) return
        apply(position shr 1)
        if (position in lazy.indices && lazy[position] != 0L) {
            vec[position] += lazy[position]
            if ((position shl 1) in lazy.indices) {
                lazy[position shl 1] += lazy[position]
                lazy[(position shl 1) + 1] += lazy[position]
            }
            lazy[position] = 0
        }
    }
    private tailrec fun update(position: Int) {
        if (position == 0) return
        val c = position shl 1
        vec[position] = minOf(vec[c] + lazy[c], vec[c + 1] + lazy[c + 1])
        update(position shr 1)
    }
    companion object {
        private fun log2Ceil(value: Int): Int {
            return if (value <= 1) 0 else 1 + log2Ceil((value + 1) shr 1)
        }
    }
}