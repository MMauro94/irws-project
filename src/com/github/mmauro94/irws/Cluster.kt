package com.github.mmauro94.irws

class Cluster(
    val medoid: Document,
    val documents: HashMap<Long, Document> = HashMap()
) {
    fun addDoc(document: Document) {
        documents[document.docId] = document
    }
}


fun Sequence<Document>.streamCluster(radius: Double): Set<Cluster> {
    val ret = HashSet<Cluster>()
    forEach { d ->
        val min = ret.minOf { c -> jaccardDistance(c.medoid, d, { it.terms }) }
        if (min != null && min.second < radius) {
            min.first.addDoc(d)
        } else {
            ret.add(Cluster(d))
        }
    }
    return ret
}