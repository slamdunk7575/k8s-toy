package com.yanggang.kubepodspring.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class KubePodStringController {

    @GetMapping("/")
    fun hello(): String {
        return "Hello, Kubernetes!"
    }

}
