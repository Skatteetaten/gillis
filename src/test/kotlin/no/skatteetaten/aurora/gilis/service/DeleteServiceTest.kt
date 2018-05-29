package no.skatteetaten.aurora.gorg.service

import assertk.assert
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import no.skatteetaten.aurora.gorg.ProjectDataBuilder
import no.skatteetaten.aurora.gorg.TemporaryApplicationDataBuilder
import no.skatteetaten.aurora.gorg.TemporaryProjectDataBuilder
import org.junit.jupiter.api.Test

class DeleteServiceTest : AbstractOpenShiftServerTest() {

    @Test
    fun `Delete existing project`() {
        val project = ProjectDataBuilder().build()
        openShiftServer.openshiftClient.inNamespace("namespace").projects().create(project)

        val deleteService = DeleteService(openShiftServer.openshiftClient)
        val deleted = deleteService.deleteProject(TemporaryProjectDataBuilder().build())
        assert(deleted).isTrue()
    }

    @Test
    fun `Return not deleted for non-existing project`() {
        val deleteService = DeleteService(openShiftServer.openshiftClient)
        val deleted = deleteService.deleteProject(TemporaryProjectDataBuilder(name = "non-existing-name").build())
        assert(deleted).isFalse()
    }

    @Test
    fun `Delete existing application`() {
        val deleteService = DeleteService(openShiftServer.openshiftClient)
        val deleted = deleteService.deleteApplication(TemporaryApplicationDataBuilder().build())
        assert(deleted).isTrue()
    }

}