package com.github.mmauro94.irws

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.floor
import kotlin.math.log2

internal class BinaryEncoderTest {
    @Test
    internal fun testBitsCount() {
        for (i in 1..1000L) {
            assertEquals(i.toString(2).length.toLong(), i.bitsCount())
        }
    }


    @Test
    internal fun testVariableByte() {
        assertEquals(8, VariableByteBinaryEncoder.calcBits(1))
        assertEquals(8, VariableByteBinaryEncoder.calcBits(10))
        assertEquals(16, VariableByteBinaryEncoder.calcBits(200))
        assertEquals(8, VariableByteBinaryEncoder.calcBits(127))
        assertEquals(16, VariableByteBinaryEncoder.calcBits(128))
        assertEquals(16, VariableByteBinaryEncoder.calcBits(16383))
        assertEquals(24, VariableByteBinaryEncoder.calcBits(16384))
    }

    @Test
    internal fun testUnaryCode() {
        assertEquals(2, UnaryCodeBinaryEncoder.calcBits(1))
        assertEquals(6, UnaryCodeBinaryEncoder.calcBits(5))
        assertEquals(101, UnaryCodeBinaryEncoder.calcBits(100))
    }

    @Test
    internal fun testEliasCode() {
        assertEquals(1, EliasGammaCodeBinaryEncoder.calcBits(1))

        assertEquals(3, EliasGammaCodeBinaryEncoder.calcBits(2))
        assertEquals(3, EliasGammaCodeBinaryEncoder.calcBits(3))

        assertEquals(5, EliasGammaCodeBinaryEncoder.calcBits(4))

        assertEquals(7, EliasGammaCodeBinaryEncoder.calcBits(9))
        assertEquals(7, EliasGammaCodeBinaryEncoder.calcBits(13))

        assertEquals(9, EliasGammaCodeBinaryEncoder.calcBits(24))

        assertEquals(17, EliasGammaCodeBinaryEncoder.calcBits(511))

        assertEquals(21, EliasGammaCodeBinaryEncoder.calcBits(1025))

        for (i in 1..100L) {
            assertEquals((2 * floor(log2(i.toDouble())) + 1).toLong(), EliasGammaCodeBinaryEncoder.calcBits(i))
        }
    }

    @Test
    internal fun testEliasGamma() {
        assertEquals(5, EliasDeltaCodeBinaryEncoder.calcBits(7))
    }
}