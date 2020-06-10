plugins {
    id("org.springframework.cloud.contract")
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("org.jetbrains.kotlin.plugin.spring") version "1.3.72"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("org.sonarqube") version "3.0"

    id("org.springframework.boot") version "2.2.6.RELEASE"
    id("org.asciidoctor.convert") version "2.4.0"

    id("com.gorylenko.gradle-git-properties") version "2.2.2"
    id("com.github.ben-manes.versions") version "0.28.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.14"

    id("no.skatteetaten.gradle.aurora") version "3.5.2"
}

dependencies {
    implementation("io.fabric8:openshift-client:4.10.2")
    testImplementation("com.fkorotkov:kubernetes-dsl:2.8")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.22")
    testImplementation("no.skatteetaten.aurora:mockmvc-extensions-kotlin:1.1.0")
    testImplementation("com.ninja-squad:springmockk:2.0.1")
}
