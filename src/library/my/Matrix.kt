package library.my

const val MOD = 1000000007

class Matrix(val row: Int, val column: Int, val state: Array<LongArray>) {
    constructor(row: Int, column: Int): this(row, column, Array(row){LongArray(column) })
    constructor(row: Int, column: Int, generator: (Int, Int) -> Long): this(row, column, Array(row){i -> LongArray(column){j -> generator(i, j)} })
    operator fun times(other: Matrix): Matrix {
        require(column == other.row)
        val result = Array(row){LongArray(other.column)}
        for (i in 0 until row) {
            for (j in 0 until other.column) {
                var sum = 0L
                for (k in 0 until column) {
                    sum += state[i][k] * other.state[k][j] % MOD
                }
                result[i][j] = sum % MOD
            }
        }
        return Matrix(row, other.column, result)
    }
    fun pow(exp: Int): Matrix {
        require(row == column)
        var e = exp
        var result = E(row)
        var b = this
        while (e > 0) {
            if (e and 1 == 1) {
                result *= b
            }
            e = e shr 1
            b *= b
        }
        return result
    }
    companion object {
        fun E(size: Int): Matrix {
            return Matrix(size, size){i, j -> if (i == j) 1 else 0}
        }
    }
}