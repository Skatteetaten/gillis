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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("io.fabric8:openshift-client:6.0.0")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.7")
    testImplementation("com.github.fkorotkov:k8s-kotlin-dsl:3.0.1")
    implementation("io.projectreactor:reactor-tools")
    implementation("no.skatteetaten.aurora.kubernetes:kubernetes-reactor-coroutines-client:1.3.31")

    /* explicit okhttp3 dependencies to force transitive */
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")

    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("io.mockk:mockk:1.12.5")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("no.skatteetaten.aurora:mockwebserver-extensions-kotlin:1.1.8") {
        exclude(group = "no.skatteetaten.aurora.springboot", module = "aurora-spring-boot-mvc-starter")
    }
    testImplementation("com.ninja-squad:springmockk:3.1.1")
    testImplementation("org.junit-pioneer:junit-pioneer:1.7.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("io.projectreactor:reactor-test")
}
