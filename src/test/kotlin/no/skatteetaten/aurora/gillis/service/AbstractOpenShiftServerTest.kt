package no.skatteetaten.aurora.gillis.service

import io.fabric8.kubernetes.api.model.RootPaths
import io.fabric8.openshift.client.DefaultOpenShiftClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
open class AbstractOpenShiftServerTest {

    val root = RootPaths()
    protected val mockServer = MockWebServer()
    protected var mockClient = DefaultOpenShiftClient(mockServer.url("/").toString())

    @AfterEach
    fun tearDown() {
        mockServer.shutdown()
    }
}
