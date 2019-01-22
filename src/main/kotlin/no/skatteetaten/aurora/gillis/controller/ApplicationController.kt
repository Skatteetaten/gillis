package no.skatteetaten.aurora.gillis.controller

import no.skatteetaten.aurora.gillis.service.CrawlService
import no.skatteetaten.aurora.gillis.service.CrawlService.RenewableCertificate
import no.skatteetaten.aurora.gillis.service.RenewService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/apps")
class ApplicationController(val crawler: CrawlService, val renewalService: RenewService) {

    val logger: Logger = LoggerFactory.getLogger(ApplicationController::class.java)

    @PostMapping
    fun renewExpiredCertificates() {
        val certs = crawler.findRenewableCertificates(Instant.now())
        val certsToRenew = certs.filter { it.ttl.isNegative }
        certsToRenew.forEach {
            try {
                val res = renewalService.renew(it)
                logger.info(res.message)
            } catch (e: SourceSystemException) {
                logger.error("Could not renew cert message=${e.message} statusCoe=${e.code}", e)
            }
        }
    }

    @GetMapping
    fun list(): List<RenewableCertificate> {
        return crawler.findRenewableCertificates(Instant.now())
    }
}
