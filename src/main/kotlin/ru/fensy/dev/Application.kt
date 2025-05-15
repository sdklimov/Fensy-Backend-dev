package ru.fensy.dev

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FensyBackendDevApplication

fun main(args: Array<String>) {
    runApplication<FensyBackendDevApplication>(*args)
}
