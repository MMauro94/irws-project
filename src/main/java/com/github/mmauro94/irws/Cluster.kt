package com.github.mmauro94.irws

import org.jgrapht.alg.tour.ChristofidesThreeHalvesApproxMetricTSP
import org.jgrapht.graph.DefaultUndirectedWeightedGraph
import org.jgrapht.graph.DefaultWeightedEdge
import java.time.Duration

/**
 * Class that represents a cluster of [Document]s. Each cluster has a [medoid] that is the leader of the cluster, and contains a collection of [documents]
 *
 * @param medoid the leader of the cluster
 * @param documents map of doc ID => [Document]
 */
class Cluster(
    val medoid: Document,
    val documents: HashMap<Long, Document> = HashMap()
) {
    init {
        addDoc(medoid)
    }

    /**
     * Adds a doc to this cluster
     */
    fun addDoc(document: Document) {
        documents[document.docId] = document
    }

    /**
     * Computes the jaccard distance between this cluster's [medoid] and the provided [doc]
     */
    fun jaccardDistance(doc: Document) = medoid.jaccardDistance(doc)

    /**
     * Computes the jaccard distance between this cluster's [medoid] and the [other] cluster's [medoid]
     */
    fun jaccardDistance(other: Cluster) = medoid.jaccardDistance(other.medoid)
}

/**
 * Applies the stream cluster algorithm on [this] sequence of [Document]s.
 * @param radius the radius parameter
 * @param maxClusters the maximum number of cluster to create
 *
 * Returns the clusters
 */
fun Collection<Document>.streamCluster(radius: Double, maxClusters: Int): Set<Cluster> {
    val ret = HashSet<Cluster>()
    //Iterate through documents
    forEachIndexed { i, doc ->
        if (i > 0 && i % 100 == 0) {
            //Print progress every 1000 documents
            println("Clustered $i/$size documents")
        }
        //Obtain the cluster that has the minimum distance between its medoid and the current doc
        val min = ret.minOf { c -> c.jaccardDistance(doc) }
        if (min != null && (min.second < radius || ret.size >= maxClusters)) {
            //Adds to the obtained cluster only if:
            //-it exists (ret is not empty) AND
            //-the Jaccard distance is less than the radius parameter OR we have reached the maxiumum number of clusters
            min.first.addDoc(doc)
        } else {
            //Otherwise create a new cluster that contains a single document (`doc`)
            ret.add(Cluster(doc))
        }
    }
    return ret
}

/**
 * Runs the TSP using the Jaccard distance between the clusters' [medoid][Cluster.medoid]s.
 *
 * Returns a [Sequence] of [Cluster] that will iterate clusters following the TSP tour.
 */
fun Set<Cluster>.runTSP(): Sequence<Cluster> {
    //Create an undirected weighted graph
    val graph = DefaultUndirectedWeightedGraph<Cluster, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)

    //For each cluster, add as vertex
    forEach { c -> graph.addVertex(c) }

    //Add an edge between each cluster pair, putting the jaccard distance as the weight
    forEachIndexed { i, c1 ->
        drop(i + 1).forEach { c2 ->
            graph.setEdgeWeight(graph.addEdge(c1, c2), c1.jaccardDistance(c2))
        }
    }

    //Run a a 3/2-approximation algorithm for the metric TSP problem in the constructed graph
    //Return the sequence of clusters
    return ChristofidesThreeHalvesApproxMetricTSP<Cluster, DefaultWeightedEdge>()
        .getTour(graph)
        .vertexList
        .asSequence()
}

/**
 * Remaps all the documents in all the clusters in [this] sequence,
 * See Sequence<Document>.remap()
 */
fun Sequence<Cluster>.remap(): Sequence<Document> {
    return flatMap { c ->
        c.documents.values.asSequence()
    }.remapIds()
}