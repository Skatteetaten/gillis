package no.skatteetaten.aurora.gillis.controller

import no.skatteetaten.aurora.gillis.service.CrawlService
import no.skatteetaten.aurora.gillis.service.RenewService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/apps")
class ApplicationController(val crawler: CrawlService, val renewalService: RenewService) {

    @DeleteMapping
    fun deleteProjects() {
        crawler.findTemporaryApplications(Instant.now())
                .filter { it.ttl.isNegative }
                .forEach { /* Call boober */ }

    }

    @GetMapping
    fun list(): List<CrawlService.TemporaryApplication> {
        return crawler.findTemporaryApplications(Instant.now())
    }

}
