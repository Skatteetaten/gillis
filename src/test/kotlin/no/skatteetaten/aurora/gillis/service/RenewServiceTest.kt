package no.skatteetaten.aurora.gillis.service

import assertk.assert
import assertk.assertThat
import assertk.assertions.isTrue
import no.skatteetaten.aurora.gillis.RenewableCertificateBuilder
import no.skatteetaten.aurora.gillis.service.openshift.token.TokenProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner
class RenewServiceTest {

    @Autowired
    private lateinit var renewService: RenewService

    @MockBean
    @Suppress("unused")
    private lateinit var tokenProvider: TokenProvider

    @Test
    fun `Renew certificate`() {
        val response = renewService.renew(RenewableCertificateBuilder().build())
        assertThat(response.success).isTrue()
        assertThat(response.message.isNotEmpty()).isTrue()
    }
}