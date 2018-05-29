package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.OpenShiftClient
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.extensions.TERMINATING_PHASE
import no.skatteetaten.aurora.gorg.extensions.removalTime
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant


@Service
class CrawlService(val client: OpenShiftClient) {

    fun findTemporaryApplications(now: Instant): List<TemporaryApplication> {
        val dcs = client.deploymentConfigs()
                .inAnyNamespace()
                .withLabel(REMOVE_AFTER_LABEL)
                .list().items

        return dcs.map {
                    val removalTime = it.removalTime()
                    TemporaryApplication(it.metadata.name,
                            it.metadata.namespace,
                            Duration.between(now, removalTime),
                            removalTime)
                }


    }


    fun findTemporaryProjects(now: Instant): List<TemporaryProject> {
        val projects = client.projects()
                .withLabel(REMOVE_AFTER_LABEL)
                .list().items

        return projects
                .filter { it.status.phase != TERMINATING_PHASE }
                .map {
                    val removalTime = it.removalTime()
                    TemporaryProject(it.metadata.name,
                            it.metadata.labels["affiliation"] ?: "",
                            Duration.between(now, removalTime),
                            removalTime)
                }

    }

    data class TemporaryApplication(val name: String, val namespace: String, val ttl: Duration, val removalTime: Instant)
    data class TemporaryProject(val name: String, val affiliation: String, val ttl: Duration, val removalTime: Instant)


}
