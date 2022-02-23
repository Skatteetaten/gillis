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
            if (it is WebClientResponseException) {
                logError(
                    SourceSystemException(
                        message = "Error in response, status:${it.statusCode} message:${it.statusText}",
                        cause = it,
                        sourceSystem = sourceSystem,
                        code = it.statusCode.name
                    )
                )
            }
            logError(SourceSystemException("Error response", it))
        }

fun logError(e: SourceSystemException) =
    logger.error("Could not renew cert message=${e.message} statusCode=${e.code}", e)
