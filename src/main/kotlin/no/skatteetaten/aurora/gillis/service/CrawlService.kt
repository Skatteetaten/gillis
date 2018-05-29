package no.skatteetaten.aurora.gillis.service

import io.fabric8.openshift.client.OpenShiftClient
import no.skatteetaten.aurora.gillis.extensions.RENEW_AFTER_LABEL
import no.skatteetaten.aurora.gillis.extensions.renewalTime
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant


@Service
class CrawlService(val client: OpenShiftClient) {

    fun findTemporaryApplications(now: Instant): List<TemporaryApplication> {
        val secrets = client.secrets()
                .inAnyNamespace()
                .withLabel(RENEW_AFTER_LABEL)
                .list().items

        return secrets.map {
                    val renewalTime = it.renewalTime()
                    TemporaryApplication(it.metadata.name,
                            it.metadata.namespace,
                            Duration.between(now, renewalTime),
                            renewalTime)
                }


    }


    data class TemporaryApplication(val name: String, val namespace: String, val ttl: Duration, val removalTime: Instant)


}
