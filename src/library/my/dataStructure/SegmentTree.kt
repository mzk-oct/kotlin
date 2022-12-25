package library.my


interface ISegmentTree {
    fun get(from: Int, until: Int): Int
    fun get(position: Int): Int
    fun set(position: Int, value: Int)
}

abstract class SegmentTreeBase<T>(val size: Int) {
    protected val log = log2Ceil(size)
    protected abstract val e: T
    protected abstract fun reduceLeft(leftPos: Int, right: T): T
    protected abstract fun reduceRight(left: T, rightPos: Int): T
    protected abstract fun changeValue(position: Int, value: T)
    protected abstract fun update(position: Int)
    fun get(from: Int, until: Int): T {
        var result = e
        var l = from + (1 shl log)
        var r = until + (1 shl log)
        while (l < r) {
            if (l and 1 == 1) {
                result = reduceLeft(l++, result)
            }
            if (r and 1 == 1) {
                result = reduceRight(result, --r)
            }
            l = l shr 1
            r = r shr 1
        }
        return result
    }
    fun get(position: Int): T {
        return reduceLeft(position + (1 shl log), e)
    }
    fun set(position: Int, value: T) {
        val pos = position + (1 shl log)
        changeValue(pos, value)
        for (i in 1 .. log) {
            update(pos shr i)
        }
    }
    companion object {
        fun log2Ceil(value: Int): Int {
            var l = 0
            while (1 shl l < value) {
                ++l
            }
            return l
        }
    }
}

abstract class SegmentTree<T>(val size: Int, private val vec: Array<T>) {
    private val log = log2Ceil(size)
    protected abstract val e: T
    protected abstract fun reduce(left: T, right: T): T
    private fun update(position: Int) {
        vec[position] = reduce(vec[position shl 1], vec[(position shl 1) + 1])
    }
    fun get(from: Int, until: Int): T {
        var result = e
        var l = from + (1 shl log)
        var r = until + (1 shl log)
        while (l < r) {
            if (l and 1 == 1) {
                result = reduce(vec[l++], result)
            }
            if (r and 1 == 1) {
                result = reduce(result, vec[--r])
            }
            l = l shr 1
            r = r shr 1
        }
        return result
    }
    fun get(position: Int): T {
        return reduce(vec[position + (1 shl log)], e)
    }
    fun set(position: Int, value: T) {
        val pos = position + (1 shl log)
        vec[pos] = value
        for (i in 1 .. log) {
            update(pos shr i)
        }
    }
    fun all(): T = vec[1]
    companion object {
        fun log2Ceil(value: Int): Int {
            var l = 0
            while (1 shl l < value) {
                ++l
            }
            return l
        }
    }
}


//
//typealias V = Long
//class SegmentTreeMax constructor(val size: Int, initial: LongArray) {
//    constructor(size: Int): this(size, longArrayOf())
//    constructor(initial: LongArray): this(initial.size, initial)
//    private val log = log2Ceil(size)
//    private val vec = LongArray(2 shl log){e}
//    init {
//        if (initial.isNotEmpty()) {
//            for (i in initial.indices) {
//                vec[i + (1 shl log)] = initial[i]
//            }
//            for (i in (1 shl log) - 1 downTo 1) {
//                vec[i] = plus(vec[i shl 1], vec[(i shl 1) + 1])
//            }
//        }
//    }
//    private val e: V = Long.MIN_VALUE
//    private fun plus(left: V, right: V): V = maxOf(left, right)
//    fun get(from: Int, until: Int): V {
//        var result = e
//        var l = from + (1 shl log)
//        var r = until + (1 shl log)
//        while (l < r) {
//            if (l and 1 == 1) {
//                result = plus(vec[l++], result)
//            }
//            if (r and 1 == 1) {
//                result = plus(result, vec[--r])
//            }
//            l = l shr 1
//            r = r shr 1
//        }
//        return result
//    }
//    fun get(position: Int): V {
//        return vec[position + (1 shl log)]
//    }
//    fun set(position: Int, value: V) {
//        val pos = position + (1 shl log)
//        vec[pos] = value
//        for (i in 1 .. log) {
//            vec[pos shr i] = plus(vec[(pos shr i shl 1)], vec[(pos shr i shl 1) + 1])
//        }
//    }
//    companion object {
//        fun log2Ceil(value: Int): Int {
//            var l = 0
//            while (1 shl l < value) {
//                ++l
//            }
//            return l
//        }
//    }
//}
typealias V = Int
abstract class SegmentTreeI protected constructor(val size: Int, initial: IntArray) {
    constructor(size: Int): this(size, intArrayOf())
    constructor(initial: IntArray): this(initial.size, initial)
    private val log = log2Ceil(size)
    private val vec = IntArray(2 shl log){e}
    init {
        if (initial.isNotEmpty()) {
            for (i in initial.indices) {
                vec[i + (1 shl log)] = initial[i]
            }
            for (i in (1 shl log) - 1 downTo 1) {
                vec[i] = plus(vec[i shl 1], vec[(i shl 1) + 1])
            }
        }
    }
    protected abstract fun plus(left: V, right: V): V
    fun get(from: Int, until: Int): V {
        var result = e
        var l = from + (1 shl log)
        var r = until + (1 shl log)
        while (l < r) {
            if (l and 1 == 1) {
                result = plus(vec[l++], result)
            }
            if (r and 1 == 1) {
                result = plus(result, vec[--r])
            }
            l = l shr 1
            r = r shr 1
        }
        return result
    }
    fun get(position: Int): V {
        return vec[position + (1 shl log)]
    }
    fun set(position: Int, value: V) {
        val pos = position + (1 shl log)
        vec[pos] = value
        for (i in 1 .. log) {
            vec[pos shr i] = plus(vec[(pos shr i shl 1)], vec[(pos shr i shl 1) + 1])
        }
    }
    fun searchRight(left: Int, predicate: (V) -> Boolean): Int? {//null || [left, size]
        if (!predicate(e)) return null
        var value = e
        var idx = left + (1 shl log)
        do {
            while (idx and 1 == 0) idx = idx shr 1
            val v = plus(value, vec[idx])
            if (predicate(v)){
                value = v
                ++idx
            }else {
                while (idx < 1 shl log) {
                    idx = idx shl 1
                    val v = plus(value, vec[idx])
                    if (predicate(v)) {
                        value = v
                        ++idx
                    }
                }
                return idx - (1 shl log)
            }
        }while (idx and -idx != idx)
        return size
    }
    fun searchLeft(right: Int, predicate: (V) -> Boolean): Int? {//null || [0, right]
        if (!predicate(e)) return null
        var value = e
        var idx = right + (1 shl log)
        do {
            while (idx and 1 == 0) idx = idx shr 1
            val v = plus(vec[--idx], value)
            if (predicate(v)) {
                value = v
            }else {
                while (idx < 1 shl log) {
                    idx = (idx shl 1) + 1
                    val v = plus(vec[idx], value)
                    if (predicate(v)) {
                        value = v
                        --idx
                    }
                }
                return idx + 1 - (1 shl log)
            }
        }while (idx and -idx != idx)
        return 0
    }
    companion object {
        protected val e: Int = TODO()
        fun log2Ceil(value: Int): Int {
            var l = 0
            while (1 shl l < value) {
                ++l
            }
            return l
        }
    }
}
