package no.skatteetaten.aurora.gorg.service

import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.openshift.client.OpenShiftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class DeleteService(val client: OpenShiftClient) {

    val logger: Logger = LoggerFactory.getLogger(DeleteService::class.java)

    fun deleteProject(project: CrawlService.TemporaryProject): Boolean {
        logger.info("Found project to devour: ${project.name}. time-to-live expired ${project.removalTime}")
        return client.projects().withName(project.name).delete().also {
            if (it) {
                logger.info("Project ${project.name} gobbled, tastes like chicken!")
            } else {
                logger.error("Unable to delete project=${project.name}")
            }
        }
    }

    fun deleteApplication(dc: CrawlService.TemporaryApplication): Boolean {
        logger.info("Found app to devour: ${dc.name}. time-to-live expired ${dc.removalTime}")

        val lst = listOf(client.deploymentConfigs(),
                client.services(),
                client.buildConfigs(),
                client.configMaps(),
                client.secrets(),
                client.imageStreams(),
                client.routes())

        val deleted = lst.map {
            it.inNamespace(dc.namespace).withLabel("app", dc.name).delete()
        }

        return deleted.all { it }.also {
            if (!it) {
                logger.error("Unable to delete application=${dc.name}")
            }
        }
    }
}
