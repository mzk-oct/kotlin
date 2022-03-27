package library.my.dataStructure


object ConvexHull {
    data class Line(val a: Double, val b: Double) {
        operator fun invoke(x: Double): Double {
            return a * x + b
        }
    }
    private fun log2Ceil(value: Int): Int {
        var result = 0
        while ((1 shl result) < value) {
            result += 1
        }
        return result
    }
    class ConvexHullMin(xPosition: DoubleArray) {
        val size = xPosition.size
        private val height = log2Ceil(xPosition.size)
        private val xPosition = DoubleArray(2 shl height){xPosition.last()}.also { for (i in xPosition.indices) it[i] = xPosition[i] }
        private val aArray = DoubleArray(2 shl height)
        private val bArray = DoubleArray(2 shl height)
        private val exist = BooleanArray(2 shl height)
        private tailrec fun add(h: Int, index: Int, a: Double, b: Double) {
            if (!exist[index]) {
                aArray[index] = a
                bArray[index] = b
                exist[index] = true
                return
            }
            if (h == 0) {
                val x = xPosition[index - (1 shl height)]
                if (a * x + b <= aArray[index] * x + bArray[index]) {
                    aArray[index] = a
                    bArray[index] = b
                }
                return
            }
            val from = (index shl h) - (1 shl height)
            val until = from + (1 shl h)
            val mid = from + (1 shl (h - 1))
            val l = xPosition[from]
            val r = xPosition[until - 1]
            val m = xPosition[mid]
            val a2 = aArray[index]
            val b2 = bArray[index]
            val left1 = a * l + b
            val mid1 = a * m + b
            val right1 = a * r + b
            val left2 = a2 * l + b2
            val mid2 = a2 * m + b2
            val right2 = a2 * r + b2
            if (left2 <= left1 && right2 <= right1) return
            if (left1 <= left2 && right1 <= right2) {
                aArray[index] = a
                bArray[index] = b
                return
            }
            if (mid2 <= mid1) {
                return if (left1 < left2) add(h - 1, index shl 1, a, b)
                else add(h - 1, (index shl 1) + 1, a, b)
            }else {
                aArray[index] = a
                bArray[index] = b
                return if (left2 < left1) add(h - 1, index shl 1, a2, b2)
                else add(h - 1, (index shl 1) + 1, a2, b2)
            }
        }
        fun add(line: Line) {
            add(height, 1, line.a, line.b)
        }
        fun getMin(position: Int): Double? {
            if (!exist[1]) return null
            val x = xPosition[position]
            var result = Double.MAX_VALUE
            for (h in height downTo 0) {
                val index = (position + (1 shl height)) shr h
                if (!exist[index]) break
                result = minOf(result, aArray[index] * x + bArray[index])
            }
            return result
        }
    }
}