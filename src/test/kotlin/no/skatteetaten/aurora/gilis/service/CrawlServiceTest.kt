package no.skatteetaten.aurora.gorg.service

import assertk.assert
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.gorg.DeploymentConfigDataBuilder
import no.skatteetaten.aurora.gorg.ProjectDataBuilder
import org.junit.jupiter.api.Test
import java.time.Instant

class CrawlServiceTest : AbstractOpenShiftServerTest() {

    @Test
    fun `Find temporary applications`() {
        val dc = DeploymentConfigDataBuilder().build()
        openShiftServer.openshiftClient.inNamespace("namespace").deploymentConfigs().create(dc)
        val crawlService = CrawlService(openShiftServer.openshiftClient)

        val applications = crawlService.findTemporaryApplications(Instant.now())
        assert(applications).hasSize(1)
        assert(applications[0].name).isEqualTo("name")
        assert(applications[0].namespace).isEqualTo("namespace")
        assert(applications[0].ttl.seconds).isGreaterThan(0)
        assert(applications[0].removalTime).isNotNull()
    }


    @Test
    fun `Find temporary projects`() {
        val project = ProjectDataBuilder().build()
        openShiftServer.openshiftClient.inNamespace("namespace").projects().create(project)
        val crawlService = CrawlService(openShiftServer.openshiftClient)

        val projects = crawlService.findTemporaryProjects(Instant.now())
        assert(projects).hasSize(1)
        assert(projects[0].name).isEqualTo("name")
        assert(projects[0].affiliation).isEqualTo("affiliation")
        assert(projects[0].ttl.seconds).isGreaterThan(0)
        assert(projects[0].removalTime).isNotNull()
    }
}