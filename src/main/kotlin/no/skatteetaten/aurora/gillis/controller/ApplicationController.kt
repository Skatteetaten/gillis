package no.skatteetaten.aurora.gillis.controller

import no.skatteetaten.aurora.gillis.service.CrawlService
import no.skatteetaten.aurora.gillis.service.CrawlService.RenewableCertificate
import no.skatteetaten.aurora.gillis.service.RenewService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/certificate")
class ApplicationController(val crawler: CrawlService, val renewalService: RenewService) {

    @PostMapping("/renew")
    fun renewExpiredCertificates(): Mono<Unit> {
        val certs = crawler.findRenewableCertificates(Instant.now())
        val certsToRenew = certs.filter { it.ttl.isNegative }
        certsToRenew.forEach {
            renewalService.renew(it)
        }
        return Mono.empty()
    }

    @GetMapping
    fun list(): List<RenewableCertificate> {
        return crawler.findRenewableCertificates(Instant.now())
    }
}
