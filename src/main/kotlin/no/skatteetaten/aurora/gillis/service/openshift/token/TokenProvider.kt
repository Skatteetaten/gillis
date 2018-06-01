package no.skatteetaten.aurora.gillis.service.openshift.token

interface TokenProvider {
    fun getToken(): String
}