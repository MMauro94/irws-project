package com.github.mmauro94.irws

import kotlin.math.log2
import kotlin.math.pow

/**
 * Interface that defines a class that can encode a number in binary, using different strategies
 */
interface BinaryEncoder {
    /**
     * The name of the encoder
     */
    val name : String

    /**
     * Function that, given a number [n] returns the number of bits required to save it
     */
    fun calcBits(n: Long): Long
}

/**
 * Function that returns the number of bits required to store [this] number in pure binary
 */
private fun Long.bitsCount(): Long {
    return log2(this.toDouble()).toLong() + 1
}

/**
 * [BinaryEncoder] that encodes numbers with a fixed length of [bits] bits.
 *
 * * Example: with `bits=8`, the encoding of `5` would be: `00000101`
 */
class FixedLengthBinaryEncoder(val bits: Long) : BinaryEncoder {

    override val name = "Fixed Length ($bits bits)"

    private val maxValue = 2.0.pow(bits.toDouble()).toLong() - 1

    override fun calcBits(n: Long): Long {
        require(n in 1..maxValue) { "n !in 1..2^$bits-1" }
        return bits
    }
}

/**
 * [BinaryEncoder] that encodes the numbers using variable byte encoding (VBCode).
 *
 * * Example: the encoding of `5` would be: `10000101`
 * * Example: the encoding of `777` would be: `00000110 10001001`
 */
object VariableByteBinaryEncoder : BinaryEncoder {

    override val name = "Variable byte"

    override fun calcBits(n: Long): Long {
        require(n > 0) { "n <= 0" }
        return (n.bitsCount() / 7) * 8
    }
}

/**
 * [BinaryEncoder] that encodes the numbers using unary code.
 *
 * * Example: the encoding of `5` would be: `111110`
 */
object UnaryCodeBinaryEncoder : BinaryEncoder {

    override val name = "Unary code"

    override fun calcBits(n: Long): Long {
        require(n > 0) { "n <= 0" }
        return n + 1
    }
}

/**
 * [BinaryEncoder] that encodes the numbers using Elias coding.
 * @param lengthEncoder the encoding to use for the length part
 */
open class EliasCodeBinaryEncoder(
    val lengthEncoder: BinaryEncoder
) : BinaryEncoder {

    override val name = "Elias code using ${lengthEncoder.name} as length encoder"

    override fun calcBits(n: Long): Long {
        require(n > 0) { "n <= 0" }
        val bitsCount = n.bitsCount()
        return lengthEncoder.calcBits(bitsCount) + bitsCount - 1
    }
}

/**
 * [BinaryEncoder] that encodes the numbers using elias gamma code.
 *
 * * Example: the encoding of `3` would be: `1110,1`
 * * Example: the encoding of `777` would be: `1111111110,100001001`
 */
object EliasGammaCodeBinaryEncoder : EliasCodeBinaryEncoder(UnaryCodeBinaryEncoder) {
    override val name = "Elias gamma code"
}

/**
 * [BinaryEncoder] that encodes the numbers using elias delta code.
 *
 * * Example: the encoding of `777` would be: `(1110,1),100001001`
 */
object EliasDeltaCodeBinaryEncoder : EliasCodeBinaryEncoder(EliasGammaCodeBinaryEncoder) {
    override val name = "Elias delta code"
}

