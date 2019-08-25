package com.github.mmauro94.irws

/*
 * Here you will find constants that can be changed to alter the program behavior.
 */

/**
 * Limits the number of documents to parse. For debug only. Use [Integer.MAX_VALUE] to have no limits.
 */
const val MAX_DOCUMENTS = Integer.MAX_VALUE

/**
 * The radius parameter to pass to [streamCluster]
 */
const val STREAM_CLUSTER_RADIUS = 0.99
/**
 * The maximum number of clusters
 */
const val MAX_CLUSTERS = Integer.MAX_VALUE

/**
 * All the encodings to use in the calculation of D-Gaps
 */
val ENCODINGS = listOf(
    FixedLengthBinaryEncoder(4 * 8),
    VariableByteBinaryEncoder,
    EliasGammaCodeBinaryEncoder,
    EliasDeltaCodeBinaryEncoder
)

/**
 * Entry point
 */
fun main() {
    //Get the Sequence<Document>
    val documents = documents().take(MAX_DOCUMENTS)

    //Compute and print the D-Gaps before any optimizations
    println("---- INITIAL D-GAPS ----")
    val initialDGaps = documents.computeDGaps()
    initialDGaps.print()

    println()

    //Compute the clusters
    println("Computing cluster...")
    val clusters = documents.sortedByDescending { it.terms.size }.streamCluster(STREAM_CLUSTER_RADIUS, MAX_CLUSTERS)
    println("Clustered completed. Obtained ${clusters.size} clusters")

    println()

    //Run TSP and remap documents IDs
    println("Remapping document IDs using TSP...")
    val remappedDocuments = clusters.runTSP().remap()
    println("Documents remap complete")

    println()

    //Compute and print D-Gaps of remapped documents, comparing with initial D-Gaps
    println("---- AFTER TSP D-GAPS ----")
    remappedDocuments.computeDGaps().print(initialDGaps)
}

