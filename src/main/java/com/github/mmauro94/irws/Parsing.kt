package com.github.mmauro94.irws

import java.io.File
import java.util.zip.GZIPInputStream

/**
 * Path where the data in dat.gz format is stored
 */
val DATA_DIR = File("data")

/**
 * Parses the documents in the files contained in the [DATA_DIR] directory, returning a [Sequence] of [Document]s
 */
fun documents(): Sequence<Document> = sequence<Document> {
    DATA_DIR.listFiles { _, name -> name.endsWith(".dat.gz") }!!.forEach { file ->
        GZIPInputStream(file.inputStream()).reader().useLines {
            val iter = it.asIterable().iterator()
            while (iter.hasNext()) {
                val doc = iter.nextDocument()
                if (doc != null) {
                    yield(doc)
                }
            }
        }
    }
}