package no.skatteetaten.aurora.gillis

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.reactive.config.EnableWebFlux

@EnableWebFlux
@SpringBootApplication
class Main

fun main(args: Array<String>) {

    SpringApplication.run(Main::class.java, *args)
}
