package library.my


import library.my.util.*

class RBTree<T>(private val comparator: Comparator<T>): MutableSet<T>, Iterable<T> {
    var root: Node<T>? = null
    fun insert(key: T): T? {
        return when (val result = Node.insert(key, root, comparator)) {
            is Right -> {
                root = result.value
                null
            }
            is Left -> result.value
        }
    }
    fun delete(key: T): Boolean {
        return when(val result = Node.delete(key, root, comparator)) {
            is Just -> {
                root = result.value
                true
            }
            else -> false
        }
    }
    override operator fun contains(element: T): Boolean {
        return Node.find(element, root, comparator) != null
    }
    fun countLowerBound(key: T): Int {
        var result = 0
        var current = root
        while (current != null) {
            val comp = comparator.compare(key, current.key)
            if (comp <= 0) {
                current = current.left
            }else {
                result += (current.left?.size ?: 0) + 1
                current = current.right
            }
        }
        return result
    }
    fun countUpperBound(key: T): Int {
        var result = 0
        var current = root
        while (current != null) {
            val comp = comparator.compare(key, current.key)
            if (comp < 0) {
                current = current.left
            }else {
                result += (current.left?.size ?: 0) + 1
                current = current.right
            }
        }
        return result
    }
    override val size: Int
        get() = root?.size ?: 0
    override fun iterator(): MutableIterator<T> = NodeIterator(this, root)
    fun verify() {
        when (val result = root?.verify(comparator)) {
            is Just ->
                throw IllegalStateException(result.value.toString())
            is None -> Unit
        }
    }
    override fun add(element: T): Boolean {
        return when(val added = Node.add(element, root, comparator)) {
            is Just -> {
                root = added.value
                true
            }
            is None -> false
        }
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
        val result = RBTree(comparator)
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
        operator fun <T: Comparable<T>> invoke(): RBTree<T> {
            return RBTree(naturalOrder())
        }
        class NodeIterator<T>(val host: RBTree<T>, var node: Node<T>?): MutableIterator<T> {
            private var lastElement:T? = null
            override fun remove() {
                host.root = Node.deleteNode(node ?: throw NoSuchElementException())
                node = Node.nextNode(node!!.key, host.root, host.comparator)
            }
            override fun hasNext(): Boolean = node != null
            override fun next(): T {
                val current = node ?: throw NoSuchElementException()
                node = Node.findNext(current)
                return current.key
            }
        }
    }
}


