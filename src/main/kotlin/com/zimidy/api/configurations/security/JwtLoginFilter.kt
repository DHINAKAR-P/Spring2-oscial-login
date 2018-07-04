package com.zimidy.api.configurations.security

import com.fasterxml.jackson.module.kotlin.readValue
import com.zimidy.api.App.Companion.JSON
import com.zimidy.api.layers.api.graphql.resolvers.nodes.UserNode
import com.zimidy.api.layers.storage.entities.node.nodes.User
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class JwtLoginFilter(private val jwtHelper: JwtHelper, authenticationManager: AuthenticationManager) :
    AbstractAuthenticationProcessingFilter(AntPathRequestMatcher(PATH_LOGIN, HttpMethod.POST.name, true)) {

    init {
        this.authenticationManager = authenticationManager
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val requestBodyInputStream = request.inputStream
        val credentials: EmailPasswordCredentials = JSON.readValue(requestBodyInputStream)
        val token = EmailPasswordAuthenticationToken(credentials)
        return authenticationManager.authenticate(token)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain?,
        authentication: Authentication
    ) {
        val userDetails = authentication.principal as AppUserDetails
        val user = userDetails.user
        jwtHelper.addTokenToResponse(response, user)
        addUserNodeToResponse(response, user)
    }

    private fun addUserNodeToResponse(response: HttpServletResponse, user: User) {
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write(JSON.writeValueAsString(UserNode(user)))
        response.writer.close()
    }
}
