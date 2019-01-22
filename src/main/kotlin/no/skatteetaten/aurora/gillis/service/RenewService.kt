package no.skatteetaten.aurora.gillis.service

import no.skatteetaten.aurora.gillis.controller.SourceSystemException
import no.skatteetaten.aurora.gillis.controller.blockNonNullAndHandleError
import no.skatteetaten.aurora.gillis.service.CrawlService.RenewableCertificate
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

data class Response(
    val success: Boolean,
    val message: String
)

@Service
class RenewService(val client: WebClient) {

    fun renew(it: RenewableCertificate): Response {

        val res = client.post()
            .uri("/v1/sts")
            .body(BodyInserters.fromObject(it.payload))
            .retrieve()
            .bodyToMono(Response::class.java)
            .blockNonNullAndHandleError(sourceSystem = "boober")

        if (!res.success) {
            throw SourceSystemException(
                message = res.message,
                code = "200",
                sourceSystem = "boober"
            )
        }
        return res
    }
}
