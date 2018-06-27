package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.core.env.Environment
import javax.annotation.PostConstruct

@SpringBootApplication
@EnableConfigurationProperties(Props::class)
class DemoApplication(private val props: Props) {
    @PostConstruct
    fun init() {
        println("\n\n${props.test}\n\n")
    }
}

@ConfigurationProperties
class Props {
    var test: String = "notSet"
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
