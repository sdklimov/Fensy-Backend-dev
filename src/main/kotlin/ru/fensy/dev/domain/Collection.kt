package ru.fensy.dev.domain

data class Collection(
    val id: Long,
    val title: String,
    val description: String,
    val allowViewingFor: String,
)