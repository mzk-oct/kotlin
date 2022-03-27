package library.my



import library.my.util.*

class RBTree2<T>(private val comparator: Comparator<T>): MutableSet<T>, Iterable<T> {
    var root: Node<T>? = null
    fun verify() {
        Node.verify(root)
    }
    fun insert(key: T): T? {
        return when (val result = Node.insert(key, root, comparator)) {
            is Right -> {
                root = result.value
                ++size
                null
            }
            is Left -> result.value
        }
    }
    fun delete(key: T): Boolean {
        return when(val result = Node.delete(key, root, comparator)) {
            is Just -> {
                root = result.value
                --size
                true
            }
            else -> false
        }
    }
    private fun delete(node: Node<T>) {
        root = Node.delete(node, root!!)
        --size
    }
    override operator fun contains(element: T): Boolean {
        return Node.find(element, root, comparator) != null
    }
    override var size: Int = 0
        private set
    override fun iterator(): MutableIterator<T> = NodeIterator(this, root)

    override fun add(element: T): Boolean {
        return insert(element) == null
    }

    override fun addAll(elements: Collection<T>): Boolean {
        var added = false
        for (element in elements) {
            added = added || add(element)
        }
        return added
    }

    override fun clear() {
        root = null
    }

    override fun remove(element: T): Boolean {
        return delete(element)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var removed = false
        for (element in elements) {
            removed = removed || delete(element)
        }
        return removed
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val result = RBTree2(comparator)
        if (elements is Set<T>) {
            if (elements.size > size) {
                for (element in this) {
                    if (element in elements) {
                        result.add(element)
                    }
                }
            }else {
                for (element in elements) {
                    if (element in this) {
                        result.add(element)
                    }
                }
            }
        }else {
            for (element in elements) {
                if (element in elements) {
                    result.add(element)
                }
            }
        }
        val changed = size != result.size
        root = result.root
        return changed
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all { it in this }
    }

