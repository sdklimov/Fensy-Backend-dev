package ru.fensy.dev.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    @GetMapping()
    fun test() = "hello from Fansy backend"

}