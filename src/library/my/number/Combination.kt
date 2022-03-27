package library.my.number

class Combination(val size: Int) {
    private val fact = LongArray(size + 1){1}
    private val inv = LongArray(size + 1){1}
    init {
        for (i in 2 .. size) {
            fact[i] = i * fact[i - 1] % MOD
        }
        inv[size] = power(fact[size], MOD - 2)
        for (i in (3 .. size).reversed()) {
            inv[i - 1] = i * inv[i] % MOD
        }
    }
    fun factorial(n: Int): Long = fact[n]
    fun combination(n: Int, r: Int): Long = fact[n] * inv[r] % MOD * inv[n - r] % MOD
    companion object {
        const val MOD = 1_000_000_007
        private fun power(base: Long, exp: Int): Long {
            var b = base % MOD
            var e = exp
            var result = 1L
            while (e > 0) {
                if (e and 1 == 1) {
                    result = result * b % MOD
                }
                b = b * b % MOD
                e = e shr 1
            }
            return result
        }
    }
}