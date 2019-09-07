package com.github.mmauro94.irws

import java.io.File
import java.io.IOException
import java.util.zip.GZIPInputStream

/**
 * Path where the data in dat.gz format is stored
 */
val DATA_DIR = File("data")

/**
 * Parses the documents in the files contained in the [DATA_DIR] directory, returning a [Sequence] of [Document]s
 */
fun documents(): Sequence<Document> = sequence<Document> {
    //For each .dat.gz file in the DATA_DIR
    DATA_DIR.listFiles { _, name -> name.endsWith(".dat.gz") }!!.forEach { file ->
        //Open and extract on-the-fly
        GZIPInputStream(file.inputStream()).reader().useLines {
            //it is a sequence of all the lines in the file
            //convert it as iterator
            val iter = it.asIterable().iterator()
            val terms = Terms()
            while (iter.hasNext()) {
                //Parse the next document starting at the current line
                val doc = iter.nextDocument(terms)
                if (doc != null) {
                    //yield the doc to the sequence
                    yield(doc)
                }
            }
        }
    }
}

/**
 * Regex for the first line of each document
 */
private val FIRST_LINE_REGEX = "^\\.I ([0-9]+)$".toRegex()
/**
 * The regex to use to split each term
 */
private val TOKEN_SPLIT_REGEX = " +".toRegex()

/**
 * Uses [this] string iterator to parse the next [Document].
 * Returns:
 * * a [Document] instance if a document could be parsed at the current position
 * * `null` if the first line is empty or the iterator is finished
 * Throws [IOException] if an invalid line is encountered
 *
 * After calling this method, the iterator will have progressed by at least one line.
 */
private fun Iterator<String>.nextDocument(terms: Terms): Document? {
    //get first line, that should be in the format ".I <docid>"
    val firstLine = if (hasNext()) next() else null
    if (firstLine.isNullOrBlank()) return null //If line is null or empty, skip

    //Match first line to get doc id
    val match = FIRST_LINE_REGEX.matchEntire(firstLine)
    val docId = if (match != null) match.groupValues[1].toLong()
    else throw IOException("Line '$firstLine' has unexpected pattern")

    //Second line should always be ".W"
    val secondLine = nextLineOrIOException()
    if (secondLine != ".W") throw IOException("Expected line '.W', got '$secondLine'")

    //Parse tokens lines (while line is not blank)
    val tokens = ArrayList<String>()
    var line = ""
    do {
        line = nextLineOrIOException()
        tokens += line.split(TOKEN_SPLIT_REGEX)
    } while (line.isNotBlank())
    return Document(docId, terms.toTermIds(tokens))
}

/**
 * Returns the next line, it it exists, otherwise throws [IOException]
 */
private fun Iterator<String>.nextLineOrIOException(): String {
    if (!hasNext()) throw IOException("Unexpected end of document")
    else return next()
}