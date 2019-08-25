package com.github.mmauro94.irws


/**
 * Computes the Jaccard distance between two generic elements [elem1] and [elem2].
 * @see jaccardIndex
 */
fun <T, X> jaccardDistance(elem1: T, elem2: T, setSelector: (T) -> Set<X>): Double {
    return 1 - jaccardIndex(elem1, elem2, setSelector)
}

/**
 * Computes the Jaccard index between two generic elements [elem1] and [elem2].
 * In order to compute the index, a [setSelector] must be provided, that will return, given an element, the reference set for that element.
 */
fun <T, X> jaccardIndex(elem1: T, elem2: T, setSelector: (T) -> Set<X>): Double {
    val elem1Set = setSelector(elem1)
    val elem2Set = setSelector(elem2)
    return elem1Set.intersect(elem2Set).size / elem1Set.union(elem2Set).size.toDouble()
}

/**
 * Computes the minimum value in [this] instance using the given [selector].
 * It returns a [Pair] containing both the element and the minimum value computed by the selector.
 */
fun <T, C : Comparable<C>> Iterable<T>.minOf(selector: (T) -> C): Pair<T, C>? {
    var min: Pair<T, C>? = null
    for (elem in this) {
        val selected = selector(elem)
        if (min == null || min.second < selected) {
            min = Pair(elem, selected)
        }
    }
    return min
}

/**
 * List of supported units by the [bitsToString] function.
 */
private val units = listOf("bytes", "kiB", "MiB", "GiB", "TiB")

/**
 * Converts [this] number representing a number of bits, in a human readable string, using the appropriate unit (e.g. kiB, MiB, etc.)
 */
fun Long.bitsToString(): String {
    return if (this < 8) {
        "$this bits"
    } else {
        var bytes = this / 8.0
        var limit = 1024
        val unit = units.listIterator()
        unit.next()
        while (bytes >= limit && unit.hasNext()) {
            bytes /= 1024
            limit *= 1024
            unit.next()
        }
        "% 7.2f %-5s".format(bytes, unit.previous())
    }
}