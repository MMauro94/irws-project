package com.github.mmauro94.irws

/**
 * A document, composed by a doc ID and a set of terms, that are mapped to a unique long id
 *
 * @param docId the document id
 * @param terms the list of terms
 */
class Document(val docId: Long, val terms: Set<Long>) {

    override fun hashCode() = docId.hashCode()
    override fun equals(other: Any?) = other is Document && other.docId == docId


    /**
     * Computes the jaccard distance between this document and [other].
     * Uses the [terms] as a set.
     */
    fun jaccardDistance(other: Document) = jaccardDistance(this, other) { it.terms }
}

/**
 * Remap the document IDs following [this] list order.
 * It will return a new [List] of [Document], where each document will be a new instance containing the same terms and the a progressive doc ID, starting from `1`.
 */
fun List<Document>.remapIds(): List<Document> {
    var lastId = 0L
    return map { doc ->
        Document(lastId++, doc.terms)
    }
}
