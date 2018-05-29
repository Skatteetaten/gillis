package no.skatteetaten.aurora.gillis.contracts

import java.time.Duration
import java.time.Instant

import no.skatteetaten.aurora.gillis.controller.ApplicationController
import no.skatteetaten.aurora.gillis.service.CrawlService
import no.skatteetaten.aurora.gillis.service.RenewService

class ApplicationBase extends AbstractContractBase {

  void setup() {
    loadJsonResponses(this)
    def crawlService = Mock(CrawlService) {
      findTemporaryApplications(_ as Instant) >> [createTemporaryApplication()]
    }
    def deleteService = Mock(RenewService)
    def controller = new ApplicationController(crawlService, deleteService)
    setupMockMvc(controller)
  }

  CrawlService.TemporaryApplication createTemporaryApplication() {
    def application = response('$[0]', Map)
    def ttl = Duration.ofSeconds(response('$[0].ttl.seconds', Long))
    def removalTime = Instant.ofEpochSecond(response('$[0].renewalTime.epochSecond', Long))
    new CrawlService.TemporaryApplication(application.name, application.namespace, ttl, removalTime)
  }
}
