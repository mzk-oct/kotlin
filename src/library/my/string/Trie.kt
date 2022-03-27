package library.my.string

class Trie {
    private val children = Array(26){null as Trie?}
    var match: ImmutableList = Nil
        private set
    private fun add(word: String, index: Int, id: Int, last: ImmutableList) {
        if (index == word.length) {
            match = Cons(id, last)
        }else if (match.isNotEmpty()) {
            when(val child = children[word[index] - 'a']) {
                null -> children[word[index] - 'a'] = Trie().also{it.add(word, index + 1, id, match)}
                else -> child.add(word, index + 1, id, match)
            }
        }else {
            match = last
            when(val child = children[word[index] - 'a']) {
                null -> children[word[index] - 'a'] = Trie().also{it.add(word, index + 1, id, last)}
                else -> child.add(word, index + 1, id, last)
            }
        }
    }
    fun add(word: String, id: Int) {
        add(word, 0, id, match)
    }
    fun matchAll(sentence: String, index: Int): Iterable<Int> {
        if (sentence.length <= index) return match
        return children[sentence[index] - 'a']?.matchAll(sentence, index + 1) ?: match
    }
    companion object {
        fun make(words: Array<String>): Trie {
            assert((1 until words.size).all { words[it - 1] <= words[it] })
            val root = Trie()
            for ((i, word) in words.withIndex()) {
                root.add(word, i)
            }
            return root
        }
    }
}