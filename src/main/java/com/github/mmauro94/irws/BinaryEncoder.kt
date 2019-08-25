package com.github.mmauro94.irws

import kotlin.math.log2
import kotlin.math.pow

interface BinaryEncoder {
    fun calcBits(n: Long): Long
}

private fun Long.bitsCount(): Long {
    return log2(this.toDouble()).toLong() + 1
}

class FixedLengthBinaryEncoder(val bits: Long) : BinaryEncoder {
    private val maxValue = 2.0.pow(bits.toDouble()).toLong() - 1

    override fun calcBits(n: Long): Long {
        require(n in 1..maxValue) { "n !in 1..2^$bits-1" }
        return bits
    }
}

object VariableByteBinaryEncoder : BinaryEncoder {

    override fun calcBits(n: Long): Long {
        require(n > 0) { "n <= 0" }
        return (n.bitsCount() / 7) * 8
    }
}

object UnaryCodeBinaryEncoder : BinaryEncoder {
    override fun calcBits(n: Long): Long {
        require(n > 0) { "n <= 0" }
        return n + 1
    }
}

open class EliasCodeBinaryEncoder(
    val lengthEncoder: BinaryEncoder
) : BinaryEncoder {

    override fun calcBits(n: Long): Long {
        require(n > 0) { "n <= 0" }
        val bitsCount = n.bitsCount()
        return lengthEncoder.calcBits(bitsCount) + bitsCount - 1
    }
}

object GammaCodeBinaryEncoder : EliasCodeBinaryEncoder(UnaryCodeBinaryEncoder)

object DeltaCodeBinaryEncoder : EliasCodeBinaryEncoder(GammaCodeBinaryEncoder)

