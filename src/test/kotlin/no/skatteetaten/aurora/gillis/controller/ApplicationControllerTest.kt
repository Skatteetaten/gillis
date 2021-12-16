package no.skatteetaten.aurora.gillis.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import no.skatteetaten.aurora.gillis.RenewableCertificateBuilder
import no.skatteetaten.aurora.gillis.controller.security.WebSecurityConfig
import no.skatteetaten.aurora.gillis.service.CrawlService
import no.skatteetaten.aurora.gillis.service.RenewService
import no.skatteetaten.aurora.gillis.service.Response
import no.skatteetaten.aurora.springboot.AuroraSecurityContextRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration

@WithMockUser
@AutoConfigureRestDocs
@WebFluxTest(value = [ApplicationController::class, WebSecurityConfig::class])
class ApplicationControllerTest {

    @MockkBean(relaxed = true)
    private lateinit var passwordEncoder: PasswordEncoder

    @MockkBean(relaxed = true)
    private lateinit var authenticationManager: ReactiveAuthenticationManager

    @MockkBean(relaxed = true)
    private lateinit var securityContextRepository: AuroraSecurityContextRepository

    @MockkBean(relaxed = true)
    private lateinit var endpoint: HttpBasicServerAuthenticationEntryPoint

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var crawlService: CrawlService

    @MockkBean
    private lateinit var renewService: RenewService

    @BeforeEach
    fun setUp() {
        val certificate = RenewableCertificateBuilder(ttl = Duration.ofSeconds(-10)).build()
        every { crawlService.findRenewableCertificates(any()) } returns listOf(certificate)
    }

    @Test
    fun `Renew expired certificates`() {
        every {
            renewService.renew(any())
        } returns Response(success = true, message = "success")

        webTestClient
            .post().uri("/api/certificate/renew")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `List certificates`() {
        webTestClient.get().uri("/api/certificate")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].name").isEqualTo("name")
            .jsonPath("$[0].namespace").isEqualTo("namespace")
    }
}
