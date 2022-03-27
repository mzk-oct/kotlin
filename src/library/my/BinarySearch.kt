package library.my


inline fun <T> List<T>.partitionPoint(predicate: (T) -> Boolean): Int {
    var min = 0
    var max = size
    while (min < max) {
        val mid = (min + max) shr 1
        if (predicate(this[mid])) {
            min = mid + 1
        }else {
            max = mid
        }
    }
    return max
}
fun <T: Comparable<T>> List<T>.lowerBound(target: T): Int {
    return partitionPoint { it < target }
}
fun <T: Comparable<T>> List<T>.upperBound(target: T): Int {
    return partitionPoint { it <= target }
}