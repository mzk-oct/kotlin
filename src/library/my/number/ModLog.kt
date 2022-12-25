package library.my.number

import kotlin.math.sqrt

fun modLog(base: Int, target: Int, primeMod: Int): Int? {
    require(base > 0)
    fun pow(b: Long, e: Int): Long {
        var r = 1L
        var b = b % primeMod
        var e = e
        while (e > 0) {
            if ((e and 1) != 0) {
                r = r * b % primeMod
            }
            b = b * b % primeMod
            e = e shr 1
        }
        return r
    }
    fun inverse(value: Long): Long {
        return pow(value, primeMod - 2)
    }
    val sqrt = (sqrt(primeMod.toDouble()) + 1).toInt()
    val babyStep = LongArray(sqrt + 1){1L}
    for (i in 1 .. sqrt) {
        babyStep[i] = base * babyStep[i - 1] % primeMod
    }
    val map = (sqrt downTo 0).associateBy(babyStep::get)
    val m = inverse(babyStep[sqrt])
    var giant = target.toLong()
    for (a in 0 until sqrt) {
        when(val b = map[giant]) {
            null -> giant = giant * m % primeMod
            else -> return a * sqrt + b
        }
    }
    return null
}