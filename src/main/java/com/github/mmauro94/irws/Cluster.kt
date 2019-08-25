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
    forEachIndexed { i, doc ->
        if (i % 1000 == 0) {
            println("Clustered $i documents")
        }
        val min = ret.minOf { c -> jaccardDistance(c.medoid, doc, { it.terms }) }
        if (min != null && min.second < radius) {
            min.first.addDoc(doc)
        } else {
            ret.add(Cluster(doc))
        }
    }
    return ret
}