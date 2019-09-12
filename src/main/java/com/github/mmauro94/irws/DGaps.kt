package com.github.mmauro94.irws

/**
 * Type that contains some [BinaryEncoder]s mapped to the total size of the posting lists using them as D-Gap encoders
 */
typealias DGaps = Map<BinaryEncoder, Long>

/**
 * Prints the sizes of [this] D-Gaps.
 * @param previous if not null, prints the saved amount in % for each encoding present in both [DGaps]
 */
fun DGaps.print(previous: DGaps? = null) {
    val winner = minBy { it.value }!!.key
    forEach { enc, bits ->
        print("%-${ENCODINGS.map { it.name.length }.max()!! + 2}s %s".format(enc.name + ": ", bits.bitsToString()))
        print(" %4s ".format(if (winner == enc) "<---" else ""))
        val prev = previous?.get(enc)
        if (prev != null) {
            print("Saved %.2f%%".format((1 - bits / prev.toDouble()) * 100))
        }
        println()
    }
}

/**
 * Computes the D-Gaps using all encodings in [ENCODINGS]
 */
fun Iterable<Document>.computeDGaps(): DGaps = ENCODINGS.associateWith { compute(it) }

/**
 * Computes the total size of the D-Gaps of the posting lists using the provided [binaryEncoder] to encode the gaps
 */
private fun Iterable<Document>.compute(binaryEncoder: BinaryEncoder): Long {
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