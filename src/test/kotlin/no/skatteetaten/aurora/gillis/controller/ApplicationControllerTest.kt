package no.skatteetaten.aurora.gillis.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import no.skatteetaten.aurora.gillis.RenewableCertificateBuilder
import no.skatteetaten.aurora.gillis.service.CrawlService
import no.skatteetaten.aurora.gillis.service.RenewService
import no.skatteetaten.aurora.gillis.service.Response
import no.skatteetaten.aurora.mockmvc.extensions.Path
import no.skatteetaten.aurora.mockmvc.extensions.get
import no.skatteetaten.aurora.mockmvc.extensions.post
import no.skatteetaten.aurora.mockmvc.extensions.responseJsonPath
import no.skatteetaten.aurora.mockmvc.extensions.statusIsOk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint
import org.springframework.test.web.servlet.MockMvc
import java.time.Duration

@WithMockUser
@AutoConfigureRestDocs
@WebMvcTest(value = [ApplicationController::class])
class ApplicationControllerTest {

    @MockkBean(relaxed = true)
    private lateinit var passwordEncoder: PasswordEncoder

    @MockkBean(relaxed = true)
    private lateinit var endpoint: BasicAuthenticationEntryPoint

    @Autowired
    lateinit var mockMvc: MockMvc

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

        mockMvc.post(Path("/api/certificate/renew")) {
            statusIsOk()
        }
    }

    @Test
    fun `List certificates`() {
        mockMvc.get(Path("/api/certificate")) {
            statusIsOk()
                .responseJsonPath("$[0].name").equalsValue("name")
                .responseJsonPath("$[0].namespace").equalsValue("namespace")
        }
    }
}
