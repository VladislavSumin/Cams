package ru.vladislavsumin.cams.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SortedListDiffTest {
    private val testItem1 = TestItem(1, "content")
    private val testItem2 = TestItem(2, "content")
    private val testItem3 = TestItem(3, "content")
    private val testItem4 = TestItem(4, "content4")
    private val testItem5 = TestItem(5, "content5")
    private val testItem55 = TestItem(5, "content5_mod")
    private val testItem6 = TestItem(6, "content6")
    private val testItem7 = TestItem(7, "content7")

    private val testSuite1 = listOf(
            testItem1,
            testItem2,
            testItem3,
            testItem4,
            testItem5,
            testItem6
    )

    private val testSuite2 = listOf(
            testItem2,
            testItem3,
            testItem4,
            testItem55,
            testItem7
    )

    @Test
    fun `diff two empty list`() {
        val dif = SortedListDiff.calculateDif(emptyList(), emptyList(), TestComparator)
        assertTrue(dif.added.isEmpty())
        assertTrue(dif.deleted.isEmpty())
        assertTrue(dif.modified.isEmpty())
    }

    @Test
    fun `only addition`() {
        val dif = SortedListDiff.calculateDif(emptyList(), testSuite1, TestComparator)
        assertTrue(dif.added.isNotEmpty())
        assertTrue(dif.deleted.isEmpty())
        assertTrue(dif.modified.isEmpty())
    }

    @Test
    fun onlyDeletion() {
        val dif = SortedListDiff.calculateDif(testSuite1, emptyList(), TestComparator)
        assertTrue(dif.added.isEmpty())
        assertTrue(dif.deleted.isNotEmpty())
        assertTrue(dif.modified.isEmpty())
    }

    @Test
    fun diff1() {
        val dif = SortedListDiff.calculateDif(testSuite1, testSuite2, TestComparator)
        assertEquals(listOf(testItem7), dif.added)
        assertEquals(listOf(testItem1, testItem6), dif.deleted)
        assertEquals(listOf(testItem55), dif.modified)
    }

    @Test
    fun diff2() {
        val dif = SortedListDiff.calculateDif(testSuite2, testSuite1, TestComparator)
        assertEquals(listOf(testItem1, testItem6), dif.added)
        assertEquals(listOf(testItem7), dif.deleted)
        assertEquals(listOf(testItem5), dif.modified)
    }

    private object TestComparator : SortedListDiff.Comparator<TestItem> {
        override fun compare(o1: TestItem, o2: TestItem): Int {
            return o1.id.compareTo(o2.id)
        }

        override fun sameContent(o1: TestItem, o2: TestItem): Boolean {
            return o1.content == o2.content
        }
    }

    private data class TestItem(val id: Long, val content: String)
}