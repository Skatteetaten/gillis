package no.skatteetaten.aurora.gillis.extensions

import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.Secret
import java.lang.IllegalStateException
import java.time.Instant

const val RENEW_AFTER_LABEL = "stsRenewAfter"
const val APP_ANNOTATION = "gillis.skatteetaten.no/app"
const val COMMON_NAME_ANNOTATION = "gillis.skatteetaten.no/commonName"

fun HasMetadata.renewalTime(): Instant {
    return this.metadata.labels[RENEW_AFTER_LABEL]?.let {
        Instant.ofEpochSecond(it.toLong())
    } ?: throw IllegalStateException("stsRenewAfter is not set or valid timstamp")
}
fun Secret.label(key:String) : String {
    return this.metadata.labels[key] ?:  throw IllegalStateException("$key is not set or valid timstamp")
}
fun Secret.annotation(key:String) : String {
    return this.metadata.annotations[key] ?:  throw IllegalStateException("$key is not set or valid timstamp")
}
