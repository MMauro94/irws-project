package com.github.mmauro94.irws


fun <T, X> jaccardDistance(elem1: T, elem2: T, setSelector: (T) -> Set<X>): Double {
    return 1 - jaccardIndex(elem1, elem2, setSelector)
}

fun <T, X> jaccardIndex(elem1: T, elem2: T, setSelector: (T) -> Set<X>): Double {
    val elem1Set = setSelector(elem1)
    val elem2Set = setSelector(elem2)
    return elem1Set.intersect(elem2Set).size / elem1Set.union(elem2Set).size.toDouble()
}

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