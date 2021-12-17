package no.skatteetaten.aurora.gillis.controller.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class WebSecurityConfig(
    @Value("\${gillis.username}") val userName: String,
    @Value("\${gillis.password}") val password: String,
    private val passwordEncoder: PasswordEncoder,
    private val authEntryPoint: HttpBasicServerAuthenticationEntryPoint
) {

    @Bean
    fun userDetailsService(): MapReactiveUserDetailsService {
        val userDetails = User
            .withUsername(userName)
            .password(passwordEncoder.encode(password))
            .roles("USER")
            .build()
        return MapReactiveUserDetailsService(userDetails)
    }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .csrf().disable()
            .authorizeExchange {
                it.pathMatchers("/docs/index.html", "/", "/actuator", "/actuator/**").permitAll()
                it.pathMatchers("/api/**").hasRole("USER")
            }
            .httpBasic {
                it.authenticationEntryPoint(authEntryPoint)
            }
        return http.build()
    }
}
