package library.my.string

sealed class ImmutableList(val size: Int): Iterable<Int> {
    fun isEmpty(): Boolean = size == 0
    fun isNotEmpty(): Boolean = size > 0
    override fun iterator(): IntIterator = ListIterator(this)
    companion object {
        data class ListIterator(private var node: ImmutableList): IntIterator() {
            override fun hasNext(): Boolean = node.isNotEmpty()
            override fun nextInt(): Int = when(val current = node) {
                is Cons -> {
                    val head = current.head
                    node = current.tail
                    head
                }
                is Nil -> throw NoSuchElementException()
            }
        }
    }
}
data class Cons(val head: Int, val tail: ImmutableList): ImmutableList(tail.size + 1)
object Nil: ImmutableList(0)
class AhoCorasick private constructor(private val node: Node) {
    fun next(char: Char): AhoCorasick = AhoCorasick(node.next(char))
    fun matches(): ImmutableList = node.match
    companion object {
        private class Node {
            val child: Array<Node> = Array(26){this}
            var match: ImmutableList = Nil
            fun next(char: Char): Node {
                return child[char - 'a']
            }
        }
        fun make(words: Array<String>): AhoCorasick {
            val root = Node()
            for ((i, word) in words.withIndex()) {
                var current = root
                for (char in word) {
                    current = when(val child = current.child[char - 'a']) {
                        current -> Node().also{current.child[char - 'a'] = it}
                        else -> child
                    }
                }
                current.match = Cons(i, Nil)
            }
            val queue = ArrayDeque<Pair<Node, Node>>()
            for (char in 'a' .. 'z') {
                when(val child = root.child[char - 'a']) {
                    root -> Unit
                    else -> queue.addLast(child to root)
                }
            }
            while (queue.isNotEmpty()) {
                val (current, failure) = queue.removeFirst()
                when(val m = current.match) {
                    is Cons -> current.match = Cons(m.head, failure.match)
                    is Nil -> current.match = failure.match
                }
                for (char in 'a' .. 'z') {
                    when(val child = current.child[char - 'a']) {
                        current -> current.child[char - 'a'] = failure.child[char - 'a']
                        else -> queue.addLast(child to failure.child[char - 'a'])
                    }
                }
            }
            return AhoCorasick(root)
        }
    }
}