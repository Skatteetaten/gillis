package no.skatteetaten.aurora.gillis

import com.fkorotkov.kubernetes.newObjectMeta
import com.fkorotkov.openshift.metadata
import com.fkorotkov.openshift.newDeploymentConfig
import com.fkorotkov.openshift.newProject
import com.fkorotkov.openshift.status
import io.fabric8.openshift.api.model.DeploymentConfig
import io.fabric8.openshift.api.model.Project
import no.skatteetaten.aurora.gillis.service.CrawlService
import java.time.Duration
import java.time.Instant

data class DeploymentConfigDataBuilder(val dcNamespace: String = "namespace",
                                       val dcKind: String = "Deployment",
                                       val dcName: String = "name",
                                       val dcRemoveAfter: Instant = Instant.now().plusSeconds(60)) {

    fun build(): DeploymentConfig =
            newDeploymentConfig {
                kind = dcKind
                metadata = newObjectMeta {
                    name = dcName
                    namespace = dcNamespace
                    labels = mapOf("removeAfter" to dcRemoveAfter.epochSecond.toString())
                }
            }
}

data class ProjectDataBuilder(
        val pName: String = "name",
        val pAffiliation: String = "affiliation",
        val pPhase: String = "phase",
        val pRemoveAfter: Instant = Instant.now().plusSeconds(60)) {

    fun build(): Project =
            newProject {
                status {
                    phase = pPhase
                }
                metadata {
                    name = pName
                    labels = mapOf("affiliation" to pAffiliation,
                            "removeAfter" to pRemoveAfter.epochSecond.toString())
                }
            }
}


