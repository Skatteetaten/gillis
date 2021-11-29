package no.skatteetaten.aurora.gillis

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient

@TestConfiguration
class TestConfig {

    @Bean
    fun webClientBuilder() = WebClient.builder()
}
