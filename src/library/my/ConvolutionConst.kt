package library.my

class ConvolutionConst() {
    private val sumE: LongArray = LongArray(30)
    private val sumIE: LongArray = LongArray(30)
    init {
        val es = LongArray(30)
        val ies = LongArray(30)
        val cnt = Integer.numberOfTrailingZeros(MOD - 1)
        val root = primitiveRoot(MOD.toLong())
        val base = powMod(root, (MOD - 1L) shr cnt)
        var e = base
        var ie = inv(base, MOD.toLong())
        for (i in (2 .. cnt).reversed()) {
            es[i - 2] = e
            ies[i - 2] = ie
            e = e * e % MOD
            ie = ie * ie % MOD
        }
        e = 1
        ie = 1
        for (i in 0 until cnt - 2){
            sumE[i] = es[i] * e % MOD
            e = e * ies[i] % MOD
            sumIE[i] = ies[i] * ie % MOD
            ie = ie * es[i] % MOD
        }
    }
    companion object {
        const val MOD = 998244353
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
                    if (powMod(g, (MOD - 1L) / divs[i]) == 1L) {
                        ok = false
                        break
                    }
                }
                if (ok) return g
                ++g
            }
        }
        fun powMod(base: Long, exp: Long): Long {
            var result = 1L
            var b = base
            var e = exp
            while (e > 0) {
                if ((e and 1) == 1L) result = result * b % MOD
                b = b * b % MOD
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
            var now = 1L
            for (s in 0 until w) {
                val offset = s shl (h - ph + 1)
                for (i in 0 until p) {
                    val left = a[i + offset]
                    val right = a[i + offset + p] * now % MOD
                    a[i + offset] = (left + right) % MOD
                    a[i + offset + p] = (left - right + MOD) % MOD
                }
                now = now * se[Integer.numberOfTrailingZeros(s.inv())] % MOD
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
            var now = 1L
            for (s in 0 until w) {
                val offset = s shl (h - ph + 1)
                for (i in 0 until p) {
                    val left = a[i + offset]
                    val right = a[i + offset + p]
                    a[i + offset] = (left + right) % MOD
                    a[i + offset + p] = now * (left - right + MOD) % MOD
                }
                now = now * sie[Integer.numberOfTrailingZeros(s.inv())] % MOD
            }
        }
    }
    fun convolution(a: IntArray, b: IntArray): LongArray {
        val n = a.size
        val m = b.size
        if (n == 0 || m == 0) return LongArray(0)
        val z = 1 shl ceilPow2(n + m - 1)
        val ra = LongArray(z)
        val rb = LongArray(z)
        for (i in 0 until n) {
            ra[i] = a[i].toLong()
        }
        for (i in 0 until m) {
            rb[i] = b[i].toLong()
        }
        butterfly(ra)
        butterfly(rb)
        for (i in 0 until z) {
            ra[i] = ra[i] * rb[i] % MOD
        }
        butterflyInv(ra)
        val result = ra.copyOf(n + m - 1)
        val invZ = inv(z.toLong(), MOD.toLong())
        for (i in result.indices) {
            result[i] = result[i] * invZ % MOD
        }
        return result
    }
    fun convolution(a: LongArray, b: LongArray): LongArray {
        val n = a.size
        val m = b.size
        if (n == 0 || m == 0) return LongArray(0)
        val z = 1 shl ceilPow2(n + m - 1)
        val ra = LongArray(z).also { a.copyInto(it) }
        val rb = LongArray(z).also { b.copyInto(it) }
        butterfly(ra)
        butterfly(rb)
        for (i in 0 until z) {
            ra[i] = ra[i] * rb[i] % MOD
        }
        butterflyInv(ra)
        val result = ra.copyOf(n + m - 1)
        val invZ = inv(z.toLong(), MOD.toLong())
        for (i in result.indices) {
            result[i] = result[i] * invZ % MOD
        }
        return result
    }
}
