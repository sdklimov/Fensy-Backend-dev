package ru.fensy.dev.domain

import java.nio.ByteBuffer

data class Subscription(
    val id: Long,
    val fullName: String,
    val userName: String,
    val avatar: ByteBuffer?,
)
