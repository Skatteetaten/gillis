package no.skatteetaten.aurora.gillis.controller

import java.time.Instant
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import no.skatteetaten.aurora.gillis.service.CrawlService
import no.skatteetaten.aurora.gillis.service.CrawlService.RenewableCertificate
import no.skatteetaten.aurora.gillis.service.RenewService
import no.skatteetaten.aurora.gillis.service.Response
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/certificate")
class ApplicationController(val crawler: CrawlService, val renewalService: RenewService) {

    @PostMapping("/renew")
    fun renewExpiredCertificates(): Flux<Response> {
        return crawler.findRenewableCertificates(Instant.now())
            .filter { it.ttl.isNegative }
            .flatMap { renewalService.renew(it) }
    }

    @GetMapping
    fun list(): Flux<RenewableCertificate> {
        return crawler.findRenewableCertificates(Instant.now())
    }
}
