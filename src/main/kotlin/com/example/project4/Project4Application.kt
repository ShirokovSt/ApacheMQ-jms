package com.example.project4

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jms.annotation.EnableJms

@SpringBootApplication
class Project4Application

fun main(args: Array<String>) {
    runApplication<Project4Application>(*args)
}
