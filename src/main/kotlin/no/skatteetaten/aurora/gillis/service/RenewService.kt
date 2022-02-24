package no.skatteetaten.aurora.gillis.service

import no.skatteetaten.aurora.gillis.controller.handleError
import no.skatteetaten.aurora.gillis.service.CrawlService.RenewableCertificate
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import mu.KotlinLogging
import no.skatteetaten.aurora.gillis.controller.SourceSystemException
import no.skatteetaten.aurora.gillis.controller.logError
import reactor.core.publisher.Mono

data class Response(
    val success: Boolean,
    val message: String
)

private val logger = KotlinLogging.logger { }

@Service
class RenewService(val client: WebClient) {
    fun renew(renewableCertificate: RenewableCertificate): Mono<Response> {
        return client.post()
            .uri("/v1/sts")
            .bodyValue(renewableCertificate.payload)
            .retrieve()
            .bodyToMono(Response::class.java)
            .handleError(sourceSystem = "boober")
            .doOnSuccess {
                if (!it.success) {
                    logError(SourceSystemException(message = it.message, code = "200", sourceSystem = "boober"))
                } else {
                    logger.info(it.message)
                }
            }
    }
}
