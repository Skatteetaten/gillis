package no.skatteetaten.aurora.gillis

import io.fabric8.openshift.client.DefaultOpenShiftClient
import io.fabric8.openshift.client.OpenShiftClient
import io.netty.channel.ChannelOption
import no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter
import no.skatteetaten.aurora.filter.logging.RequestKorrelasjon
import no.skatteetaten.aurora.gillis.service.openshift.token.TokenProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint
import org.springframework.web.reactive.function.client.WebClient
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
    @Throws(SSLException::class)
    fun createWebClient(
        @Value("\${gillis.httpclient.readTimeout:10000}") readTimeout: Int,
        @Value("\${gillis.httpclient.connectTimeout:5000}") connectTimeout: Int,
        @Value("\${spring.application.name}") applicationName: String,
        @Value("\${gillis.boober.url}") baseUrl: String,
        tokenProvider: TokenProvider
    ): WebClient {
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector {
                it.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                it.option(ChannelOption.SO_TIMEOUT, readTimeout)
            })
            .defaultHeader("Authorization", "Bearer " + tokenProvider.getToken())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(AuroraHeaderFilter.KORRELASJONS_ID, RequestKorrelasjon.getId())
            .defaultHeader("KlientID", applicationName)
            .baseUrl(baseUrl)
            .build()
    }
}
