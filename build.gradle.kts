plugins {
    id("java")
    id("no.skatteetaten.gradle.aurora") version "4.3.22"
}

aurora {
    useKotlinDefaults
    useSpringBootDefaults
    useAsciiDoctor
    useSpringBoot {
        useCloudContract
    }
}

dependencies {
    implementation("io.fabric8:openshift-client:5.9.0")
    testImplementation("com.github.fkorotkov:k8s-kotlin-dsl:3.0.1")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("no.skatteetaten.aurora:mockmvc-extensions-kotlin:1.1.7")
    testImplementation("no.skatteetaten.aurora:mockwebserver-extensions-kotlin:1.2.0")
    testImplementation("com.ninja-squad:springmockk:3.0.1")
}

