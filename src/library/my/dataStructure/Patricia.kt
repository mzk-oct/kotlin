package library.my.dataStructure

import java.util.Collections.emptyIterator

class Patricia private constructor(private val node: Node?, val size: Int): Iterable<Int>{
    constructor(): this(null, 0)
    operator fun contains(value: Int): Boolean = node?.let{Node.search(it, value)} ?: false
    operator fun plus(value: Int): Patricia = node?.let { root -> Node.insert(root, value).let{newRoot -> if (newRoot === root) this else  Patricia(newRoot, size + 1) }} ?: Patricia(Node(value), 1)
    operator fun minus(value: Int): Patricia = node?.let{ root -> Node.erase(root, value).let{newRoot -> if (newRoot === root) this else Patricia(newRoot, size - 1)}} ?: this
    fun greaterThanOrEqual(value: Int): Int? = node?.let{ Node.greaterThanOrEqual(it, value) }
    fun greaterThan(value: Int): Int? = node?.let { Node.greaterThan(it, value) }
    fun lessThanOrEqual(value: Int): Int? = node?.let { Node.lessThanOrEqual(it, value) }
    fun lessThan(value: Int): Int? = node?.let { Node.lessThan(it, value) }
    override fun iterator(): Iterator<Int> {
        return node?.iterator() ?: emptyIterator()
    }
    companion object {
        private class NodeIterator(node: Node): Iterator<Int> {
            private val stack = Array(32){node}
            private var index = 0
            init {
                untilLeaf()
            }
            private fun untilLeaf(): Unit {
                if (index >= 0) {
                    when(val node = stack[index]) {
                        is Branch -> {
                            stack[index] = node.right
                            stack[index + 1] = node.left
                            index += 1
                            untilLeaf()
                        }
                        else -> {}
                    }
                }
            }
            override fun hasNext(): Boolean {
                return index >= 0
            }
            override fun next(): Int {
                val current = stack[index].prefix
                index -= 1
                untilLeaf()
                return current
            }
        }
        private sealed interface Node: Iterable<Int> {
            val prefix: Int
            override fun iterator(): Iterator<Int> {
                return NodeIterator(this)
            }
            companion object {
                private fun leftMostOne(value: Int): Int {
                    var result = 0
                    for (i in 4 downTo 0) {
                        if (value ushr (result + (1 shl i)) > 0) {
                            result  += 1 shl i
                        }
                    }
                    return 1 shl result
                }
                operator fun invoke(left: Node, right: Node): Node {
                    val mask = -leftMostOne(left.prefix xor right.prefix)
                    val prefix = (left.prefix or right.prefix) and mask
                    return Branch(prefix, left, right)
                }
                operator fun invoke(value: Int): Node = Leaf(value)
                fun insert(node: Node, newValue: Int): Node {
                    return when(node) {
                        is Branch -> {
                            val (prefix, left, right) = node
                            val mask = -(prefix and -prefix) shl 1
                            if ((prefix and mask) != (newValue and mask)) {
                                if (prefix < newValue) Node(node, Leaf(newValue))
                                else Node(Leaf(newValue), node)
                            }else {
                                if ((newValue and prefix) == (left.prefix and prefix)) {
                                    val newLeft = insert(left, newValue)
                                    if (newLeft === left) node
                                    else Branch(prefix, newLeft, right)
                                }else {
                                    val newRight = insert(right, newValue)
                                    if (newRight === right) node
                                    else Branch(prefix, left, newRight)
                                }
                            }
                        }
                        is Leaf -> {
                            if (node.prefix == newValue) node
                            else if (node.prefix < newValue) Node(node, Leaf(newValue))
                            else Node(Leaf(newValue), node)
                        }
                    }
                }
                fun erase(node: Node, value: Int): Node? {
                    return when(node) {
                        is Branch -> {
                            val (prefix, left, right) = node
                            val mask = -(prefix and -prefix) shl 1
                            if ((prefix and mask) != (value and mask)) node
                            else if ((value and prefix) == (left.prefix and prefix)) {
                                val newLeft = erase(left, value) ?: return right
                                if (newLeft === left) node
                                else Node(newLeft, right)
                            }else {
                                val newRight = erase(right, value) ?: return left
                                if (newRight === right) node
                                else Node(left, newRight)
                            }
                        }
                        is Leaf -> {
                            if (node.prefix == value) null
                            else node
                        }
                    }
                }
                tailrec fun minValue(node: Node): Int {
                    return when(node) {
                        is Branch -> minValue(node.left)
                        is Leaf -> node.prefix
                    }
                }
                tailrec fun maxValue(node: Node): Int {
                    return when(node) {
                        is Branch -> maxValue(node.right)
                        is Leaf -> node.prefix
                    }
                }
                tailrec fun search(node: Node, value: Int): Boolean {
                    return when(node) {
                        is Branch -> {
                            val (prefix, left, right) = node
                            val mask = -(prefix and -prefix) shl 1
                            if ((prefix and mask) != (value and mask)) false
                            else if ((value and prefix) == (left.prefix and prefix)) search(left, value)
                            else search(right, value)
                        }
                        is Leaf -> value == node.prefix
                    }
                }
                fun greaterThanOrEqual(node: Node, value: Int): Int? {
                    return when(node) {
                        is Branch -> {
                            val (prefix, left, right) = node
                            val mask = -(prefix and -prefix) shl 1
                            if ((prefix and mask) < (value and mask)) null
                            else if ((value and mask) < (prefix and mask)) minValue(left)
                            else if ((value and prefix) == (right.prefix and prefix)) greaterThanOrEqual(right, value)
                            else greaterThanOrEqual(left, value) ?: minValue(right)
                        }
                        is Leaf -> {
                            if (value <= node.prefix) node.prefix
                            else null
                        }
                    }
                }
                fun greaterThan(node: Node, value: Int): Int? {
                    return when(node) {
                        is Branch -> {
                            val (prefix, left, right) = node
                            val mask = -(prefix and -prefix) shl 1
                            if ((prefix and mask) < (value and mask)) null
                            else if ((value and mask) < (prefix and mask)) minValue(left)
                            else if ((value and prefix) == (right.prefix and prefix)) greaterThan(right, value)
                            else greaterThan(left, value) ?: minValue(right)
                        }
                        is Leaf -> {
                            if (value < node.prefix) node.prefix
                            else null
                        }
                    }
                }
                fun lessThanOrEqual(node: Node, value: Int): Int? {
                    return when(node) {
                        is Branch -> {
                            val (prefix, left, right) = node
                            val mask = -(prefix and -prefix) shl 1
                            if ((value and mask) < (prefix and mask)) null
                            else if ((prefix and mask) < (value and mask)) maxValue(right)
                            else if ((value and prefix) == (left.prefix and prefix)) lessThanOrEqual(left, value)
                            else lessThanOrEqual(right, value) ?: maxValue(left)
                        }
                        is Leaf -> {
                            if (node.prefix <= value) node.prefix
                            else null
                        }
                    }
                }
                fun lessThan(node: Node, value: Int): Int? {
                    return when(node) {
                        is Branch -> {
                            val (prefix, left, right) = node
                            val mask = -(prefix and -prefix) shl 1
                            if ((value and mask) < (prefix and mask)) null
                            else if ((prefix and mask) < (value and mask)) maxValue(right)
                            else if ((value and prefix) == (left.prefix and prefix)) lessThan(left, value)
                            else lessThan(right, value) ?: maxValue(left)
                        }
                        is Leaf -> {
                            if (node.prefix < value) node.prefix
                            else null
                        }
                    }
                }
            }
        }
        private data class Branch(override val prefix: Int, val left: Node, val right: Node): Node
        private data class Leaf(override val prefix: Int): Node
    }
}