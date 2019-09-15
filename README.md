# Introduction
The goal of this project is to take a bunch of documents (list of words), and assign IDs to them in such a way that it would take the materialization of the compressed postings list the least amount of space.

The whole project can be diveded in the following steps:
1. **Reading documents**: this first step is fairly simple: the `documents()` function generates a [`Sequence`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence) of documents. While doing so, a map of `term => term id` is kept, so we can directly create the document instance with the set of term ids. The document ID is provided by the parsed file.
2. **Computing cluster**: in this step the `streamCluster()` algorithm is performed, creating a bunch of clusters of documents, each of which has a medoid.
3. **Running TSP**: after computing the clusters, the Travelling Salesman Problem is run on the clusters, using the [Jaccard distance](https://en.wikipedia.org/wiki/Jaccard_index) between the clusters.
4. **ID remapping**: each document is remapped using the order given by the TSP visit. Documents inside the same cluster are freely given consecutive IDs without a particular order.
5. **Compute D-Gaps**: lastly, the sizes of possible postings list using the new doc ID assignment is computed, using various compression techniques, confronting the results with the original IDs.

# The stream cluster algorithm
This algorithm is used to cluster the documents together. A pseudo-code algorithm is the following:
```
Stream_cluster(SortedCollection, Radius)
  C = EmptySet
  for each d in SortedCollection
    Dist_c = Min(JaccardDistance(c, d), for each medoid c in C)
    if (Dist_c < radius) then
      add d to cluster c  
    else
      make d a new medoid, and add this singleton cluster to C
  return C
```

In the actual implementation I added a parameter that limits the number of clusters that are generated, to avoid the creation of too many clusters.

# The TSP visit
Since it would take too much time to perform a TSP between all documents, this algorithm is run only between the medoids of the clusters. For this task I used the library [JGraphT](https://jgrapht.org) and used the [Christofides algorithm](https://en.wikipedia.org/wiki/Christofides_algorithm), which is a `3/2`-approximation algorithm for the metric version of the TSP. When constructing the graph I added one vertex per cluster and one edge between each pair of vertices with, as a weight, the jaccard distance between the medoids.

# The postings lists sizes
As mentioned, we just need to compute the sizes of the compressed posting lists, so we do not need to actually materialize them. I used four different techniques to compute the bit length of the gaps between doc IDs, illustrated in the following table:

| Encoding  | Description |
| ------------- | ------------- |
| Fixed length (32 bit)  | This is the easiest method: simply store each gap in 32 bits, no matter the actual value of the number. Obviously we cannot compute a gap that takes more than 32 bits to store, and we waste a lot of space.  |
| Variable byte | We take the number and keep the first 7 bits. If we need more bits, we add a `1`, otherwise a `0`. Basically we continue adding 7 bits at a time, until we finish them. This results the gap to be encoded in a multiple of 8, depending on its size. |
| Elias code (gamma) | In the elias code we first add an encoding that specifies the length of our gap, and then the gap itself (without the first 1, because it's implied). In this gamma version, the encoding of the length is made using unary code, that puts as many `1`s as the number is, followed by a zero. |
| Elias code (delta) | It's the same as the above one, but it uses a gamma delta code to encode the length. |

# Project structure
The project is strucutred in the following files:

| File  | Description |
| ------------- | ------------- |
| BinaryEncoder | Contains a `BinaryEncoder` interface that allows to calculate the number of bits that it takes for the implementing encoding to encode a number. It also contains the implementations for all the encodings mentioned above. |
| Cluster | Contains the class definition of `Cluster`, the function to run the TSP in a set of cluters and the function to remap the IDs of the documents in a list of cluster. |
| DGaps | Contians a function to compute the posting list size given a collection of documents and an encoding, and a function to print all the d-gaps |
| Document | Contains the class definition of `Document` and a function to remap IDs |
| Main | Contains the main function that glues all togehter and some modifiable constants |
| Parsing | Contains the function to parse the documents |
| Terms | Contains the class definition of `Terms`, needed to cache the term/term id map while parsing |
| Utils | Contains miscellaneous utility functions, notably a function to compute the Jaccard distance beween two sets and the function the converts a number of bits to a human readable length (kiB, MiB, etc.) |

# Running the program
It is advised to increase the maximum JVM heap size with the parameter `-Xmx`. For instance, to set the max heap size to 10 gigabytes, use the param like this: `-Xmx10g`. This is needed as the program needs to load in RAM all the documents to perfrom its computations.

# Results
I've made a few runs trying different radiuses and max clusters. Obiviously incrementing the number of clusters will yield improved results at the cost of performance.

Here is reported the program output for the best run, with radius `0.9` and `3000` max clusters:

```
Reading documents...OK
806791 documents read

---- INITIAL D-GAPS ----
Fixed Length (32 bits):   241,11 MiB        
Variable byte:             71,97 MiB        
Elias gamma code:          65,86 MiB        
Elias delta code:          56,89 MiB   <--- 

Computing clusters...
Remapping document IDs using TSP...OK

---- AFTER TSP D-GAPS ----
Fixed Length (32 bits):   241,11 MiB        Saved 0,00%
Variable byte:             69,40 MiB        Saved 3,57%
Elias gamma code:          58,26 MiB        Saved 11,54%
Elias delta code:          50,45 MiB   <--- Saved 11,32%
```

As we can see we (obiviously) cannot possibly save any space using a fixed-length binary encoding, as the number of gaps stays the same.

For the other three encodings we were able to save space, with the Elias gamma and delta code stealing the show both for absoulte size and also for percentage saved.

Other runs can be found as text files in the `/runs` folder.
