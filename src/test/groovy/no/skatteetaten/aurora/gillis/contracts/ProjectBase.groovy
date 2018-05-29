package no.skatteetaten.aurora.gillis.contracts

import java.time.Duration
import java.time.Instant

import no.skatteetaten.aurora.gillis.controller.ApplicationController
import no.skatteetaten.aurora.gillis.service.CrawlService
import no.skatteetaten.aurora.gillis.service.RenewService

class ProjectBase extends AbstractContractBase {

  void setup() {
    loadJsonResponses(this)

    def crawlService = Mock(CrawlService) {
      findTemporaryProjects(_ as Instant) >> [createTemporaryProject()]
    }
    def deleteService = Mock(RenewService)

    def controller = new ApplicationController(crawlService, deleteService)
    setupMockMvc(controller)
  }
}
