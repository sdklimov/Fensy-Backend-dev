package ru.fensy.dev.extensions

object Extensions {
    fun String.toLikeQuery(): String = "%$this%"
}
