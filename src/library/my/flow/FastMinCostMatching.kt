package library.my.flow

import java.util.*

class FastMinCostMatching(val sourceSide: Int, val sinkSide: Int) {
    private val graph = Array(sourceSide){ mutableListOf<Pair<Int, Long>>() }
    fun addEdge(from: Int, to: Int, cost: Long): Unit {
        graph[from].add(to to cost)
    }
    fun pushFlow(): List<Triple<Int, Int, Long>> {
        val sourceMatching = IntArray(sourceSide){-1}
        val sinkMatching = IntArray(sinkSide){-1}
        val sinkMatchingCost = LongArray(sinkSide)
        val prevEdge = Array(sinkSide){-1 to -1L}
        val sourcePotential = LongArray(sourceSide)
        val sinkPotential = LongArray(sinkSide)
        for (list in graph) {
            for ((to, cost) in list) {
                sinkPotential[to] = minOf(sinkPotential[to], cost)
            }
        }
        val queue = PriorityQueue<Pair<Int, Long>>(compareBy(Pair<Int, Long>::second))
        val sourceMinDistance = LongArray(sourceSide)
        val sinkMinDistance = LongArray(sinkSide)
        while (true) {
            sourceMinDistance.fill(Long.MAX_VALUE)
            sinkMinDistance.fill(Long.MAX_VALUE)
            for (i in 0 until sourceSide) {
                if (sourceMatching[i] == -1) {
                    sourceMinDistance[i] = 0L
                    queue.add(i to 0L)
                }
            }
            while (queue.isNotEmpty()) {
                val (i, cost) = queue.poll()
                if (i < 0) {
                    val sink = -i - 1
                    if (sinkMinDistance[sink] < cost) continue
                    val source = sinkMatching[sink]
                    if (source == -1) continue
                    check(sinkPotential[sink] - sinkMatchingCost[sink] - sourcePotential[source] >= 0)
                    val c = cost + sinkPotential[sink] - sinkMatchingCost[sink] - sourcePotential[source]
                    if (sourceMinDistance[source] > c) {
                        sourceMinDistance[source] = c
                        queue.add(source to sourceMinDistance[source])
                    }
                }else {
                    val source = i
                    if (sourceMinDistance[source] < cost) continue
                    val currentMatching = sourceMatching[source]
                    for ((sink, e) in graph[source]) {
                        if (sink == currentMatching) continue
                        check(sourcePotential[source] + e - sinkPotential[sink] >= 0)
                        val c = cost + sourcePotential[source] + e - sinkPotential[sink]
                        if (sinkMinDistance[sink] > c) {
                            sinkMinDistance[sink] = c
                            queue.add(-sink - 1 to c)
                            prevEdge[sink] = source to e
                        }
                    }
                }
            }
            var sink = (0 until sinkSide).filter { sinkMinDistance[it] != Long.MAX_VALUE && sinkMatching[it] == -1 }.minByOrNull { sinkMinDistance[it] + sinkPotential[it] } ?: break
            var source = prevEdge[sink].first
            while (sourceMatching[source] != -1) {
                val nextSink = sourceMatching[source]
                sourceMatching[source] = sink
                sinkMatching[sink] = source
                sinkMatchingCost[sink] = prevEdge[sink].second
                sink = nextSink
                source = prevEdge[sink].first
            }
            sourceMatching[source] = sink
            sinkMatching[sink] = source
            sinkMatchingCost[sink] = prevEdge[sink].second
            for (i in 0 until sourceSide) {
                if (sourceMinDistance[i] == Long.MAX_VALUE) continue
                sourcePotential[i] += sourceMinDistance[i]
            }
            for (i in 0 until sinkSide) {
                if (sinkMinDistance[i] == Long.MAX_VALUE) continue
                sinkPotential[i] += sinkMinDistance[i]
            }
        }
        return (0 until sinkSide).filter { sinkMatching[it] != -1 }.map { Triple(sinkMatching[it], it, sinkMatchingCost[it]) }
    }
}