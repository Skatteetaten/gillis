package no.skatteetaten.aurora.gillis.service

import io.fabric8.kubernetes.api.model.OwnerReference
import io.fabric8.openshift.client.OpenShiftClient
import no.skatteetaten.aurora.gillis.extensions.APP_ANNOTATION
import no.skatteetaten.aurora.gillis.extensions.COMMON_NAME_ANNOTATION
import no.skatteetaten.aurora.gillis.extensions.RENEW_AFTER_LABEL
import no.skatteetaten.aurora.gillis.extensions.annotation
import no.skatteetaten.aurora.gillis.extensions.label
import no.skatteetaten.aurora.gillis.extensions.renewalTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class CrawlService(val client: OpenShiftClient) {

    val logger: Logger = LoggerFactory.getLogger(RenewService::class.java)

    fun findRenewableCertificates(now: Instant): List<RenewableCertificate> {

        val secrets = client.secrets()
            .inAnyNamespace()
            .withLabel(RENEW_AFTER_LABEL)
            .list().items

        return secrets.mapNotNull {

            try {
                val renewalTime = it.renewalTime()

                val ownerRerefence = it.metadata.ownerReferences[0]

                RenewableCertificate(
                    name = it.metadata.name,
                    namespace = it.metadata.namespace,
                    ttl = Duration.between(now, renewalTime),
                    renewTime = renewalTime,
                    payload = RenewPayload(
                        name = it.annotation(APP_ANNOTATION),
                        namespace = it.metadata.namespace,
                        affiliation = it.label("affiliation"),
                        commonName = it.annotation(COMMON_NAME_ANNOTATION),
                        ownerReference = ownerRerefence
                    )
                )
            } catch (e: Exception) {
                logger.warn("Secret with name=${it.metadata.name} is not valid message=${e.message}")
                null
            }
        }
    }

    data class RenewableCertificate(
        val name: String,
        val namespace: String,
        val ttl: Duration,
        val renewTime: Instant,
        val payload: RenewPayload
    )

    data class RenewPayload(
        val name: String,
        val namespace: String,
        val affiliation: String,
        val commonName: String,
        val ownerReference: OwnerReference
    )
}
