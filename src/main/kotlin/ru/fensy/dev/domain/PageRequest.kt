package ru.fensy.dev.domain

/**
 * Запрос страницы.
 */
data class PageRequest(
    val pageNumber: Int,
    val pageSize: Int,
) {

    /**
     * Смещение, которое должно быть взято в соответствии с базовой страницей и размером страницы.
     */
    val offset: Int = (pageNumber - 1) * pageSize

}
