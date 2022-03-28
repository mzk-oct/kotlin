package library.my.dataStructure

/*
typealias Oracle = MinSegTreeNative
internal class MinSegTreeTest {

    @ParameterizedTest
    @MethodSource
    fun test(seed: Int) {
        val random = Random(seed)
        val size = random.nextInt(1 .. 5)
        val oracle = Oracle(size)
        val target = MinSegTreeInterface(size)
        repeat(1000) {
            when(random.nextInt(3)) {
                0 -> {
                    val position = random.nextInt(0 until size)
                    val value = random.nextInt()
                    oracle.set(position, value)
                    target.set(position, value)
                }
                1 -> {
                    val from = random.nextInt(0 until size)
                    val until = random.nextInt(from + 1 .. size)
                    val expected = oracle.get(from, until)
                    val actual = target.get(from, until)
                    assertEquals(expected, actual, "${oracle.inspect()}, ${target} -> $actual, $from, $until")
                }
                2 -> {
                    val position = random.nextInt(0 until size)
                    val expected = oracle.get(position)
                    val actual = target.get(position)
                    assertEquals(expected, actual, "${oracle.inspect()}, ${target}, $position")
                }
            }
        }
    }
    companion object {
        const val Seed = 1
        @JvmStatic
        fun test(): Stream<Int> {
            val random = Random(Seed)
            return sequence {
                repeat(10000){yield(random.nextInt())}
            }.asStream()
        }
    }
}

 */