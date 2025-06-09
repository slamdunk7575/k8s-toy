package com.yanggang.kubepodspring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KubePodSpringApplication

fun main(args: Array<String>) {
	runApplication<KubePodSpringApplication>(*args)
}
