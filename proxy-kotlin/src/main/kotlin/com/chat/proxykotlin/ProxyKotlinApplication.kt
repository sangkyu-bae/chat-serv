package com.chat.proxykotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProxyKotlinApplication

fun main(args: Array<String>) {
	runApplication<ProxyKotlinApplication>(*args)
}
