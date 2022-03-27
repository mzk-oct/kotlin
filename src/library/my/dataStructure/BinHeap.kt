package library.my.dataStructure


class BinHeap<T: Comparable<T>>() {
    private data class Node<T>(val top: T, val children: MutableList<Node<T>>) {
        constructor(value: T): this(value, mutableListOf())
    }

    private val children = Array<Node<T>?>(32){null}
    private var root = null as Node<T>?
    var size: Int = 0
        private set
    val top: T?
        get() = root?.top
    val isEmpty: Boolean
        get() = size == 0
    fun add(value: T) {
        ++size
        if (root == null) {
            root = Node(value)
            return
        }
        if (root!!.top <= value) {
            addNode(Node(value))
        }else {
            addNode(root!!)
            root = Node(value)
        }
    }
    fun pop(): T? {
        val (result, others) = root ?: return null
        --size
        for (node in others) {
            addNode(node)
        }
        when(val newRoot = children.indices.filter { children[it] != null }.minByOrNull { children[it]!!.top }) {
            null -> {
                root = null
            }
            else -> {
                root = children[newRoot]
                children[newRoot] = null
            }
        }
        return result
    }
    fun clear() {
        children.fill(null)
        size = 0
        root = null
    }
    fun merge(other: BinHeap<T>) {
        if (other.isEmpty) return
        if (isEmpty) return steal(other)
        size += other.size
        for (i in children.indices) {
            val overflow = other.children[i] ?: continue
            addNode(overflow)
        }
        if (root!!.top <= other.root!!.top) {
            addNode(other.root!!)
        }else {
            addNode(root!!)
            root = other.root
        }
        other.clear()
    }
    private fun steal(other: BinHeap<T>) {
        if (this == other) return
        other.children.copyInto(children)
        size = other.size
        root = other.root
        other.clear()
    }
    private fun addNode(node: Node<T>) {
        var overflow = node
        for (i in node.children.size until children.size) {
            val child = children[i]
            if (child == null) {
                children[i] = overflow
                return
            }
            if (overflow.top <= child.top) {
                overflow.children.add(child)
            }else {
                child.children.add(overflow)
                overflow = child
            }
            children[i] = null
        }
    }
}