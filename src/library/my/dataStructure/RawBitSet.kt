package library.my.dataStructure

class RawBitSet private constructor(val size: Int, var bytes: LongArray) {
    constructor(size: Int):this(size, LongArray((size + WORD_LENGTH - 1) / WORD_LENGTH))
    val indices = 0 until size
    infix fun shl(offset: Int): RawBitSet {
        val newBytes = LongArray((size + offset + WORD_LENGTH - 1) shr DIV_W_TO_SHR)
        val wordOffset = offset shr DIV_W_TO_SHR
        val inWordLeft = offset and REM_W_TO_AND
        val inWordRight = WORD_LENGTH - inWordLeft
        if (inWordLeft == 0) {
            bytes.copyInto(newBytes, wordOffset)
        }else if ((size - (bytes.lastIndex shl DIV_W_TO_SHR)) + inWordLeft > WORD_LENGTH){
            for (i in bytes.indices) {
                newBytes[i + wordOffset] += bytes[i] shl inWordLeft
                newBytes[i + wordOffset + 1] = bytes[i] ushr inWordRight
            }
        }else {
            for (i in 0 until bytes.lastIndex) {
                newBytes[i + wordOffset] += bytes[i] shl inWordLeft
                newBytes[i + wordOffset + 1] = bytes[i] ushr inWordRight
            }
            newBytes[bytes.lastIndex + wordOffset] += bytes.last() shl inWordLeft
        }
        return RawBitSet(size + offset, newBytes)
    }
    infix fun shr(offset: Int): RawBitSet {
        if (size <= offset) return RawBitSet(0)
        val newBytes = LongArray((size - offset + WORD_LENGTH - 1) shr DIV_W_TO_SHR)
        val wordOffset = offset shr DIV_W_TO_SHR
        val inWordRight = offset and REM_W_TO_AND
        val inWordLeft = WORD_LENGTH - inWordRight
        if (inWordRight == 0) {
            bytes.copyInto(newBytes, 0, wordOffset)
        }else if (size - (bytes.lastIndex shl DIV_W_TO_SHR) > offset and REM_W_TO_AND) {
            newBytes[0] = bytes[wordOffset] ushr inWordRight
            for (i in wordOffset + 1 until bytes.size) {
                newBytes[i - wordOffset - 1] += bytes[i] shl inWordLeft
                newBytes[i - wordOffset] = bytes[i] ushr inWordRight
            }
        }else {
            newBytes[0] = bytes[wordOffset] ushr inWordRight
            for (i in wordOffset + 1 until bytes.lastIndex) {
                newBytes[i - wordOffset - 1] += bytes[i] shl inWordLeft
                newBytes[i - wordOffset] = bytes[i] ushr inWordRight
            }
            if (bytes.lastIndex - wordOffset - 1 >= 0)
                newBytes[bytes.lastIndex - wordOffset - 1] += bytes.last() shl inWordLeft
        }
        return RawBitSet(size - offset, newBytes)
    }
    infix fun and(other: RawBitSet): RawBitSet {
        if (size < other.size) return other and this
        val newBytes = LongArray(bytes.size)
        val from = other.bytes
        for (i in from.indices) {
            newBytes[i] = bytes[i] and from[i]
        }
        return RawBitSet(size, newBytes)
    }
    infix fun or(other: RawBitSet): RawBitSet {
        if (size < other.size) return other or this
        val newBytes = bytes.copyOf()
        val from = other.bytes
        for (i in from.indices) {
            newBytes[i] = newBytes[i] or from[i]
        }
        return RawBitSet(size, newBytes)
    }
    infix fun xor(other: RawBitSet): RawBitSet {
        if (size < other.size) return other xor this
        val newBytes = bytes.copyOf()
        val from = other.bytes
        for (i in from.indices) {
            newBytes[i] = newBytes[i] xor from[i]
        }
        return RawBitSet(size, newBytes)
    }
    fun set(position: Int, value: Boolean = true) {
        check(position in indices)
        val wordOffset = position shr DIV_W_TO_SHR
        val inWordOffset = position and REM_W_TO_AND
        if (value) {
            bytes[wordOffset] = bytes[wordOffset] or (1L shl inWordOffset)
        }else {
            bytes[wordOffset] = bytes[wordOffset] and (1L shl inWordOffset).inv()
        }
    }
    fun set(range: IntRange, value: Boolean = true) {
        check(range.first in indices && range.last in indices)
        val from = range.first
        val until = range.last + 1
        val fromWordOffset = from shr DIV_W_TO_SHR
        val toWordOffset = until shr DIV_W_TO_SHR
        if (value) {
            for (i in fromWordOffset + 1 until toWordOffset) {
                bytes[i] = ALL_SET
            }
            if (fromWordOffset == toWordOffset) {
                bytes[fromWordOffset] = bytes[fromWordOffset] or ((1L shl (until and REM_W_TO_AND)) - (1L shl (from and REM_W_TO_AND)))
            }else {
                bytes[fromWordOffset] = bytes[fromWordOffset] or (-(1L shl (from and REM_W_TO_AND)))
                if (until and REM_W_TO_AND != 0) {
                    bytes[toWordOffset] = bytes[toWordOffset] or (1L shl (until and REM_W_TO_AND)) - 1
                }
            }
        }else {
            for (i in fromWordOffset + 1 until toWordOffset) {
                bytes[i] = 0
            }
            if (fromWordOffset == toWordOffset) {
                bytes[fromWordOffset] = bytes[fromWordOffset] and ((-(1L shl (until and REM_W_TO_AND))) or ((1L shl (from and REM_W_TO_AND)) - 1))
            }else {
                bytes[fromWordOffset] = bytes[fromWordOffset] and (1L shl (from and REM_W_TO_AND)) - 1
                if (until and REM_W_TO_AND != 0) {
                    bytes[toWordOffset] = bytes[toWordOffset] and (-(1L shl (until and REM_W_TO_AND)))
                }
            }
        }
    }
    fun nextBit(firstIndex: Int = 0): Int {
        val wordOffset = firstIndex shr DIV_W_TO_SHR
        if (bytes[wordOffset] ushr (firstIndex and REM_W_TO_AND) != 0L) {
            return firstIndex + firstBit(bytes[wordOffset] ushr (firstIndex and REM_W_TO_AND))
        }else {
            for (i in wordOffset + 1 until bytes.size) {
                if (bytes[i] != 0L) return i * WORD_LENGTH + firstBit(bytes[i])
            }
            return -1
        }
    }
    fun previousBit(lastIndex: Int = size - 1): Int {
        val wordOffset = lastIndex shr DIV_W_TO_SHR
        if (bytes[wordOffset] and ((2L shl (lastIndex and REM_W_TO_AND)) - 1) != 0L) {
            return (wordOffset shl DIV_W_TO_SHR) + lastBit(bytes[wordOffset] and ((2L shl (lastIndex and REM_W_TO_AND)) - 1))
        }else {
            for (i in wordOffset - 1 downTo 0) {
                if (bytes[i] != 0L)
                    return (i shl DIV_W_TO_SHR) + lastBit(bytes[i])
            }
            return -1
        }
    }
    operator fun get(index: Int): Boolean {
        check(index in indices)
        return ((bytes[index shr DIV_W_TO_SHR] ushr (index and REM_W_TO_AND)) and 1) != 0L
    }
    companion object {
        const val WORD_LENGTH = 64
        const val REM_W_TO_AND = 63
        const val DIV_W_TO_SHR = 6
        const val ALL_SET = -1L
        private fun firstBit(value: Long): Int {
            return lastBit(value and -value)
        }
        private fun lastBit(value: Long): Int {
            var result = 0
            for (d in 5 downTo 0) {
                if (value ushr (result + (1 shl d)) != 0L) {
                    result += 1 shl d
                }
            }
            return result
        }
    }
}