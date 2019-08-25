package com.github.mmauro94.irws

fun main() {
    //Get the Sequence<Document>
    val documents = documents().take(1000)

    println("---- INITIAL D-GAPS ----")
    documents.printDGaps()

    println()

    println("Computing cluster...")
    val clusters = documents.sortedByDescending { it.terms.size }.streamCluster(0.5)
    println("Clustered completed. Obtained ${clusters.size} clusters")

    println()

    println("Computing TSP...")
}

val ENCODINGS = listOf(VariableByteBinaryEncoder, GammaCodeBinaryEncoder, DeltaCodeBinaryEncoder)
fun Sequence<Document>.printDGaps() {
    val dGaps = ENCODINGS.associateWith { computeDGaps(it) }
    val winner = dGaps.minBy { it.value }!!.key
    dGaps
        .forEach { (enc, bits) ->
            print("%-30s % 25d bits".format(enc::class.simpleName + ":", bits))
            if(winner == enc) {
                print(" <---")
            }
            println()
        }
}

fun Sequence<Document>.computeDGaps(binaryEncoder: BinaryEncoder): Long {
    val termToLastId = HashMap<Long, Long>()
    var totalBits = 0L
    sortedBy { it.docId }.forEach { doc ->
        doc.terms.forEach { term ->
            val lastId = termToLastId[term]
            if (lastId != null) {
                totalBits += binaryEncoder.calcBits(doc.docId - lastId)
            }
            termToLastId[term] = doc.docId
        }
    }
    return totalBits
}