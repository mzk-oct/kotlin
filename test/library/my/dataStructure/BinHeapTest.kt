package library.my.dataStructure

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.streams.asStream
import kotlin.test.assertEquals


internal class BinHeapTest {

    @ParameterizedTest
    @MethodSource
    fun test(seed: Int) {
        val random = Random(seed)
        val candidate = Candidate()
        val target = Target()
        repeat(REPUTATION) {
            val op = random.nextInt(102)
            if (op == 101) {
                //println("merge left")
                candidate.mergeToLeft()
                target.mergeToLeft()
            }else if (op == 100) {
                //println("merge right")
                candidate.mergeToRight()
                target.mergeToRight()
            }else if (90 <= op) {
                //println("pop left")
                assertEquals(candidate.popLeft(), target.popLeft())
            }else if (80 <= op) {
                //println("pop right")
                assertEquals(candidate.popRight(), target.popRight())
            }else if (40 <= op) {
                val value = random.nextInt()
                //println("add left: $value")
                candidate.addLeft(value)
                target.addLeft(value)
            }else {
                val value = random.nextInt()
                //println("add right: $value")
                candidate.addRight(value)
                target.addRight(value)
            }
            checkState(candidate, target)
        }
        checkBreak(candidate, target)
    }
    interface Tester {
        fun addLeft(value: Int)
        fun addRight(value: Int)
        fun popLeft(): Int?
        fun popRight(): Int?
        fun mergeToLeft()
        fun mergeToRight()
        fun pops(): Pair<Int?, Int?> = popLeft() to popRight()
        fun tops(): Pair<Int?, Int?>
        fun sizes(): Pair<Int, Int>
        fun isEmpties(): Pair<Boolean, Boolean>
        fun isEmpty(): Boolean = isEmpties().first && isEmpties().second
    }
    fun checkState(a: Tester, b: Tester) {
        assert(a.sizes() == b.sizes() && a.tops() == b.tops() && a.isEmpties() == b.isEmpties())
    }
    fun checkBreak(a: Tester, b: Tester) {
        while (!a.isEmpty() || !b.isEmpty()) {
            checkState(a, b)
            assert(a.pops() == b.pops())
        }
    }
    class Target(val a: PriorityQueue<Int>, val b: PriorityQueue<Int>): Tester {
        constructor():this(PriorityQueue(), PriorityQueue())
        override fun addLeft(value: Int) {
            a.add(value)
        }
        override fun addRight(value: Int) {
            b.add(value)
        }
        override fun popLeft(): Int? {
            return a.poll()
        }
        override fun popRight(): Int? {
            return b.poll()
        }
        override fun mergeToLeft() {
            a.addAll(b)
            b.clear()
        }
        override fun mergeToRight() {
            b.addAll(a)
            a.clear()
        }
        override fun tops(): Pair<Int?, Int?> {
            return a.firstOrNull() to b.firstOrNull()
        }
        override fun sizes(): Pair<Int, Int> {
            return a.size to b.size
        }
        override fun isEmpties(): Pair<Boolean, Boolean> {
            return a.isEmpty() to b.isEmpty()
        }
    }
    class Candidate(val a: BinHeap<Int>, val b: BinHeap<Int>): Tester {
        constructor():this(BinHeap(), BinHeap())
        override fun addLeft(value: Int) {
            a.add(value)
        }
        override fun addRight(value: Int) {
            b.add(value)
        }
        override fun popLeft(): Int? {
            return a.pop()
        }
        override fun popRight(): Int? {
            return b.pop()
        }
        override fun mergeToLeft() {
            a.merge(b)
        }
        override fun mergeToRight() {
            b.merge(a)
        }
        override fun tops(): Pair<Int?, Int?> {
            return a.top to b.top
        }
        override fun sizes(): Pair<Int, Int> {
            return a.size to b.size
        }
        override fun isEmpties(): Pair<Boolean, Boolean> {
            return a.isEmpty to b.isEmpty
        }
    }
    companion object {
        const val SEED = 1
        const val TEST_COUNT = 5000
        const val REPUTATION = 5000
        @JvmStatic
        fun test(): Stream<Int> {
            val random = Random(SEED)
            return sequence {
                repeat(TEST_COUNT) {
                    yield(random.nextInt())
                }
            }.asStream()
        }
        fun <T> PriorityQueue<T>.merge(other: PriorityQueue<T>) {
            for (a in other) {
                add(a)
            }
        }
    }
}