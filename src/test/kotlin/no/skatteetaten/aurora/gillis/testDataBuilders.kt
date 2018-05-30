package no.skatteetaten.aurora.gillis

import com.fkorotkov.kubernetes.newObjectMeta
import com.fkorotkov.kubernetes.newSecret
import io.fabric8.kubernetes.api.model.Secret
import no.skatteetaten.aurora.gillis.extensions.APP_ANNOTATION
import no.skatteetaten.aurora.gillis.extensions.COMMON_NAME_ANNOTATION
import no.skatteetaten.aurora.gillis.extensions.RENEW_AFTER_LABEL
import no.skatteetaten.aurora.gillis.extensions.RENEW_BEFORE_ANNOTATION
import no.skatteetaten.aurora.gillis.extensions.TTL_ANNOTATION
import java.time.Instant

data class SecretDataBuilder(
    val secretNamespace: String = "namespace",
    val appName: String = "app",
    val ttl: String = "1d",
    val renewBefore: String = "12h",
    val comonName:String ="no.skatteetaten.aurora.app",
    val stsRenewAfter: Instant = Instant.now().plusSeconds(60)
) {

    fun build(): Secret {
        return newSecret {
            metadata = newObjectMeta {
                name = "$appName-cert"
                namespace = secretNamespace
                annotations = mapOf(
                    APP_ANNOTATION to appName,
                    TTL_ANNOTATION to ttl,
                    RENEW_BEFORE_ANNOTATION to renewBefore,
                    COMMON_NAME_ANNOTATION to comonName

                )
                labels = mapOf(RENEW_AFTER_LABEL to stsRenewAfter.epochSecond.toString())
            }
        }
    }
}


