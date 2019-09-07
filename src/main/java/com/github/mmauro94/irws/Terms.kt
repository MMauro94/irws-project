package com.github.mmauro94.irws

/**
 * Class that holds a reference to all the encountered terms, mapping them to a unique ID
 */
class Terms {
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
    fun getTermId(term: String) = terms.getOrPut(term) { ++lastTermId }


    /**
     * Converts a list of terms to a set of IDs, using the [getTermId] function.
     */
    fun toTermIds(terms: List<String>) = terms.map { getTermId(it) }.toSet()
}
