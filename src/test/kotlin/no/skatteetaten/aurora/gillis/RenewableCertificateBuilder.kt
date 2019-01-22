package no.skatteetaten.aurora.gillis

import io.fabric8.kubernetes.api.model.OwnerReference
import no.skatteetaten.aurora.gillis.service.CrawlService
import java.time.Duration
import java.time.Instant

class RenewableCertificateBuilder {

    fun build() =
        CrawlService.RenewableCertificate(
            name = "name",
            namespace = "namespace",
            ttl = Duration.ofSeconds(30),
            renewTime = Instant.now(),
            payload = CrawlService.RenewPayload(
                name = "name",
                namespace = "namespace",
                affiliation = "affiliation",
                commonName = "commonName",
                ownerReference = OwnerReference()
            )
        )
}
