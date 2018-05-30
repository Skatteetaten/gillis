package no.skatteetaten.aurora.gillis.controller

import no.skatteetaten.aurora.gillis.service.CrawlService
import no.skatteetaten.aurora.gillis.service.CrawlService.RenewableCertificate
import no.skatteetaten.aurora.gillis.service.RenewService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/apps")
class ApplicationController(val crawler: CrawlService, val renewalService: RenewService) {

    @PostMapping
    fun renewExpiredCertificates() {
        crawler.findRenewableCertificates(Instant.now())
                .filter { it.ttl.isNegative }
                .forEach { renewalService.renew(it) }

    }

    @GetMapping
    fun list(): List<RenewableCertificate> {
        return crawler.findRenewableCertificates(Instant.now())
    }

}
