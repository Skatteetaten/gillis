package no.skatteetaten.aurora.gillis.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

class SourceSystemException(
    message: String,
    cause: Throwable? = null,
    val code: String = "",
    val errorMessage: String = message,
    val sourceSystem: String? = null
) : RuntimeException(message, cause)

val logger: Logger = LoggerFactory.getLogger(ApplicationController::class.java)

fun <T> Mono<T>.handleError(sourceSystem: String? = null) =
    this.switchIfEmpty(Mono.error(SourceSystemException("Empty response")))
        .doOnError {
            val ex = when (it) {
                is WebClientResponseException -> getSourceExceptionForWebClientResponse(it, sourceSystem)
                is SourceSystemException -> it
                else -> SourceSystemException("Error response", it)
            }
            logger.error("Could not renew cert message=${ex.message} statusCode=${ex.code}", ex)
        }

fun getSourceExceptionForWebClientResponse(ex: WebClientResponseException, sourceSystem: String?) =
    SourceSystemException(
        message = "Error in response, status:${ex.statusCode} message:${ex.statusText}",
        cause = ex,
        sourceSystem = sourceSystem,
        code = ex.statusCode.name
    )
