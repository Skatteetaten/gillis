package no.skatteetaten.aurora.gillis.service.openshift.token

import java.io.File
import java.io.IOException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

/**
 * Loader for the Application Token that will be used when loading resources from Openshift that does not require
 * an authenticated user.
 *
 * @param tokenLocation the location on the file system for the file that contains the token
 * @param tokenOverride an optional override of the token that will be used instead of the one on the file system
 *                      - useful for development and testing.
 */
@Component
@Profile("openshift")
class ServiceAccountTokenProvider(
    @Value("\${gillis.openshift.tokenLocation}") val tokenLocation: String
) : TokenProvider {

    private val logger: Logger = LoggerFactory.getLogger(ServiceAccountTokenProvider::class.java)

    private val tokenSupplier = { token: String -> readToken() }.memoize()

    /**
     * Get the Application Token by using the specified tokenOverride if it is set, or else reads the token from the
     * specified file system path. Any value used will be cached forever, so potential changes on the file system will
     * not be picked up.
     *
     * @return
     */
    override fun getToken() = tokenSupplier("token")

    private fun readToken(): String {

        logger.info("Reading application token from tokenLocation={}", tokenLocation)
        try {
            val token: String = File(tokenLocation).readText(Charsets.UTF_8).trimEnd()
            logger.trace(
                "Read token with length={}, firstLetter={}, lastLetter={}", token.length,
                token[0], token[token.length - 1]
            )
            return token
        } catch (e: IOException) {
            throw IllegalStateException("tokenLocation=$tokenLocation could not be read", e)
        }
    }
}

// replace with arrow? https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-syntax/src/main/kotlin/arrow/syntax/function/memoization.kt
class Memoize1<in T, out R>(val f: (T) -> R) : (T) -> R {
    private val values = mutableMapOf<T, R>()
    override fun invoke(x: T): R {
        return values.getOrPut(x) { f(x) }
    }
}

fun <T, R> ((T) -> R).memoize(): (T) -> R = Memoize1(this)
