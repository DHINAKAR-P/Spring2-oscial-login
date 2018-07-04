package com.zimidy.api.configurations.security

import com.zimidy.api.AppProperties
import com.zimidy.api.AppProperties.Env
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.Arrays

internal const val PATH_LOGIN = "/login"
internal const val PATH_LOGOUT = "/logout"
internal const val PATH_GRAPHQL = "/graphql"
internal const val PATH_USER_PROFILE = "/gcs/set-user-image"
internal const val PATH_EVENT_PROFILE = "/gcs/set-event-image"
internal const val OAUTH_LOGIN = "/oauth_login"
internal const val LOGIN_FAILURE = "/loginFailure"
internal const val LOGIN_SUCCESS = "/loginSuccess"
internal const val SOCIAL_GOOGLE_URI = "/oauth2/authorization/google"
internal const val SOCIAL_FACEBOOK_URI = "/oauth2/authorization/facebook"

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
internal class SecurityConfig(
    private val app: AppProperties,
    private val userDetailsService: AppUserDetailsService,
    private val jwtHelper: JwtHelper
) : WebSecurityConfigurerAdapter() {

    companion object {
        private val PASSWORD_ENCODER = BCryptPasswordEncoder()

        fun encodePassword(password: String?): String? =
            if (password != null) PASSWORD_ENCODER.encode(password) else null
    }

    override fun configure(http: HttpSecurity) {
        disableUnnecessaryDefaults(http)
        // @formatter:off
        http
            // .cors().configurationSource(corsConfigurationSource()).and()
//            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
//            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll() // To allow CORS pre-flight requests
            .antMatchers(HttpMethod.POST, PATH_LOGIN).permitAll()
            .antMatchers(HttpMethod.POST, PATH_LOGOUT).permitAll()
            .antMatchers(HttpMethod.POST, PATH_GRAPHQL).permitAll()
            .antMatchers(HttpMethod.POST, PATH_USER_PROFILE).permitAll()
            .antMatchers(HttpMethod.POST, PATH_EVENT_PROFILE).permitAll()
            .antMatchers(HttpMethod.GET, OAUTH_LOGIN).permitAll()
            .antMatchers(HttpMethod.GET, LOGIN_FAILURE).permitAll()
            .antMatchers(HttpMethod.GET, LOGIN_SUCCESS).permitAll()
            .antMatchers(HttpMethod.GET, SOCIAL_GOOGLE_URI).permitAll()
            .antMatchers(HttpMethod.GET, SOCIAL_FACEBOOK_URI).permitAll()
            .antMatchers(HttpMethod.GET, "/browser").permitAll()
            .antMatchers(HttpMethod.GET, "/favicon.ico").permitAll() // todo: who needs it?
            .antMatchers(HttpMethod.GET, "/actuator/health").permitAll()
            .anyRequest().authenticated()
//            .logoutSuccessHandler(HttpStatusReturningLogoutSuccessHandler())
            .and()
            .addFilterBefore(
                JwtLoginFilter(jwtHelper, authenticationManager()),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                JwtAuthenticationFilter(jwtHelper),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .formLogin().loginPage(PATH_LOGIN).permitAll().and().logout()
            .and()
            .oauth2Login().loginPage("/oauth_login")
            .permitAll()
            .defaultSuccessUrl("/loginSuccess")
            .failureUrl("/loginFailure")
        /*.and()
        .exceptionHandling()
        .authenticationEntryPoint(AppAuthenticationEntryPoint())
        .and()
        .logout()
        .logoutUrl(PATH_LOGOUT)
        .deleteCookies(JwtHelper.TOKEN_COOKIE_NAME)
        .and()
        .oauth2Login()e
        .loginPage(OAUTH_LOGIN).defaultSuccessUrl(LOGIN_SUCCESS)
        .failureUrl(LOGIN_FAILURE)*/
        /*.and()
        .logout()
        .logoutUrl(PATH_LOGOUT)
        .deleteCookies(JwtHelper.TOKEN_COOKIE_NAME)*/
        // @formatter:on bolt://cunnectable.com:7687, username: neo4j, password: WinterGr33n
    }

    override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(PASSWORD_ENCODER)
    }

    private fun disableUnnecessaryDefaults(http: HttpSecurity) {
        @Suppress("UNCHECKED_CAST")
        val defaultLoginPageConfigurerClass =
            DefaultLoginPageConfigurer::class.java as Class<DefaultLoginPageConfigurer<HttpSecurity>>
        // @formatter:off
        http
            .csrf().disable() // todo: does it expose any security vulnerabilities?
            .removeConfigurer(defaultLoginPageConfigurerClass)
        // @formatter:on
    }

    // The purpose of CORS is to relax the Same Origin Policy implemented by browsers.
    // In our case api and web modules are always belong to different origins (at least domain or port are different).
    // Thus, we need to fulfil the following requirements:
    // 1. a web should be able to to read responses from an api if they both are running within the same env
    // 2. a web running locally should be able to read responses from an api running on a any remote env, except prod
    private fun corsConfigurationSource(): CorsConfigurationSource {

        val corsConfiguration = object : CorsConfiguration() {
            private val web = "${app.web.protocol}://www.${app.web.domain}:${app.web.port}"

            override fun checkOrigin(requestOrigin: String?): String? {
                if (requestOrigin == null) return null
                if (app.env == Env.DEV) return requestOrigin
                if (app.env == Env.PROD) return null
                if (requestOrigin.equals(web, ignoreCase = true)) return requestOrigin
                val urlComponents = requestOrigin.split(":")
                if (urlComponents.size == 3) {
                    val (protocol, _, port) = urlComponents
                    return "$protocol://localhost:$port"
                }
                return null
            }
        }

        // Order of applied mappings does matter:
        return UrlBasedCorsConfigurationSource().apply {
            corsConfiguration.apply {
                allowedMethods = Arrays.asList("POST")
                allowedHeaders = Arrays.asList("Content-Type")
                allowCredentials = true
            }
            registerCorsConfiguration(PATH_LOGIN, corsConfiguration)
            registerCorsConfiguration(PATH_LOGOUT, corsConfiguration)
            registerCorsConfiguration(PATH_GRAPHQL, corsConfiguration)
            registerCorsConfiguration(PATH_USER_PROFILE, corsConfiguration)
            registerCorsConfiguration(PATH_EVENT_PROFILE, corsConfiguration)
            // Spring will allow any origin for patches without a mapping; but that is insecure!
            // That is why we explicitly deny all origins for any path, which was left without a mapping:
            registerCorsConfiguration("/**", CorsConfiguration())
        }
    }
}
