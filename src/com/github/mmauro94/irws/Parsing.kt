package com.github.mmauro94.irws

import java.io.File
import java.util.zip.GZIPInputStream

val DATA_DIR = File("data")

fun documents() = sequence<Document> {
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

