package com.github.mmauro94.irws

import java.io.IOException

class Document(val docId: Long, val terms: Set<Long>) {

    override fun hashCode() = docId.hashCode()
    override fun equals(other: Any?) = other is Document && other.docId == docId
}

private val FIRST_LINE_REGEX = "^\\.I ([0-9]+)$".toRegex()
private val TOKEN_SPLIT_REGEX = " +".toRegex()

fun Iterator<String>.nextDocument(): Document? {
    //get first line, that should be in the format ".I <docid>"
    val firstLine = if(hasNext()) next() else null
    if(firstLine.isNullOrBlank()) return null //If line is null or empty, skip

    //Match first line to get doc id
    val match = FIRST_LINE_REGEX.matchEntire(firstLine)
    val docId = if (match != null) match.groupValues[1].toLong()
    else throw IOException("Line '$firstLine' has unexpected pattern")

    val secondLine = nextLineOrIOException()
    if(secondLine != ".W") throw IOException("Expected line '.W', got '$secondLine'")

    val tokens = ArrayList<String>()
    var line = ""
    do {
        line = nextLineOrIOException()
        tokens += line.split(TOKEN_SPLIT_REGEX)
    }while(line.isNotBlank())
    return Document(docId, tokens.toTermIds())
}

private fun Iterator<String>.nextLineOrIOException(): String {
    if (!hasNext()) throw IOException("Unexpected end of document")
    else return next()
}