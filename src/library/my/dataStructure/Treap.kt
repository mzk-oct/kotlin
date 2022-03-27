package library.my.dataStructure

import library.my.dataStructure.Treap.Node.Companion.size
import kotlin.random.Random

class Treap<T: Comparable<T>> {
    class Node<T>(val value: T, val priority: Int, var size: Int, var left: Node<T>? = null, var right: Node<T>? = null) {
        constructor(value: T, priority: Int): this(value, priority, 1)
        companion object {
            fun <T> Node<T>?.size(): Int {
                return this?.size ?: 0
            }
        }
    }
    companion object {
        fun <T> merge(left: Node<T>?, right: Node<T>?): Node<T>? {
            if (left == null) return right
            if (right == null) return left
            return if (left.priority < right.priority) {
                right.left = merge(left, right.left)
                right.size += left.size
                right
            }else {
                left.right = merge(left.right, right)
                left.size += right.size
                left
            }
        }
        fun <T: Comparable<T>> split(node: Node<T>?, separator: T): Pair<Node<T>?, Node<T>?> {
            if (node == null) return null to null
            val cmp = node.value.compareTo(separator)
            return if (cmp == 0) {
                node.left to node.right
            }else if (cmp < 0) {
                val (l, r) = split(node.left, separator)
                node.left = r
                node.size = node.left.size() + node.right.size() + 1
                l to node
            }else {
                val (l, r) = split(node.right, separator)
                node.right = l
                node.size = node.left.size() + node.right.size() + 1
                node to r
            }
        }
        fun <T> nth(node: Node<T>, rank: Int): T {
            val cmp = node.left.size().compareTo(rank)
            return if (cmp == 0) {
                node.value
            }else if (cmp < 0) {
                nth(node.left!!, rank)
            }else {
                nth(node.right!!, rank - node.left.size() - 1)
            }
        }
    }
    private val random = Random(0)
    private var root = null as Node<T>?
    val size: Int
        get() = root.size()
    fun insert(value: T) {
        val (left, right) = split(root, value)
        root = merge(merge(left, Node(value, random.nextInt())), right)
    }
    fun erase(value: T) {
        val (left, right) = split(root, value)
        root = merge(left, right)
    }
    fun nth(rank: Int): T {
        require(rank in 0 until size)
        return nth(root!!, rank)
    }
}