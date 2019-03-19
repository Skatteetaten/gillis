package no.skatteetaten.aurora.gillis.service

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import io.fabric8.kubernetes.api.model.SecretList
import no.skatteetaten.aurora.gillis.SecretDataBuilder
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.execute
import org.junit.jupiter.api.Test
import java.time.Instant

class CrawlServiceTest : AbstractOpenShiftServerTest() {

    @Test
    fun `Find renewable certificates`() {

        val secret = SecretDataBuilder().build()
        val list = SecretList().apply { items = listOf(secret) }

        mockServer.execute(list) {
            val crawlService = CrawlService(mockClient)

            val applications = crawlService.findRenewableCertificates(Instant.now())
            val app = applications[0]
            val payload = app.payload

            assertThat(applications).hasSize(1)
            assertThat(app.name).isEqualTo("app-cert")
            assertThat(app.namespace).isEqualTo("namespace")
            assertThat(app.ttl.seconds).isGreaterThan(0)
            assertThat(app.renewTime).isNotNull()
            assertThat(app.payload).isNotNull()
            assertThat(payload.commonName).isEqualTo("no.skatteetaten.aurora.app")
            assertThat(payload.namespace).isEqualTo(app.namespace)
            assertThat("${payload.name}-cert").isEqualTo(app.name)
        }
    }
}