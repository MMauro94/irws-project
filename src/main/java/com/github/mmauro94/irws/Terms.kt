package com.github.mmauro94.irws

object Terms {
    private var lastTermId = 0L
    private val terms = HashMap<String, Long>()

    fun termId(term: String) = terms.getOrPut(term) { ++lastTermId }
}

fun List<String>.toTermIds() = map { Terms.termId(it) }.toSet()