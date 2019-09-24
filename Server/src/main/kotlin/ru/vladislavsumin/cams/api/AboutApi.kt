package ru.vladislavsumin.cams.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/about")
class AboutApi {

    @GetMapping
    fun checkConnection(): Map<String, String> {
        return mapOf("connection_state" to "ok")
    }
}