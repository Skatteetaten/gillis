package no.skatteetaten.aurora.gillis.controller

import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

class SourceSystemException(
    message: String,
    cause: Throwable? = null,
    val code: String = "",
    val errorMessage: String = message,
    val sourceSystem: String? = null
) : RuntimeException(message, cause)

fun <T> Mono<T>.blockNonNullAndHandleError(duration: Duration = Duration.ofSeconds(30), sourceSystem: String? = null) =
    this.switchIfEmpty(SourceSystemException("Empty response").toMono())
        .blockAndHandleError(duration, sourceSystem)!!

fun <T> Mono<T>.blockAndHandleError(duration: Duration = Duration.ofSeconds(30), sourceSystem: String? = null) =
    this.handleError(sourceSystem)
        .block(duration)

fun <T> Mono<T>.handleError(sourceSystem: String?) =
    this.doOnError {
        if (it is WebClientResponseException) {
            throw SourceSystemException(
                message = "Error in response, status:${it.statusCode} message:${it.statusText}",
                cause = it,
                sourceSystem = sourceSystem,
                code = it.statusCode.name
            )
        }
        throw SourceSystemException("Error response", it)
    }
