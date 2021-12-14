package no.skatteetaten.aurora.gillis.controller.security

import no.skatteetaten.aurora.springboot.AuroraSecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class WebSecurityConfig(
    private val authenticationManager: ReactiveAuthenticationManager,
    private val securityContextRepository: AuroraSecurityContextRepository,
    private val authEntryPoint: HttpBasicServerAuthenticationEntryPoint
) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http
        .httpBasic().disable()
        .formLogin().disable()
        .csrf().disable()
        .logout().disable()
        .authenticationManager(authenticationManager)
        .securityContextRepository(securityContextRepository)
        .authorizeExchange().pathMatchers("/docs/index.html", "/", "/actuator", "/actuator/**").permitAll()
        .pathMatchers("/api/**").hasRole("USER")
        .and()
        .httpBasic().authenticationEntryPoint(authEntryPoint)
        .and()
        .build()
}
