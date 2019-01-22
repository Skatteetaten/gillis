package no.skatteetaten.aurora.gillis.service

import assertk.assert
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.gillis.SecretDataBuilder
import org.junit.jupiter.api.Test
import java.time.Instant

class CrawlServiceTest : AbstractOpenShiftServerTest() {

    @Test
    fun `Find renewable certificates`() {
        val secret = SecretDataBuilder().build()
        openShiftServer.openshiftClient.inNamespace("namespace").secrets().create(secret)
        val crawlService = CrawlService(openShiftServer.openshiftClient)

        val applications = crawlService.findRenewableCertificates(Instant.now())
        assert(applications).hasSize(1)
        val app = applications[0]
        assert(app.name).isEqualTo("app-cert")
        assert(app.namespace).isEqualTo("namespace")
        assert(app.ttl.seconds).isGreaterThan(0)
        assert(app.renewTime).isNotNull()
        assert(app.payload).isNotNull()
        val payload = app.payload
        assert(payload.commonName).isEqualTo("no.skatteetaten.aurora.app")
        assert(payload.namespace).isEqualTo(app.namespace)
        assert("${payload.name}-cert").isEqualTo(app.name)
    }
}