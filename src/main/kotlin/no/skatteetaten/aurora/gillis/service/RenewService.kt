package no.skatteetaten.aurora.gillis.service

import no.skatteetaten.aurora.gillis.service.CrawlService.RenewableCertificate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Service
class RenewService(val client: WebClient) {

    val logger: Logger = LoggerFactory.getLogger(RenewService::class.java)

    fun renew(it: RenewableCertificate) {

        val res = client.post()
            .uri("/v1/sts")
            .body(BodyInserters.fromObject(it.payload))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
        logger.info(res)


    }
}


