package no.skatteetaten.aurora.gillis

import io.fabric8.openshift.client.DefaultOpenShiftClient
import io.fabric8.openshift.client.OpenShiftClient
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter
import no.skatteetaten.aurora.filter.logging.RequestKorrelasjon
import no.skatteetaten.aurora.gillis.service.openshift.token.TokenProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.TcpClient
import javax.net.ssl.SSLException

@Configuration
class ApplicationConfig : BeanPostProcessor {

    val logger: Logger = LoggerFactory.getLogger(ApplicationConfig::class.java)

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
    fun tcpClient(
        @Value("\${gillis.httpclient.readTimeout:10000}") readTimeout: Int,
        @Value("\${gillis.httpclient.writeTimeout:10000}") writeTimeout: Int,
        @Value("\${gillis.httpclient.connectTimeout:5000}") connectTimeout: Int
    ): TcpClient {
        return TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
            .doOnConnected { connection ->
                connection.addHandlerLast(ReadTimeoutHandler(readTimeout))
                    .addHandlerLast(WriteTimeoutHandler(writeTimeout))
            }
    }

    @Bean
    @Throws(SSLException::class)
    fun createWebClient(
        @Value("\${spring.application.name}") applicationName: String,
        @Value("\${gillis.boober.url}") baseUrl: String,
        tokenProvider: TokenProvider,
        tcpClient: TcpClient,
        builder: WebClient.Builder
    ): WebClient {
        logger.info("Created webclient for base url=${baseUrl}")
        return builder
            .clientConnector(ReactorClientHttpConnector(HttpClient.from(tcpClient)))
            .defaultHeader("Authorization", "Bearer " + tokenProvider.getToken())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(AuroraHeaderFilter.KORRELASJONS_ID, RequestKorrelasjon.getId())
            .defaultHeader("KlientID", applicationName)
            .baseUrl(baseUrl)
            .build()
    }
}
