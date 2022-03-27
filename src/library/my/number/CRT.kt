package library.my.number

class CRT(val modA: Long, val modB: Long) {
    constructor(modA: Int, modB: Int): this(modA.toLong(), modB.toLong())
    private val x: Long
    private val y: Long
    private val mod: Long = modA * modB
    init {
        val (a, b) = extendedGcd(modA, modB)
        x = (modB + a) % modB
        y = (modA + b) % modA
    }
    fun solve(ra: Int, rb: Int): Long {
        return (rb * x % modB * modA + ra * y % modA * modB) % mod
    }
    companion object {
        tailrec fun gcd(a: Long, b: Long): Long {
            return when(b) {
                0L -> a
                else -> gcd(b, a % b)
            }
        }
        fun extendedGcd(a: Long, b: Long): Pair<Long, Long> {
            var x = 1L
            var y = 0L
            var z = 0L
            var w = 1L
            val g = gcd(a, b)
            while (a * x + b * y > g) {
                val p = (a * x + b * y) / (a * z + b * w)
                val nz = x - z * p
                val nw = y - w * p
                x = z
                y = w
                z = nz
                w = nw
            }
            return x to y
        }
    }
}