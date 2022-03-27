package library.my.util

sealed class MayBe<out T>
data class Just<out T>(val value: T): MayBe<T>()
object None: MayBe<Nothing>()
