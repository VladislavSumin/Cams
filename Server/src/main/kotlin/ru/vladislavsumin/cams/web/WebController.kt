package ru.vladislavsumin.cams.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class WebController {
    @RequestMapping("/", method = [RequestMethod.GET])
    fun getRoot(): String {
        return "index"
    }
}