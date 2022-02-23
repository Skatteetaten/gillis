package no.skatteetaten.aurora.gillis.service

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.skatteetaten.aurora.gillis.RenewableCertificateBuilder
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.url
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

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
        mockWebServer.enqueue(
            MockResponse()
                .setBody(jacksonObjectMapper().writeValueAsString(Response(true, "ok")))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        )

        renewService.renew(RenewableCertificateBuilder().build()).map {
            assertThat(it.success).isTrue()
            assertThat(it.message).isNotEmpty()
        }
    }

    @Test
    fun `empty body`() {
        mockWebServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        )

        renewService.renew(RenewableCertificateBuilder().build()).map {
            assertThat(it.success).isTrue()
            assertThat(it.message).isNotEmpty()
        }
    }
}
