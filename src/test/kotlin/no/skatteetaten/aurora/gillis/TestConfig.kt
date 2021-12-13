package no.skatteetaten.aurora.gillis

import no.skatteetaten.aurora.gillis.service.openshift.token.TokenProvider
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient

@TestConfiguration
class TestConfig {

    @Bean
    fun webClientBuilder() = WebClient.builder()

    @Bean
    fun mockTokenProvider() = MockTokenProvider()
}

class MockTokenProvider : TokenProvider {
    override fun getToken(): String = "test"
}
