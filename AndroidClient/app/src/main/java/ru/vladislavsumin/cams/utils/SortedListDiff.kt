package ru.vladislavsumin.cams.utils

object SortedListDiff {
    fun <T> calculateDif(
            oldList: List<T>,
            newList: List<T>,
            comparator: Comparator<T>
    ): Difference<T> {
        val deleted = mutableListOf<T>()
        val added = mutableListOf<T>()
        val modified = mutableListOf<T>()

        var i = 0 // old list position
        var j = 0 // new list position

        while (i < oldList.size && j < newList.size) {
            val oldItem = oldList[i]
            val newItem = newList[j]

            val compareRes = comparator.compare(oldItem, newItem)
            when {
                compareRes == 0 -> {
                    if (!comparator.sameContent(oldItem, newItem)) modified += newItem
                    ++i; ++j
                }
                compareRes > 0 -> {
                    added += newItem
                    ++j
                }
                compareRes < 0 -> {
                    deleted += oldItem
                    ++i
                }
            }
        }

        for (ii in i until oldList.size) deleted += oldList[ii]
        for (jj in j until newList.size) added += newList[jj]

        return Difference(deleted, added, modified)
    }

    interface Comparator<T> {
        fun compare(o1: T, o2: T): Int
        fun sameContent(o1: T, o2: T): Boolean
    }

    data class Difference<T>(
            val deleted: List<T>,
            val added: List<T>,
            val modified: List<T>
    )
}