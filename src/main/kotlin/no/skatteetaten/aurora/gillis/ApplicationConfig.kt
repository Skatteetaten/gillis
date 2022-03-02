package no.skatteetaten.aurora.gillis

import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import io.netty.channel.ChannelOption
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import mu.KotlinLogging
import no.skatteetaten.aurora.kubernetes.config.kubernetesToken
import reactor.kotlin.core.publisher.toMono
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.SslProvider

private val logger = KotlinLogging.logger {}

@Configuration
class ApplicationConfig(
    @Value("\${gillis.httpclient.readTimeout:10000}") val readTimeout: Long,
    @Value("\${gillis.httpclient.writeTimeout:10000}") val writeTimeout: Long,
    @Value("\${gillis.httpclient.connectTimeout:5000}") val connectTimeout: Int
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun basic(): HttpBasicServerAuthenticationEntryPoint {
        return HttpBasicServerAuthenticationEntryPoint().also {
            it.setRealm("GILLIS")
        }
    }

    @Bean
    fun createWebClient(
        @Value("\${spring.application.name}") applicationName: String,
        @Value("\${integrations.boober.url}") baseUrl: String,
        builder: WebClient.Builder
    ): WebClient {
        logger.info { "Created webclient for base url=$baseUrl" }
        return builder.init()
            .defaultHeader("Authorization", "Bearer ${kubernetesToken()}")
            .baseUrl(baseUrl)
            .build()
    }

    fun WebClient.Builder.init() =
        this.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(
                ExchangeFilterFunction.ofRequestProcessor {
                    logger.debug { "HttpRequest method=${it.method()} url=${it.url()}" }
                    it.toMono()
                }
            )
            .clientConnector(clientConnector())

    private fun clientConnector(ssl: Boolean = false): ReactorClientHttpConnector {
        val httpClient =
            HttpClient.create().compress(true)
                .tcpConfiguration {
                    it.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                        .doOnConnected { connection ->
                            connection.addHandlerLast(ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                            connection.addHandlerLast(WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS))
                        }
                }

        if (ssl) {
            val sslProvider = SslProvider.builder().sslContext(
                SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
            ).defaultConfiguration(SslProvider.DefaultConfigurationType.NONE).build()
            httpClient.tcpConfiguration {
                it.secure(sslProvider)
            }
        }

        return ReactorClientHttpConnector(httpClient)
    }
}
