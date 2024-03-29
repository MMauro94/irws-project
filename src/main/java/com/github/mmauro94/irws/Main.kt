package com.github.mmauro94.irws

/*
 * Here you will find constants that can be changed to alter the program behavior.
 */

/**
 * Limits the number of documents to parse. For debug only. Use [Integer.MAX_VALUE] to have no limits.
 */
const val MAX_DOCUMENTS = 500

/**
 * The radius parameter to pass to [streamCluster]
 */
const val STREAM_CLUSTER_RADIUS = 0.9
/**
 * The maximum number of clusters
 */
const val MAX_CLUSTERS = 1500

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
    //Step 1: Parse documents and transform them in a set of term IDs
    print("Reading documents...")
    val documents = documents().take(MAX_DOCUMENTS).toList()
    println("OK")
    println("${documents.size} documents read")
    println()

    //Compute and print the D-Gaps before any optimizations, so we can see where we start
    println("---- INITIAL D-GAPS ----")
    val initialDGaps = documents.computeDGaps()
    initialDGaps.print()
    println()

    //Step 2: sort the collection by document length, and run the stream cluster algorithm
    println("Computing clusters...")
    val clusters = documents.sortedByDescending { it.terms.size }.streamCluster(STREAM_CLUSTER_RADIUS, MAX_CLUSTERS)
    println("Clustered completed. Obtained ${clusters.size} clusters")
    println()

    //Step 3 and 4: run TSP and remap documents IDs using the TSP-induced order
    print("Remapping document IDs using TSP...")
    val remappedDocuments = clusters.runTSP().remap()
    println("OK")
    println()

    //Step 5: compute and print D-Gaps of remapped documents, comparing with initial D-Gaps
    println("---- AFTER TSP D-GAPS ----")
    remappedDocuments.computeDGaps().print(initialDGaps)
}

