package no.skatteetaten.aurora.gillis.service

import io.fabric8.openshift.client.OpenShiftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class RenewService(val client: OpenShiftClient) {

    val logger: Logger = LoggerFactory.getLogger(RenewService::class.java)

        //
        // Code that calls boober
        //

}


