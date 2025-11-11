package com.example.practica4_1.utils

class PaginationHelper<T>(private val itemsPerPage: Int = 5) {

    private var allItems: List<T> = emptyList()
    private var currentPage: Int = 1
    private var totalPages: Int = 0

    fun setItems(items: List<T>) {
        allItems = items
        currentPage = 1
        totalPages = if (items.isEmpty()) 1 else ((items.size + itemsPerPage - 1) / itemsPerPage)
    }

    fun getCurrentPageItems(): List<T> {
        if (allItems.isEmpty()) return emptyList()

        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, allItems.size)

        return if (startIndex < allItems.size) {
            allItems.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }

    fun nextPage(): Boolean {
        return if (currentPage < totalPages && allItems.isNotEmpty()) {
            currentPage++
            true
        } else {
            false
        }
    }

    fun previousPage(): Boolean {
        return if (currentPage > 1 && allItems.isNotEmpty()) {
            currentPage--
            true
        } else {
            false
        }
    }

    fun getCurrentPage(): Int = currentPage

    fun getTotalPages(): Int = totalPages

    fun hasPreviousPage(): Boolean = currentPage > 1 && allItems.isNotEmpty()

    fun hasNextPage(): Boolean = currentPage < totalPages && allItems.isNotEmpty()

    fun getTotalItems(): Int = allItems.size
}
