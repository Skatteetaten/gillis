package no.skatteetaten.aurora.gillis.service

import assertk.assertThat
import assertk.assertions.isTrue
import kotlinx.coroutines.runBlocking
import no.skatteetaten.aurora.gillis.ApplicationConfig
import no.skatteetaten.aurora.gillis.RenewableCertificateBuilder
import no.skatteetaten.aurora.gillis.StubrunnerRepoPropertiesEnabler
import no.skatteetaten.aurora.gillis.TestConfig
import no.skatteetaten.aurora.gillis.service.openshift.token.TokenProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = [TestConfig::class, ApplicationConfig::class, RenewService::class]
)
@AutoConfigureStubRunner
class RenewServiceTest : StubrunnerRepoPropertiesEnabler() {

    @Autowired
    private lateinit var renewService: RenewService

    @MockBean
    @Suppress("unused")
    private lateinit var tokenProvider: TokenProvider

    @Test
    fun `Renew certificate`() {
        val response = runBlocking {
            renewService.renew(RenewableCertificateBuilder().build())
        }
        assertThat(response.success).isTrue()
        assertThat(response.message.isNotEmpty()).isTrue()
    }
}
