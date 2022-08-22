plugins {
    id("java")
    id("no.skatteetaten.gradle.aurora") version "4.5.4"
}

aurora {
    useKotlinDefaults
    useSpringBootDefaults
    useAsciiDoctor
    useSpringBoot {
        useWebFlux
    }
    versions {
        javaSourceCompatibility = "17"
    }
}

dependencies {
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.7")
    implementation("no.skatteetaten.aurora.kubernetes:kubernetes-reactor-coroutines-client:1.3.31")
    implementation("org.springframework.boot:spring-boot-starter-security")

    testImplementation("com.github.fkorotkov:k8s-kotlin-dsl:3.0.1")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("io.mockk:mockk:1.12.5")
    testImplementation("no.skatteetaten.aurora:mockwebserver-extensions-kotlin:1.3.1")

    testImplementation("com.ninja-squad:springmockk:3.1.1")
    testImplementation("io.projectreactor:reactor-test")
}
