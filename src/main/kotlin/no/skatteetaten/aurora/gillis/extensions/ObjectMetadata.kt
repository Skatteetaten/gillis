package no.skatteetaten.aurora.gillis.extensions

import io.fabric8.kubernetes.api.model.HasMetadata
import java.lang.IllegalStateException
import java.time.Instant

const val RENEW_AFTER_LABEL = "stsRenewAfter"

fun HasMetadata.renewalTime(): Instant {
    return this.metadata.labels[RENEW_AFTER_LABEL]?.let {
        Instant.ofEpochSecond(it.toLong())
    } ?: throw IllegalStateException("stsRenewAfter is not set or valid timstamp")
}
