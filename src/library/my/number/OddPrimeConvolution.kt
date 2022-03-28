package library.my.number

class OddPrimeConvolution(private val mod: Long) {
    private val r2: Long = R * R % mod
    private val invMod: Long = invPair(R, mod).second
    private val sumE: LongArray = LongArray(30)
    private val sumIE: LongArray = LongArray(30)
    init {
        val es = LongArray(30)
        val ies = LongArray(30)
        val cnt = Integer.numberOfTrailingZeros(mod.toInt() - 1)
        val root = primitiveRoot(mod)
        val base = powMod(root, (mod - 1) shr cnt, mod)
        var e = reduce(r2 * base)
        var ie = reduce(r2 * inv(base, mod))
        for (i in (2 .. cnt).reversed()) {
            es[i - 2] = e
            ies[i - 2] = ie
            e = reduce(e * e)
            ie = reduce(ie * ie)
        }
        e = R
        ie = R
        for (i in 0 until cnt - 2){
            sumE[i] = reduce(es[i] * e)
            e = reduce(e * ies[i])
            sumIE[i] = reduce(ies[i] * ie)
            ie = reduce(ie * es[i])
        }
    }
    private inline fun reduce(a: Long): Long {
        val a = ((((((a and Mask) * invMod) and Mask) * mod + a) shr Shift))
        return if (a < mod) a else a - mod
    }
    private inline fun modMinus(a: Long): Long {
        return if (0 <= a) a else a + mod
    }
    private inline fun modPlus(a: Long): Long {
        return if (a < mod) a else a - mod
    }
    companion object {
        const val Shift = 31
        const val R = 1L shl Shift
        const val Mask = R - 1
        fun primitiveRoot(mod: Long): Long {
            if (mod == 998244353L) return 3
            if (mod == 167772161L) return 3
            if (mod == 469762049L) return 3
            if (mod == 754974721L) return 11
            val divs = IntArray(20)
            divs[0] = 2
            var cnt = 1
            var x = (mod - 1) shr 1
            while (x and 1 == 1L) x = x shr 1
            var i = 3L
            while (i * i <= x) {
                if (x % i == 0L) {
                    divs[cnt++] = i.toInt()
                    while (x % i == 0L) {
                        x /= i
                    }
                }
                i += 2
            }
            if (x > 1) {
                divs[cnt++] = x.toInt()
            }
            var g = 2L
            while (true) {
                var ok = true
                for (i in 0 until cnt) {
                    if (powMod(g, (mod - 1) / divs[i], mod) == 1L) {
                        ok = false
                        break
                    }
                }
                if (ok) return g
                ++g
            }
        }
        fun powMod(base: Long, exp: Long, mod: Long): Long {
            var result = 1L
            var b = base
            var e = exp
            while (e > 0) {
                if ((e and 1) == 1L) result = result * b % mod
                b = b * b % mod
                e = e shr 1
            }
            return result
        }
        fun inv(value: Long, mod: Long): Long {
            return invPair(value, mod).first.let{if (it >= 0) it else it + mod}
        }
        fun invPair(a: Long, b: Long): Pair<Long, Long> {
            val (x, y) = invGcd(a, b)
            return (if (x < 0) x + b else x) to (if (y < 0) -y else a - y)
        }
        fun gcd(a: Long, b: Long): Long {
            return when (b) {
                0L -> a
                else -> gcd(b, a % b)
            }
        }
        fun invGcd(a: Long, b: Long): Pair<Long, Long> {
            if (b == 0L) return 1L to 0L
            val q = a / b
            val (x, y) = invGcd(b, a % b)
            return y to x - y * q
        }
        private fun ceilPow2(value: Int): Int {
            var r = 0
            while (1L shl r < value) ++r
            return r
        }
    }
    private fun butterfly(a: LongArray) {
        val n = a.size
        val h = ceilPow2(n)
        val se = sumE
        for (ph in 1..h) {
            val w = 1 shl (ph - 1)
            val p = 1 shl (h - ph)
            var now = reduce(r2)
            for (s in 0 until w) {
                val offset = s shl (h - ph + 1)
                for (i in 0 until p) {
                    val left = a[i + offset]
                    val right = reduce(a[i + offset + p] * now)
                    a[i + offset] = modPlus(left + right)
                    a[i + offset + p] = modMinus(left - right)
                }
                now = reduce(now * se[Integer.numberOfTrailingZeros(s.inv())])
            }
        }
    }
    private fun butterflyInv(a: LongArray) {
        val n = a.size
        val h = ceilPow2(n)
        val sie = sumIE
        for (ph in (1 .. h).reversed()) {
            val w = 1 shl (ph - 1)
            val p = 1 shl (h - ph)
            var now = reduce(r2)
            for (s in 0 until w) {
                val offset = s shl (h - ph + 1)
                for (i in 0 until p) {
                    val left = a[i + offset]
                    val right = a[i + offset + p]
                    a[i + offset] = modPlus(left + right)
                    a[i + offset + p] = reduce(now * (left - right + mod))
                }
                now = reduce(now * sie[Integer.numberOfTrailingZeros(s.inv())])
            }
        }
    }
    fun convolution(a: LongArray, b: LongArray): LongArray {
        val n = a.size
        val m = b.size
        if (n == 0 || m == 0) return LongArray(0)
        val z = 1 shl ceilPow2(n + m - 1)
        val ra = LongArray(z)
        val rb = LongArray(z)
        for (i in 0 until n) {
            ra[i] = reduce(r2 * a[i])
        }
        for (i in 0 until m) {
            rb[i] = reduce(r2 * b[i])
        }
        butterfly(ra)
        butterfly(rb)
        for (i in 0 until z) {
            ra[i] = reduce(ra[i] * rb[i])
        }
        butterflyInv(ra)
        val result = ra.copyOf(n + m - 1)
        val invZ = reduce(r2 * inv(z.toLong(), mod))
        for (i in result.indices) {
            result[i] = reduce(reduce(result[i] * invZ))
        }
        return result
    }
    fun convolution(a: IntArray, b: IntArray): LongArray {
        val n = a.size
        val m = b.size
        if (n == 0 || m == 0) return LongArray(0)
        val z = 1 shl ceilPow2(n + m - 1)
        val ra = LongArray(z)
        val rb = LongArray(z)
        for (i in 0 until n) {
            ra[i] = reduce(r2 * a[i])
        }
        for (i in 0 until m) {
            rb[i] = reduce(r2 * b[i])
        }
        butterfly(ra)
        butterfly(rb)
        for (i in 0 until z) {
            ra[i] = reduce(ra[i] * rb[i])
        }
        butterflyInv(ra)
        val result = ra.copyOf(n + m - 1)
        val invZ = reduce(r2 * inv(z.toLong(), mod))
        for (i in result.indices) {
            result[i] = reduce(reduce(result[i] * invZ))
        }
        return result
    }
}