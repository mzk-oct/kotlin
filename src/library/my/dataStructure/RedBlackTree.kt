package library.my.dataStructure
/*

class RedBlackTree<T>(private val comparator: Comparator<T>) {
    inner class Entry(
        var value: T,
        var parent: Entry? = null,
        var red: Boolean = true,
        var left: Entry? = null,
        var right: Entry? = null)
    inner class Iterator(var node: Entry): MutableIterator<T> {
        override fun hasNext(): Boolean {
            TODO("Not yet implemented")
        }

        override fun next(): T {
            TODO("Not yet implemented")
        }

        override fun remove() {

        }
    }
    var size: Int = 0
        private set
    private var root: Entry? = null
    fun find(value: T): Entry? {
        var current = root
        while (current != null) {
            val cmp = comparator.compare(value, current.value)
            when  {
                cmp == 0 -> break
                cmp < 0 -> current = current.left
                cmp > 0 -> current = current.right
            }
        }
        return current
    }
    fun insert(value: T): MayBe<T> {
        //insertOrReplace
        ++size
        if (root == null) {
            root = Entry(value)
            return MayBe.None
        }
        var prev = null as RedBlackTree<T>.Entry?
        var current = root
        var cmp = 0
        while (current != null) {
            prev = current
            cmp = comparator.compare(value, current.value)
            when {
                cmp == 0 -> break
                cmp < 0 -> current = current.left
                cmp > 0 -> current = current.right
            }
        }
        when {
            cmp == 0 -> {
                val old = current!!.value
                current.value = value
                --size
                return MayBe.Just(old)
            }
            cmp < 0 -> {
                val entry = Entry(value, prev)
                prev!!.left = entry
                insertFixUp(entry)
            }
            cmp > 0 -> {
                val entry = Entry(value, prev)
                prev!!.right = entry
                insertFixUp(entry)
            }
        }
        return MayBe.None
    }
    fun insertIfAbsence(value: T): Boolean {
        ++size
        if (root == null) {
            root = Entry(value)
            return true
        }
        var prev = null as RedBlackTree<T>.Entry?
        var current = root
        var cmp = 0
        while (current != null) {
            prev = current
            cmp = comparator.compare(value, current.value)
            when {
                cmp == 0 -> break
                cmp < 0 -> current = current.left
                cmp > 0 -> current = current.right
            }
        }
        when {
            cmp == 0 -> {
                --size
                return false
            }
            cmp < 0 -> {
                val entry = Entry(value, prev)
                prev!!.left = entry
                insertFixUp(entry)
            }
            cmp > 0 -> {
                val entry = Entry(value, prev)
                prev!!.right = entry
                insertFixUp(entry)
            }
        }
        return true
    }
    fun delete(value: T): Entry? {
        --size
        var current = root
        var cmp = 0
        while (current != null) {
            cmp = comparator.compare(value, current.value)
            when {
                cmp == 0 -> break
                cmp < 0 -> current = current.left
                cmp > 0 -> current = current.right
            }
        }
        if (current == null) {
            ++size
            return null
        }
        return deleteNode(current)
    }
    fun clear() {
        root = null
        size = 0
    }
    private fun deleteNode(node: Entry): Entry {
        if (node.left != null && node.right != null) {

        }
        if (node.left == null) {
            val right = node.right
            if (right == null) {
                deleteRight(node)
            }else {
                TODO()
            }
        }
        val right = rightLeaf(node.left!!)
        if (right != node) {
            val value = right.value
            right.value = node.value
            node.value = value
        }
        deleteRight(right)
        return right
    }
    private fun leftLeaf(node: Entry): Entry {
        var prev = node
        var current = node.left
        while (current != null) {
            prev = current
            current = current.left
        }
        return prev
    }
    private fun rightLeaf(node: Entry): Entry {
        var prev = node
        var current = node.right
        while (current != null) {
            prev = current
            current = current.right
        }
        return prev
    }
    private fun deleteRight(node: Entry) {
        assert(node.right == null)
        if (node == root) {
            root = node.left
            root?.red = false
            root?.parent = null
            return
        }
        if (node.red) {
            if (node.parent?.right == node) {
                node.parent?.right = null
            }else {
                node.parent?.left = null
            }
            return
        }
        var parent = node.parent
        if (parent!!.left == node) {
            parent.left = node.left
            node.left?.parent = parent
        }else {
            parent.right = node.left
            node.left?.parent = parent
        }
        if (node.left?.red == true) {
            node.left!!.red = false
            return
        }
        var current = node
        while (parent != null){
            if (current == parent.left) {
                val right = parent.right!!
                when {
                    right.red -> {
                        rotateL(right, parent)
                    }
                    right.right?.red == true -> {
                        rotateL(right, parent)
                        right.right!!.red = false
                        return
                    }
                    right.left?.red == true -> {
                        rotateRL(right.left!!, right, parent)
                        right.left!!.red = false
                        return
                    }
                    else -> {
                        val color = parent.red
                        parent.red = false
                        right.red = true
                        if (color) return
                    }
                }
            }else {
                val left = parent.left!!
                when {
                    left.red -> {
                        rotateR(left, parent)
                    }
                    left.left?.red == true -> {
                        rotateR(left, parent)
                        left.left!!.red = false
                        return
                    }
                    left.right?.red == true -> {
                        rotateLR(left.right!!, left, parent)
                        left.right!!.red = false
                        return
                    }
                    else -> {
                        val color = parent.red
                        parent.red = false
                        left.red = true
                        if (color) return
                    }
                }
            }
            current = parent
            parent = current.parent
        }
    }
    private fun insertFixUp(node: Entry) {//node != root
        assert(node.red)
        var parent = node.parent ?: throw IllegalStateException()
        if (!parent.red) return
        if (parent.parent == null) {
            parent.red = false
            return
        }
        var current = parent
        var grandParent = parent.parent
        while (grandParent != null) {
            if (parent == grandParent.left) {
                val uncle = grandParent.right
                when {
                    uncle?.red == true -> {
                        uncle.red = false
                        parent.red = false
                        grandParent.red = true
                        current = parent
                        parent = grandParent
                        grandParent = grandParent.parent
                    }
                    current == parent.left -> {
                        rotateR(parent, grandParent)
                        return
                    }
                    else -> {
                        rotateLR(current, parent, grandParent)
                        return
                    }
                }
            }else {
                val uncle = grandParent.left
                when {
                    uncle?.red == true -> {
                        uncle.red = false
                        parent.red = false
                        grandParent.red = true
                        current = parent
                        parent = grandParent
                        grandParent = grandParent.parent
                    }
                    current == parent.right -> {
                        rotateL(parent, grandParent)
                        return
                    }
                    else -> {
                        rotateRL(current, parent, grandParent)
                        return
                    }
                }
            }
        }
        parent.red = false
    }
    private fun rotateL(node: Entry, parent: Entry) {
        assert(parent.right == node)

        parent.right = node.right
        node.right?.parent = parent

        node.right = node.left

        node.left = parent.left
        node.left?.parent = node

        val value = node.value
        node.value = parent.value
        parent.value = value
    }
    private fun rotateR(node: Entry, parent: Entry) {
        assert(parent.left == node)

        parent.left = node.left
        node.left?.parent = parent

        node.left = node.right

        node.right = parent.right
        node.right?.parent = node

        val value = node.value
        node.value = parent.value
        parent.value = value
    }
    private fun rotateLR(node: Entry, parent: Entry, grandParent: Entry) {
        parent.right = node.left
        node.left?.parent = parent

        node.left = node.right

        node.right = grandParent.right
        grandParent.right?.parent = node

        grandParent.right = node
        node.parent = grandParent

        val value = node.value
        node.value = grandParent.value
        grandParent.value = value
    }
    private fun rotateRL(node: Entry, parent: Entry, grandParent: Entry) {
        parent.left = node.right
        node.right?.parent = parent

        node.right = node.left

        node.left = grandParent.left
        grandParent.left?.parent = node

        grandParent.left = node
        node.parent = grandParent

        val value = node.value
        node.value = grandParent.value
        grandParent.value = value
    }
    private fun successor(node: Entry): Entry? {
        if (node.right == null) {
            return if (node.parent?.right == node) null else node.parent
        }
        var current = node.right
        while (current?.left != null) {
            current = current.left
        }
        return current
    }
    sealed class MayBe<out T> {
        object None: MayBe<Nothing>()
        data class Just<out T>(val value: T): MayBe<T>()
    }
}

class RedBlackTreeSet<T>(comparator: Comparator<T>): MutableSet<T> {
    private val tree = RedBlackTree(comparator)
    override fun add(element: T): Boolean {
        return tree.insertIfAbsence(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        var added = false
        for (e in elements) {
            added = tree.insertIfAbsence(e) || added
        }
        return added
    }

    override fun clear() {
        tree.clear()
    }

    override fun iterator(): MutableIterator<T> {
        TODO("Not yet implemented")
    }

    override fun remove(element: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override val size: Int
        get() = tree.size

    override fun contains(element: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

}

 */