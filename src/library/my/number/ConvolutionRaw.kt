package library.my.number


class ConvolutionRaw(val mod: Int) {
    private val pRoot = primitiveRoot(mod)
    private val sumE = makeSumE(mod, pRoot)
    private val sumIE = makeSumIE(mod, pRoot)
    init {
        //println("sumE: ${sumE.joinToString(" ")}")
        //println("sumIE: ${sumIE.joinToString(" ")}")
    }
    private fun butterflyInv(a: LongArray) {
        val n = a.size
        val h = ceilPow2(n)
        for (ph in h downTo 1) {
            val w = 1 shl ph - 1
            val p = 1 shl h - ph
            var inow: Long = 1
            for (s in 0 until w) {
                val offset = s shl h - ph + 1
                for (i in 0 until p) {
                    val l = a[i + offset]
                    val r = a[i + offset + p]
                    a[i + offset] = (l + r) % mod
                    a[i + offset + p] = (mod + l - r) * inow % mod
                }
                val x = Integer.numberOfTrailingZeros(s.inv())
                inow = inow * sumIE[x] % mod
            }
        }
    }

    private fun butterfly(a: LongArray) {
        val n = a.size
        val h = ceilPow2(n)
        for (ph in 1..h) {
            val w = 1 shl ph - 1
            val p = 1 shl h - ph
            var now: Long = 1
            for (s in 0 until w) {
                val offset = s shl h - ph + 1
                for (i in 0 until p) {
                    val l = a[i + offset]
                    val r = a[i + offset + p] * now % mod
                    a[i + offset] = (l + r) % mod
                    a[i + offset + p] = (l - r + mod) % mod
                }
                val x = Integer.numberOfTrailingZeros(s.inv())
                now = now * sumE[x] % mod
            }
        }
    }

    fun convolution(a: LongArray, b: LongArray): LongArray {
        val n = a.size
        val m = b.size
        if (n == 0 || m == 0) return LongArray(0)
        val z = 1 shl ceilPow2(n + m - 1)
        var ra = LongArray(z)
        val rb = LongArray(z)
        System.arraycopy(a, 0, ra, 0, n)
        System.arraycopy(b, 0, rb, 0, m)
        butterfly(ra)
        butterfly(rb)
        println("ra: " + ra.joinToString(" "))
        println("rb: " + rb.joinToString(" "))
        for (i in 0 until z) {
            ra[i] = ra[i] * rb[i] % mod
        }
        println("raxb: " + ra.joinToString(" "))
        butterflyInv(ra)
        ra = ra.copyOf(n + m - 1)
        println("rc: " + ra.joinToString(" "))
        val iz = pow(z.toLong(), mod - 2.toLong(), mod)
        println("invZ: $iz")
        for (i in 0 until n + m - 1) ra[i] = ra[i] * iz % mod
        println("result: " + ra.joinToString(" "))
        return ra
    }
    companion object {

        private fun primitiveRoot(m: Int): Int {
            if (m == 2) return 1
            if (m == 167772161) return 3
            if (m == 469762049) return 3
            if (m == 754974721) return 11
            if (m == 998244353) return 3
            val divs = IntArray(20)
            divs[0] = 2
            var cnt = 1
            var x = (m - 1) / 2
            while (x % 2 == 0) x /= 2
            var i = 3
            while (i.toLong() * i <= x) {
                if (x % i == 0) {
                    divs[cnt++] = i
                    while (x % i == 0) {
                        x /= i
                    }
                }
                i += 2
            }
            if (x > 1) {
                divs[cnt++] = x
            }
            var g = 2
            while (true) {
                var ok = true
                for (p in divs) {
                    if (pow(g.toLong(), (m - 1L) / p, m) == 1L) {
                        ok = false
                        break
                    }
                }
                if (ok) return g
                g++
            }
        }
        private fun pow(x: Long, n: Long, m: Int): Long {
            var e = n
            if (m == 1) return 0
            var r: Long = 1
            var y = x % m
            while (e > 0) {
                if (e and 1 != 0L) r = r * y % m
                y = y * y % m
                e = e shr 1
            }
            return r
        }

        private fun ceilPow2(n: Int): Int {
            var x = 0
            while (1L shl x < n) x++
            return x
        }


        private fun makeSumE(mod: Int, g: Int): LongArray {
            val sumE = LongArray(30)
            val es = LongArray(30)
            val ies = LongArray(30)
            val cnt2 = Integer.numberOfTrailingZeros(mod - 1)
            var e = pow(g.toLong(), (mod - 1 shr cnt2.toLong().toInt()).toLong(), mod)
            var ie = pow(e, mod - 2.toLong(), mod)
            for (i in cnt2 downTo 2) {
                es[i - 2] = e
                ies[i - 2] = ie
                e = e * e % mod
                ie = ie * ie % mod
            }
            var now: Long = 1
            for (i in 0 until cnt2 - 2) {
                sumE[i] = es[i] * now % mod
                now = now * ies[i] % mod
            }
            return sumE
        }
        private fun makeSumIE(mod: Int, g: Int): LongArray {
            val sumIE = LongArray(30)
            val es = LongArray(30)
            val ies = LongArray(30)
            val cnt2 = Integer.numberOfTrailingZeros(mod - 1)
            var e = pow(g.toLong(), (mod - 1 shr cnt2.toLong().toInt()).toLong(), mod)
            var ie = pow(e, mod - 2.toLong(), mod)
            for (i in cnt2 downTo 2) {
                es[i - 2] = e
                ies[i - 2] = ie
                e = e * e % mod
                ie = ie * ie % mod
            }
            var now: Long = 1
            for (i in 0 until cnt2 - 2) {
                sumIE[i] = ies[i] * now % mod
                now = now * es[i] % mod
            }
            return sumIE
        }
    }
}