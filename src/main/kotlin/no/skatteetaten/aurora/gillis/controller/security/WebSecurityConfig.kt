package no.skatteetaten.aurora.gillis.controller.security

import no.skatteetaten.aurora.springboot.AuroraSecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint

// import javax.servlet.http.HttpServletRequest
//
// @Configuration
// @EnableWebSecurity
// class WebSecurityConfig(
//    @Value("\${management.server.port}") val managementPort: Int,
//    @Value("\${gillis.username}") val userName: String,
//    @Value("\${gillis.password}") val password: String,
//    val passwordEncoder: PasswordEncoder,
//    val authEntryPoint: BasicAuthenticationEntryPoint
//
// ) : WebSecurityConfigurerAdapter() {
//
//    @Autowired
//    @Throws(Exception::class)
//    fun configureGlobalSecurity(auth: AuthenticationManagerBuilder) {
//        auth.inMemoryAuthentication().withUser(userName).password(passwordEncoder.encode(password)).roles("USER")
//    }
//
//    private fun forPort(port: Int) = RequestMatcher { request: HttpServletRequest -> port == request.localPort }
//
//    override fun configure(http: HttpSecurity) {
//
//        http.csrf().disable().sessionManagement()
//            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // We don't need sessions to be created.
//
//        http.authorizeRequests()
//            .requestMatchers(forPort(managementPort)).permitAll()
//            .antMatchers("/docs/index.html").permitAll()
//            .antMatchers("/").permitAll()
//            .antMatchers("/api/**").hasRole("USER")
//            .and().httpBasic().realmName("GILLIS").authenticationEntryPoint(authEntryPoint)
//    }
// }

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
