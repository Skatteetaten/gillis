package no.skatteetaten.aurora.gillis.service

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import io.fabric8.kubernetes.api.model.SecretList
import no.skatteetaten.aurora.gillis.SecretDataBuilder
import no.skatteetaten.aurora.gillis.extensions.execute
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
            assertThat(applications).hasSize(1)
            val app = applications[0]
            assertThat(app.name).isEqualTo("app-cert")
            assertThat(app.namespace).isEqualTo("namespace")
            assertThat(app.ttl.seconds).isGreaterThan(0)
            assertThat(app.renewTime).isNotNull()
            assertThat(app.payload).isNotNull()
            val payload = app.payload
            assertThat(payload.commonName).isEqualTo("no.skatteetaten.aurora.app")
            assertThat(payload.namespace).isEqualTo(app.namespace)
            assertThat("${payload.name}-cert").isEqualTo(app.name)
        }
    }
}