sealed class Failure<T>(val node: Node<T>)
class DoubleRed<T> (node: Node<T>): Failure<T>(node)
class BlackCountNotSame<T>(node: Node<T>): Failure<T>(node)
class WrongOrder<T>(node: Node<T>): Failure<T>(node)
class Other<T>(node: Node<T>, val reason: String): Failure<T>(node)
class Node<T>(var key: T, var red: Boolean, var parent: Node<T>?, var left: Node<T>?, var right: Node<T>?){
    var size: Int = (left?.size ?: 0) + (right?.size ?: 0) + 1
        private set
    val black: Boolean
        get() = !red
    private fun update(): Unit {
        size = (left?.size ?: 0) + (right?.size ?: 0) + 1
    }
    fun verify(comparator: Comparator<T>): MayBe<Failure<T>> {
        return when (val result = verifyInner(comparator)) {
            is Left -> Just(result.value)
            is Right -> None
        }
    }
    private fun verifyInner(comparator: Comparator<T>): Either<Failure<T>, Int> {
        if (size != (left?.size ?: 0) + (right?.size ?: 0) + 1) {
            return Left(Other(this, "size not match"))
        }
        if (red) {
            if (left?.red == true) {
                return Left(DoubleRed(this))
            }
            if (right?.red == true) {
                return Left(DoubleRed(this))
            }
        }
        if (left != null && comparator.compare(left!!.key, key) > 0) {
            return Left(WrongOrder(this))
        }
        if (right != null && comparator.compare(key, right!!.key) > 0) {
            return Left(WrongOrder(this))
        }
        val leftCount = left?.verifyInner(comparator).let {
            when(it) {
                null -> 0
                is Right -> it.value
                is Left -> return it
            }
        }
        val rightCount = right?.verifyInner(comparator).let {
            when(it) {
                null -> 0
                is Right -> it.value
                is Left -> return it
            }
        }
        if (leftCount != rightCount) {
            return Left(BlackCountNotSame(this))
        }
        return if (red) Right(leftCount) else Right(leftCount + 1)
    }
    companion object {
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
            return Right(insertFixRec(current))
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
            return Just(insertFixRec(current))
        }
        fun <T> delete(key: T, root: Node<T>?, comparator: Comparator<T>): MayBe<Node<T>?> {
            val node = find(key, root, comparator) ?: return None
            return Just(deleteNode(node))
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
        fun <T> deleteNode(node: Node<T>): Node<T>? {
            return if (node.right != null && node.left != null) {
                val next = findMin(node.right!!)
                node.key = next.key
                return deleteNode(next)
            }else if (node.right != null) {
                node.right!!.parent = node.parent
                if (node == node.parent?.left) {
                    node.parent!!.left = node.right
                }else if (node == node.parent?.right){
                    node.parent!!.right = node.right
                }
                node.right!!.red = false
                updateUp(node.parent ?: return node.right)
            }else if (node.left != null) {
                node.left!!.parent = node.parent
                if (node == node.parent?.left) {
                    node.parent!!.left = node.left
                }else if (node == node.parent?.right){
                    node.parent!!.right = node.left
                }
                node.left!!.red = false
                updateUp(node.parent ?: return node.left)
            }else {
                when (node) {
                    node.parent?.left -> {
                        node.parent!!.left = null
                        if (node.red) {
                            updateUp(node.parent!!)
                        }else {
                            deleteLeftFix(node.parent!!)
                        }
                    }
                    node.parent?.right -> {
                        node.parent!!.right = null
                        if (node.red) {
                            updateUp(node.parent!!)
                        }else {
                            deleteRightFix(node.parent!!)
                        }
                    }
                    else -> null
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
            parent.update()
            left.update()
            //root.update()
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
            parent.update()
            right.update()
            //root.update()
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
            left.update()
            right.update()
            mid.update()
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
            left.update()
            right.update()
            mid.update()
        }
        private tailrec fun <T> insertFixRec(node: Node<T>): Node<T> {
            val parent = node.parent ?: return node
            parent.update()
            if (parent.parent == null) {
                parent.red = false
            }
            if (!parent.red) return updateUp(parent.parent ?: return parent)
            assert(parent.parent != null)
            val grandparent = parent.parent!!
            assert(grandparent.black)
            if (parent == grandparent.left) {
                val uncle = grandparent.right
                return when {
                    uncle?.red == true -> {
                        uncle.red = false
                        parent.red = false
                        grandparent.red = true
                        grandparent.update()
                        insertFixRec(grandparent)
                    }
                    node == parent.left -> {
                        parent.red = false
                        grandparent.red = true
                        rotateR(parent, grandparent)
                        updateUp(parent.parent ?: return parent)
                    }
                    else -> {
                        node.red = false
                        grandparent.red = true
                        rotateLR(parent, node, grandparent)
                        updateUp(node.parent ?: return node)
                    }
                }
            }else {
                val uncle = grandparent.left
                return when {
                   uncle?.red == true -> {
                       uncle.red = false
                       parent.red = false
                       grandparent.red = true
                       grandparent.update()
                       insertFixRec(grandparent)
                   }
                    node == parent.right -> {
                        parent.red = false
                        grandparent.red = true
                        rotateL(grandparent, parent)
                        updateUp(parent.parent ?: return parent)
                    }
                    else -> {
                        node.red = false
                        grandparent.red = true
                        rotateRL(grandparent, node, parent)
                        updateUp(node.parent ?: return node)
                    }
                }
            }
        }
        private tailrec fun <T> updateUp(node: Node<T>): Node<T> {
            node.update()
            return updateUp(node.parent ?: return node)
        }
        private tailrec fun <T> deleteFixUpRec(node: Node<T>): Node<T> {
            assert(node.black)
            val parent = node.parent ?: return node
            parent.update()
            if (node == parent.left) {
                val right = parent.right!!
                return when {
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
                        updateUp(right.parent ?: return right)
                    }
                    right.left?.red == true -> {
                        val mid = right.left!!
                        mid.red = parent.red
                        parent.red = false
                        rotateRL(parent, mid, right)
                        updateUp(mid.parent ?: return mid)
                    }
                    else -> {
                        val red = parent.red
                        parent.red = false
                        right.red = true
                        if (red) {
                            updateUp(parent.parent ?: return parent)
                        }else {
                            deleteFixUpRec(parent)
                        }
                    }
                }
            }else {
                val left = parent.left!!
                return when {
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
                        updateUp(left)
                    }
                    left.right?.red == true -> {
                        val mid = left.right!!
                        mid.red = parent.red
                        parent.red = false
                        rotateLR(left, mid, parent)
                        updateUp(mid.parent ?: return mid)
                    }
                    else -> {
                        val red = parent.red
                        parent.red = false
                        left.red = true
                        if (red) {
                            updateUp(parent.parent ?: return parent)
                        }else {
                            deleteFixUpRec(parent)
                        }
                    }
                }
            }
        }
        private tailrec fun <T> deleteLeftFix(parent: Node<T>): Node<T> {
            assert(parent.right != null)
            parent.update()
            val right = parent.right!!
            return when {
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
                    updateUp(right.parent ?: return right)
                }
                right.left?.red == true -> {
                    val mid = right.left!!
                    mid.red = parent.red
                    parent.red = false
                    rotateRL(parent, mid, right)
                    updateUp(mid.parent ?: return mid)
                }
                else -> {
                    val red = parent.red
                    parent.red = false
                    right.red = true
                    if (red) {
                        updateUp(parent.parent ?: return parent)
                    }else {
                        deleteFixUpRec(parent)
                    }
                }
            }
        }
        private tailrec fun <T> deleteRightFix(parent: Node<T>): Node<T> {
            assert(parent.left != null)
            parent.update()
            val left = parent.left!!
            return when {
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
                    updateUp(left)
                }
                left.right?.red == true -> {
                    val mid = left.right!!
                    mid.red = parent.red
                    parent.red = false
                    rotateLR(left, mid, parent)
                    updateUp(mid.parent ?: return mid)
                }
                else -> {
                    val red = parent.red
                    parent.red = false
                    left.red = true
                    if (red) {
                        updateUp(parent.parent ?: return parent)
                    }else {
                        deleteFixUpRec(parent)
                    }
                }
            }
        }
    }
}