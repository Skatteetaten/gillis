package no.skatteetaten.aurora.gillis

import com.fkorotkov.kubernetes.newObjectMeta
import com.fkorotkov.kubernetes.newOwnerReference
import com.fkorotkov.kubernetes.newSecret
import io.fabric8.kubernetes.api.model.Secret
import no.skatteetaten.aurora.gillis.extensions.APP_ANNOTATION
import no.skatteetaten.aurora.gillis.extensions.COMMON_NAME_ANNOTATION
import no.skatteetaten.aurora.gillis.extensions.RENEW_AFTER_LABEL
import java.time.Instant

data class SecretDataBuilder(
    val secretNamespace: String = "namespace",
    val appName: String = "app",
    val comonName: String = "no.skatteetaten.aurora.app",
    val stsRenewAfter: Instant = Instant.now().plusSeconds(60)
) {

    fun build(): Secret {
        return newSecret {
            metadata = newObjectMeta {
                ownerReferences = listOf(
                    newOwnerReference {
                        apiVersion = "v1"
                        kind = "DeploymentConfig"
                        metadata = newObjectMeta {
                            name = appName
                            uid = "123"
                        }
                    }
                )
                name = "$appName-cert"
                namespace = secretNamespace
                annotations = mapOf(
                    APP_ANNOTATION to appName,
                    COMMON_NAME_ANNOTATION to comonName
                )
                labels = mapOf(
                    "affiliation" to "foo",
                    RENEW_AFTER_LABEL to stsRenewAfter.epochSecond.toString()
                )
            }
        }
    }
}
