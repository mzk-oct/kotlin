package library.my.number

object MillerRabin {
    private val tester = intArrayOf(2, 3, 5, 7, 11, 13, 17)
    private fun powMod(base: Int, exp: Int, mod: Int): Long {
        var result = 1L
        var b = base.toLong() % mod
        var e = exp
        while (e > 0) {
            if (e and 1 != 0) {
                result = result * b % mod
            }
            b = b * b % mod
            e = e shr 1
        }
        return result
    }
    fun checkPrime(suspect: Int): Boolean {
        if (suspect <= 1) return false
        if (suspect and 1 == 0) return suspect == 2
        var d = suspect - 1
        var s = 0
        while (d and 1 == 0){
            s += 1
            d = d shr 1
        }
        for (t in tester) {
            if (suspect <= t) break
            var a = powMod(t, d, suspect)
            if (a == 1L || a == suspect - 1L) continue
            for (i in 1 until s) {
                a = a * a % suspect
                if (a == suspect - 1L) break
            }
            if (a != suspect - 1L) return false
        }
        return true
    }
}