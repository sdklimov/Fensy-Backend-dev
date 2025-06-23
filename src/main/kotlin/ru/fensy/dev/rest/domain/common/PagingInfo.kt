package ru.fensy.dev.rest.domain.common

import kotlin.math.ceil

/**
 * Модель с информацией о пагинации.
 */
data class PagingInfo(
    val pageNumber: Int,
    val pageSize: Int,
    val itemsTotal: Long,
) {
    companion object {
        private const val FIRST_PAGE = 1L
    }

    val pageTotal = when (itemsTotal < pageSize) {
        true -> FIRST_PAGE
        false -> ceil(itemsTotal.toDouble() / pageSize.toDouble()).toLong()
    }

}
