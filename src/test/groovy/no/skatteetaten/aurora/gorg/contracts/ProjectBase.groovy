package no.skatteetaten.aurora.gorg.contracts

import java.time.Duration
import java.time.Instant

import no.skatteetaten.aurora.gorg.controller.ProjectController
import no.skatteetaten.aurora.gorg.service.CrawlService
import no.skatteetaten.aurora.gorg.service.DeleteService

class ProjectBase extends AbstractContractBase {

  void setup() {
    loadJsonResponses(this)

    def crawlService = Mock(CrawlService) {
      findTemporaryProjects(_ as Instant) >> [createTemporaryProject()]
    }
    def deleteService = Mock(DeleteService)

    def controller = new ProjectController(crawlService, deleteService)
    setupMockMvc(controller)
  }

  CrawlService.TemporaryProject createTemporaryProject() {
    def application = response('$[0]', Map)
    def ttl = Duration.ofSeconds(response('$[0].ttl.seconds', Long))
    def removalTime = Instant.ofEpochSecond(response('$[0].removalTime.epochSecond', Long))
    new CrawlService.TemporaryProject(application.name, application.affiliation, ttl, removalTime)
  }
}
