package no.skatteetaten.aurora.gillis.controller

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.then
import com.nhaarman.mockito_kotlin.times
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
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import java.time.Duration

@AutoConfigureRestDocs
@WebMvcTest(controllers = [ApplicationController::class], secure = false)
@ExtendWith(SpringExtension::class)
class ApplicationControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockBean
    private lateinit var crawlService: CrawlService

    @MockBean
    private lateinit var renewService: RenewService

    @BeforeEach
    fun setUp() {
        val certificate = RenewableCertificateBuilder(ttl = Duration.ofSeconds(-10)).build()
        given(crawlService.findRenewableCertificates(any())).willReturn(listOf(certificate))
    }

    @Test
    fun `Renew expired certificates`() {
        given(renewService.renew(any())).willReturn(Response(success = true, message = "success"))
        mockMvc.post(Path("/api/certificate/renew")) {
            statusIsOk()
        }
        then(renewService).should(times(1)).renew(any())
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