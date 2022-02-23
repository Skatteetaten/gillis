package no.skatteetaten.aurora.gillis.service

import no.skatteetaten.aurora.gillis.controller.handleError
import no.skatteetaten.aurora.gillis.service.CrawlService.RenewableCertificate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

data class Response(
    val success: Boolean,
    val message: String
)

@Service
class RenewService(val client: WebClient) {
    val logger: Logger = LoggerFactory.getLogger(RenewService::class.java)

    fun renew(it: RenewableCertificate): Mono<Response> {

        return client.post()
            .uri("/v1/sts")
            .bodyValue(it.payload)
            .retrieve()
            .bodyToMono(Response::class.java)
            .doOnSuccess { logger.info(it.message) }
            .handleError(sourceSystem = "boober")
    }
}
