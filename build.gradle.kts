plugins {
    id("java")
    id("no.skatteetaten.gradle.aurora") version "4.3.24"
}

aurora {
    useKotlinDefaults
    useSpringBootDefaults
    useAsciiDoctor
    useSpringBoot {
        useWebFlux
    }
}

dependencies {
    implementation("io.fabric8:openshift-client:5.10.1")
    implementation("no.skatteetaten.aurora.springboot:aurora-spring-security-starter:1.6.2")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.5")
    testImplementation("com.fkorotkov:kubernetes-dsl:2.8.1")

    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("io.mockk:mockk:1.12.1")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("no.skatteetaten.aurora:mockwebserver-extensions-kotlin:1.1.8") {
        exclude(group = "no.skatteetaten.aurora.springboot", module = "aurora-spring-boot-mvc-starter")
    }
    testImplementation("com.ninja-squad:springmockk:3.0.1")
    testImplementation("org.junit-pioneer:junit-pioneer:1.5.0")
}
