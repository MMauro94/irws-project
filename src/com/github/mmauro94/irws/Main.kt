package com.github.mmauro94.irws

fun main() {
    println(documents()
        .sortedByDescending { it.terms.size }
        .streamCluster(0.5)
        .size
    )
}
    