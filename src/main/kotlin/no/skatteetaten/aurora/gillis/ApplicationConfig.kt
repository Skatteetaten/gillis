package no.skatteetaten.aurora.gillis

import io.fabric8.openshift.client.DefaultOpenShiftClient
import io.fabric8.openshift.client.OpenShiftClient
import io.netty.channel.ChannelOption
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter
import no.skatteetaten.aurora.filter.logging.RequestKorrelasjon
import no.skatteetaten.aurora.gillis.service.SharedSecretReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint
import org.springframework.web.reactive.function.client.WebClient
import java.io.File
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLException

@Configuration
class ApplicationConfig : BeanPostProcessor {

    @Bean
    fun client(): OpenShiftClient {
        return DefaultOpenShiftClient()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun basic(): BasicAuthenticationEntryPoint {
        return BasicAuthenticationEntryPoint().also {
            it.realmName = "GILLIS"
        }
    }

    @Bean
    @Profile("local")
    fun sslContext() : SslContext {
        return SslContextBuilder
            .forClient()
            //TODO kun i local profil ellers les fra fil
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build()
    }

    @Bean
    @Profile("openshift")
    fun openShiftSslContext() : SslContext {
        return SslContextBuilder
            .forClient()
            .trustManager(File("/var/run/secrets/kubernetes.io/serviceaccount/ca.crt"))
            .build()
    }

    @Bean
    @Throws(SSLException::class)
    fun createWebClient(
        @Value("\${gillis.httpclient.readTimeout:10000}") readTimeout: Int,
        @Value("\${gillis.httpclient.connectTimeout:5000}") connectTimeout: Int,
        @Value("\${spring.application.name}") applicationName: String,
        @Value("\${gillis.boober.url}") baseUrl: String,
        sharedSecretReader: SharedSecretReader,
        sslContext:SslContext
    ): WebClient {

        val httpConnector = ReactorClientHttpConnector {
            it.sslContext(sslContext)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .option(ChannelOption.SO_TIMEOUT, readTimeout)
        }
        return WebClient.builder().clientConnector(httpConnector)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer aurora-token ${sharedSecretReader.secret}")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(AuroraHeaderFilter.KORRELASJONS_ID, RequestKorrelasjon.getId())
            .defaultHeader("KlientID", applicationName)
            .baseUrl(baseUrl)
            .build()
    }
}
