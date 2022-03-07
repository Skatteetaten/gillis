package no.skatteetaten.aurora.gillis.service

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance
import no.skatteetaten.aurora.kubernetes.HttpClientTimeoutConfiguration
import no.skatteetaten.aurora.kubernetes.KubernetesConfiguration
import no.skatteetaten.aurora.kubernetes.RetryConfiguration
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.url
import okhttp3.mockwebserver.MockWebServer

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
open class AbstractOpenShiftServerTest {

    protected val mockServer = MockWebServer()

    private val config = KubernetesConfiguration(
        retry = RetryConfiguration(0),
        timeout = HttpClientTimeoutConfiguration(),
        url = mockServer.url
    )

    protected var mockClient = config.createTestClient("abc")

    @AfterEach
    fun tearDown() {
        mockServer.shutdown()
    }
}
