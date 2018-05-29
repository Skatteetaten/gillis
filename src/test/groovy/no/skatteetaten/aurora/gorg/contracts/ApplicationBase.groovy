package no.skatteetaten.aurora.gorg.contracts

import java.time.Duration
import java.time.Instant

import no.skatteetaten.aurora.gorg.controller.ApplicationController
import no.skatteetaten.aurora.gorg.service.CrawlService
import no.skatteetaten.aurora.gorg.service.DeleteService

class ApplicationBase extends AbstractContractBase {

  void setup() {
    loadJsonResponses(this)
    def crawlService = Mock(CrawlService) {
      findTemporaryApplications(_ as Instant) >> [createTemporaryApplication()]
    }
    def deleteService = Mock(DeleteService)
    def controller = new ApplicationController(crawlService, deleteService)
    setupMockMvc(controller)
  }

  CrawlService.TemporaryApplication createTemporaryApplication() {
    def application = response('$[0]', Map)
    def ttl = Duration.ofSeconds(response('$[0].ttl.seconds', Long))
    def removalTime = Instant.ofEpochSecond(response('$[0].removalTime.epochSecond', Long))
    new CrawlService.TemporaryApplication(application.name, application.namespace, ttl, removalTime)
  }
}
