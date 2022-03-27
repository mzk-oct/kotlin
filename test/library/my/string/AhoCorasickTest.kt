package library.my.string

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.streams.asStream


internal class AhoCorasickTest {
    @ParameterizedTest
    @MethodSource
    fun test(data: Pair<String, List<String>>) {
        val (sentence, dictionary) = data
        val expected = count(sentence, dictionary)
        val actual = countAho(sentence, dictionary)
        assert(actual.contentEquals(expected)){
            "expected: ${expected.joinToString(", ", "[", "]")}\nactual: ${actual.joinToString(", ", "[", "]")}"
        }
    }
    fun countAho(sentence: String, dictionary: List<String>): IntArray {
        var aho = AhoCorasick.make(dictionary.toTypedArray())
        val result = IntArray(dictionary.size)
        for (c in sentence) {
            aho = aho.next(c)
            for (i in aho.matches()) {
                ++result[i]
            }
        }
        return result
    }
    fun count(sentence: String, dictionary: List<String>): IntArray {
        val result = IntArray(dictionary.size)
        for ((i, word) in dictionary.withIndex()) {
            for (j in sentence.indices) {
                var len = 0
                while (len < word.length && j + len < sentence.length && word[len] == sentence[j + len]) ++len
                if (len == word.length) {
                    ++result[i]
                }
            }
        }
        return result
    }
    companion object {
        const val Seed = 0
        @JvmStatic
        fun test(): Stream<Arguments> {
            val random = Random(Seed)
            return sequence {
                repeat(10) {
                    val str = CharArray(100000){('a' .. 'd').random(random)}.joinToString("")
                    val words = List(1000){
                        val len = (1 .. 8).random(random) + (1 .. 8).random(random)
                        CharArray(len){('a' .. 'd').random(random)}.joinToString("")
                    }.distinct().sorted()
                    yield(Arguments.of(str to words))
                }
            }.asStream()
        }
    }
}