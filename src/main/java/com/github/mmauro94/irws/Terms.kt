package com.github.mmauro94.irws

/**
 * Object that holds a reference to all the terms encountered terms, mapping them to a unique ID
 */
object Terms {
    /**
     * The last term ID, used to provide the next one
     */
    private var lastTermId = 0L
    /**
     * Map containing all terms mapped to their ID
     */
    private val terms = HashMap<String, Long>()

    /**
     * Function that returns the term ID, given the [term]
     */
    fun termId(term: String) = terms.getOrPut(term) { ++lastTermId }
}

/**
 * Converts a list of terms to a set of IDs, using the [Terms.termId] function.
 */
fun List<String>.toTermIds() = map { Terms.termId(it) }.toSet()