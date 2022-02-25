package no.skatteetaten.aurora.gillis

import java.time.Duration
import java.time.Instant
import io.fabric8.kubernetes.api.model.OwnerReference
import no.skatteetaten.aurora.gillis.service.CrawlService

data class RenewableCertificateBuilder(val ttl: Duration = Duration.ofSeconds(30)) {

    fun build() =
        CrawlService.RenewableCertificate(
            name = "name",
            namespace = "namespace",
            ttl = ttl,
            renewTime = Instant.now(),
            payload = CrawlService.RenewPayload(
                name = "name",
                namespace = "namespace",
                affiliation = "affiliation",
                commonName = "commonName",
                ownerReference = OwnerReference(),
                suffix = "cert"
            )
        )
}
