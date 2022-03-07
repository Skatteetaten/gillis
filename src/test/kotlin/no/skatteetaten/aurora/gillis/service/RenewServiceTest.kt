package no.skatteetaten.aurora.gillis.service

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.skatteetaten.aurora.gillis.RenewableCertificateBuilder
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.execute
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.url
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import reactor.test.StepVerifier

class RenewServiceTest {

    private lateinit var mockWebServer: MockWebServer

    private lateinit var renewService: RenewService

    @BeforeAll
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val webClient = WebClient.builder()
            .baseUrl(mockWebServer.url)
            .build()

        renewService = RenewService(webClient)
    }

    @AfterAll
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `Renew certificate`() {
        mockWebServer.execute(
            MockResponse()
                .setBody(jacksonObjectMapper().writeValueAsString(Response(true, "ok")))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        ) {
            val response = renewService.renew(RenewableCertificateBuilder().build())
            StepVerifier
                .create(response)
                .expectNext(Response(success = true, message = "ok"))
                .expectComplete()
                .verify()
        }
    }

    @Test
    fun `Expect error when response has empty body`() {
        mockWebServer.execute(
            MockResponse()
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        ) {
            val response = renewService.renew(RenewableCertificateBuilder().build())
            StepVerifier
                .create(response)
                .expectErrorMessage("Empty response")
                .verify()
        }
    }

    @Test
    fun `Expect error when response has success false`() {
        mockWebServer.execute(
            MockResponse()
                .setBody(jacksonObjectMapper().writeValueAsString(Response(false, "Failed renewing certificate")))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        ) {
            val response = renewService.renew(RenewableCertificateBuilder().build())
            StepVerifier
                .create(response)
                .expectErrorMessage("Failed renewing certificate")
                .verify()
        }
    }
}