    override fun isEmpty(): Boolean {
        return root == null
    }
    companion object {
        operator fun <T: Comparable<T>> invoke(): RBTree2<T> {
            return RBTree2(naturalOrder())
        }
        class NodeIterator<T>(val host: RBTree2<T>, var node: Node<T>?): MutableIterator<T> {
            private var lastElement:T? = null
            override fun remove() {
                host.delete(node ?: throw NoSuchElementException())
                node = Node.nextNode(node!!.key, host.root, host.comparator)
            }
            override fun hasNext(): Boolean = node != null
            override fun next(): T {
                val current = node ?: throw NoSuchElementException()
                node = Node.findNext(current)
                return current.key
            }
        }

        class Node<T>(var key: T, var red: Boolean, var parent: Node<T>?, var left: Node<T>?, var right: Node<T>?){
            val black: Boolean
                get() = !red
            companion object {
                private fun blackCount(node: Node<*>?): Int {
                    if (node == null) return 0
                    return if (node.red) blackCount(node.left) else blackCount(node.left) + 1
                }
                fun <T> verify(node: Node<T>?) {
                    if (node == null) return
                    if (node.red && (node.left?.red == true || node.right?.red == true)) {
                        throw IllegalStateException("Double Red")
                    }
                    val diff = if (node.red) 0 else 1
                    if (blackCount(node) != blackCount(node.right) + diff || blackCount(node) != blackCount(node.left) + diff) {
                        throw IllegalStateException("Wrong Black Count")
                    }
                    verify(node.left)
                    verify(node.right)
                }
                tailrec fun <T> nextNode(key: T, root: Node<T>?, comparator: Comparator<T>): Node<T>? {
                    val node = root ?: return null
                    val comp = comparator.compare(key, node.key)
                    if (comp >= 0) {
                        return nextNode(key, node.right, comparator)
                    }else {
                        return nextNode(key, node.left, comparator) ?: node
                    }
                }
                fun <T> insert(key: T, root: Node<T>?, comparator: Comparator<T>): Either<T, Node<T>> {
                    var current = root ?: return Right(Node(key, false, null, null, null))
                    val newNode = Node(key, true, null, null, null)
                    while (current != newNode) {
                        val comp = comparator.compare(current.key, key)
                        when {
                            comp < 0 -> current = current.right ?: newNode.apply {
                                current.right = this
                                parent = current
                            }
                            0 < comp -> current = current.left ?: newNode.apply {
                                current.left = this
                                parent = current
                            }
                            else -> {
                                val old = current.key
                                current.key = key
                                return Left(old)
                            }
                        }
                    }
                    insertFixRec(current)
                    return Right(findRoot(root))
                }
                fun <T> add(key: T, root: Node<T>?, comparator: Comparator<T>): MayBe<Node<T>> {
                    var current = root ?: return Just(Node(key, false, null, null, null))
                    val newNode = Node(key, true, null, null, null)
                    while (current != newNode) {
                        val comp = comparator.compare(current.key, key)
                        when {
                            comp < 0 -> current = current.right ?: newNode.apply {
                                current.right = this
                                parent = current
                            }
                            0 < comp -> current = current.left ?: newNode.apply {
                                current.left = this
                                parent = current
                            }
                            else -> return None
                        }
                    }
                    insertFixRec(current)
                    return Just(findRoot(root))
                }
                fun <T> delete(key: T, root: Node<T>?, comparator: Comparator<T>): MayBe<Node<T>?> {
                    val node = find(key, root, comparator) ?: return None
                    if (node == root && root.left == null && root.right == null) {
                        return Just(null)
                    }
                    deleteNode(node)
                    return Just(findRoot(root!!))
                }
                fun <T> delete(node: Node<T>, root: Node<T>): Node<T>? {
                    if (node == root && root.left == null && root.right == null) {
                        return null
                    }
                    deleteNode(node)
                    return findRoot(root)
                }
                tailrec fun <T> find(key: T, node: Node<T>?, comparator: Comparator<T>): Node<T>? {
                    val comp = comparator.compare(key, (node ?: return null).key)
                    return when {
                        comp < 0 -> find(key, node.left, comparator)
                        0 < comp -> find(key, node.right, comparator)
                        else -> node
                    }
                }
                private tailrec fun <T> findRoot(node: Node<T>): Node<T> {
                    return findRoot(node.parent ?: return node)
                }
                fun <T> deleteNode(node: Node<T>) {
                    if (node.right != null && node.left != null) {
                        val next = findMin(node.right!!)
                        node.key = next.key
                        deleteNode(next)
                    }else if (node.right != null) {
                        node.right!!.parent = node.parent
                        if (node == node.parent?.left) {
                            node.parent!!.left = node.right
                        }else if (node == node.parent?.right){
                            node.parent!!.right = node.right
                        }
                        node.right!!.red = false
                        node.parent = node.right
                    }else if (node.left != null) {
                        node.left!!.parent = node.parent
                        if (node == node.parent?.left) {
                            node.parent!!.left = node.left
                        }else if (node == node.parent?.right){
                            node.parent!!.right = node.left
                        }
                        node.left!!.red = false
                        node.parent = node.left
                    }else {
                        when (node) {
                            node.parent?.left -> {
                                node.parent!!.left = null
                                if (!node.red) {
                                    deleteLeftFix(node.parent!!)
                                }
                            }
                            node.parent?.right -> {
                                node.parent!!.right = null
                                if (!node.red) {
                                    deleteRightFix(node.parent!!)
                                }
                            }
                            else -> Unit
                        }
                    }
                }
                private tailrec fun <T> findMax(node: Node<T>): Node<T> {
                    return when(val right = node.right) {
                        null -> node
                        else -> findMax(right)
                    }
                }
                private tailrec fun <T> findMin(node: Node<T>): Node<T> {
                    return if (node.left == null) node else findMin(node.left!!)
                }
                fun <T> findNext(node: Node<T>): Node<T>? {
                    if (node.right != null) {
                        return findMin(node.right!!)
                    }
                    var current = node
                    while (current.parent?.right == current) {
                        current = current.parent!!
                    }
                    return current.parent
                }
                private fun <T> rotateR(left: Node<T>, parent: Node<T>): Unit {
                    assert(left == parent.left)
                    val root = parent.parent
                    left.right?.parent = parent
                    parent.left = left.right
                    parent.parent = left
                    left.right = parent
                    left.parent = root
                    if (root != null) {
                        if (parent == root.left) {
                            root.left = left
                        } else {
                            root.right = left
                        }
                    }
                }
                private fun <T> rotateL(parent: Node<T>, right: Node<T>): Unit {
                    assert(parent.right == right)
                    val root = parent.parent
                    right.left?.parent = parent
                    parent.right = right.left
                    parent.parent = right
                    right.left = parent
                    right.parent = root
                    if (root != null) {
                        if (parent == root.left) {
                            root.left = right
                        } else {
                            root.right = right
                        }
                    }
                }
                private fun <T> rotateLR(left: Node<T>, mid: Node<T>, right: Node<T>): Unit {
                    assert(left.right == mid)
                    assert(left == right.left)
                    val root = right.parent

                    mid.left?.parent = left
                    mid.right?.parent = right
                    left.right = mid.left
                    right.left = mid.right

                    left.parent = mid
                    right.parent = mid
                    mid.left = left
                    mid.right = right

                    mid.parent = root
                    if (root != null) {
                        if (right == root.left) {
                            root.left = mid
                        } else {
                            root.right = mid
                        }
                    }
                }
                private fun <T> rotateRL(left: Node<T>, mid: Node<T>, right: Node<T>): Unit {
                    assert(right == left.right)
                    assert(right.left == mid)

                    val root = left.parent

                    mid.left?.parent = left
                    mid.right?.parent = right
                    left.right = mid.left
                    right.left = mid.right

                    left.parent = mid
                    right.parent = mid
                    mid.left = left
                    mid.right = right

                    mid.parent = root
                    if (root != null) {
                        if (left == root.left) {
                            root.left = mid
                        } else {
                            root.right = mid
                        }
                    }
                }
                private tailrec fun <T> insertFixRec(node: Node<T>) {
                    val parent = node.parent ?: return
                    if (parent.parent == null) {
                        parent.red = false
                    }
                    if (!parent.red) return
                    assert(parent.parent != null)
                    val grandparent = parent.parent!!
                    assert(grandparent.black)
                    if (parent == grandparent.left) {
                        val uncle = grandparent.right
                        when {
                            uncle?.red == true -> {
                                uncle.red = false
                                parent.red = false
                                grandparent.red = true
                                insertFixRec(grandparent)
                            }
                            node == parent.left -> {
                                parent.red = false
                                grandparent.red = true
                                rotateR(parent, grandparent)
                            }
                            else -> {
                                node.red = false
                                grandparent.red = true
                                rotateLR(parent, node, grandparent)
                            }
                        }
                    }else {
                        val uncle = grandparent.left
                        when {
                            uncle?.red == true -> {
                                uncle.red = false
                                parent.red = false
                                grandparent.red = true
                                insertFixRec(grandparent)
                            }
                            node == parent.right -> {
                                parent.red = false
                                grandparent.red = true
                                rotateL(grandparent, parent)
                            }
                            else -> {
                                node.red = false
                                grandparent.red = true
                                rotateRL(grandparent, node, parent)
                            }
                        }
                    }
                }
                private tailrec fun <T> deleteFixUpRec(node: Node<T>) {
                    assert(node.black)
                    val parent = node.parent ?: return
                    if (node == parent.left) {
                        val right = parent.right!!
                        when {
                            right.red -> {
                                right.red = false
                                parent.red = true
                                rotateL(parent, right)
                                deleteLeftFix(parent)
                            }
                            right.right?.red == true -> {
                                right.red = parent.red
                                parent.red = false
                                right.right!!.red = false
                                rotateL(parent, right)
                            }
                            right.left?.red == true -> {
                                val mid = right.left!!
                                mid.red = parent.red
                                parent.red = false
                                rotateRL(parent, mid, right)
                            }
                            else -> {
                                val red = parent.red
                                parent.red = false
                                right.red = true
                                if (!red) {
                                    deleteFixUpRec(parent)
                                }
                            }
                        }
                    }else {
                        val left = parent.left!!
                        when {
                            left.red -> {
                                left.red = false
                                parent.red = true
                                rotateR(left, parent)
                                deleteRightFix(parent)
                            }
                            left.left?.red == true -> {
                                left.red = parent.red
                                parent.red = false
                                left.left!!.red = false
                                rotateR(left, parent)
                            }
                            left.right?.red == true -> {
                                val mid = left.right!!
                                mid.red = parent.red
                                parent.red = false
                                rotateLR(left, mid, parent)
                            }
                            else -> {
                                val red = parent.red
                                parent.red = false
                                left.red = true
                                if (!red) {
                                    deleteFixUpRec(parent)
                                }
                            }
                        }
                    }
                }
                private tailrec fun <T> deleteLeftFix(parent: Node<T>) {
                    assert(parent.right != null)
                    val right = parent.right!!
                    when {
                        right.red -> {
                            right.red = false
                            parent.red = true
                            rotateL(parent, right)
                            deleteLeftFix(parent)
                        }
                        right.right?.red == true -> {
                            right.red = parent.red
                            parent.red = false
                            right.right!!.red = false
                            rotateL(parent, right)
                        }
                        right.left?.red == true -> {
                            val mid = right.left!!
                            mid.red = parent.red
                            parent.red = false
                            rotateRL(parent, mid, right)
                        }
                        else -> {
                            val red = parent.red
                            parent.red = false
                            right.red = true
                            if (!red) {
                                deleteFixUpRec(parent)
                            }
                        }
                    }
                }
                private tailrec fun <T> deleteRightFix(parent: Node<T>) {
                    assert(parent.left != null)
                    val left = parent.left!!
                    when {
                        left.red -> {
                            left.red = false
                            parent.red = true
                            rotateR(left, parent)
                            deleteRightFix(parent)
                        }
                        left.left?.red == true -> {
                            left.red = parent.red
                            parent.red = false
                            left.left!!.red = false
                            rotateR(left, parent)
                        }
                        left.right?.red == true -> {
                            val mid = left.right!!
                            mid.red = parent.red
                            parent.red = false
                            rotateLR(left, mid, parent)
                        }
                        else -> {
                            val red = parent.red
                            parent.red = false
                            left.red = true
                            if (!red) {
                                deleteFixUpRec(parent)
                            }
                        }
                    }
                }
            }
        }
    }
}

