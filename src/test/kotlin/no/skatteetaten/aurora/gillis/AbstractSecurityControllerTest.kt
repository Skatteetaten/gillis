package no.skatteetaten.aurora.gillis

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(SpringExtension::class)
open class AbstractSecurityControllerTest : AbstractTest() {

    @Autowired
    lateinit var mockMvc: MockMvc
}
