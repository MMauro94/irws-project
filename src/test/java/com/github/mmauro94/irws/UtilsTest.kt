package com.github.mmauro94.irws

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class UtilsTest {

    @Test
    internal fun testJaccardIndex() {
        val set1 = setOf(1, 2, 3)
        val set2 = setOf(4, 5, 6)
        val set3 = setOf(2, 3, 4)

        assertEquals(1.0, jaccardIndex(set1, set1) { it })
        assertEquals(0.0, jaccardIndex(set1, set2) { it })
        assertEquals(0.0, jaccardIndex(set2, set1) { it })
        assertEquals(0.5, jaccardIndex(set1, set3) { it })
        assertEquals(0.5, jaccardIndex(set3, set1) { it })
        assertEquals(0.2, jaccardIndex(set2, set3) { it })
        assertEquals(0.2, jaccardIndex(set3, set2) { it })
    }

    @Test
    internal fun testJaccardDistance() {
        val set1 = setOf(1, 2, 3)
        val set2 = setOf(4, 5, 6)
        val set3 = setOf(2, 3, 4)

        assertEquals(0.0, jaccardDistance(set1, set1) { it })
        assertEquals(1.0, jaccardDistance(set1, set2) { it })
        assertEquals(1.0, jaccardDistance(set2, set1) { it })
        assertEquals(0.5, jaccardDistance(set1, set3) { it })
        assertEquals(0.5, jaccardDistance(set3, set1) { it })
        assertEquals(0.8, jaccardDistance(set2, set3) { it })
        assertEquals(0.8, jaccardDistance(set3, set2) { it })
    }

    @Test
    internal fun testMinOf() {
        val list = listOf("hello", "abc", "12345678")
        assertEquals("abc" to 3, list.minOf { it.length })
    }
}