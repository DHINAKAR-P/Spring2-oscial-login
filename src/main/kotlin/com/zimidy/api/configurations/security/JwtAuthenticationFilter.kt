package com.zimidy.api.configurations.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

internal class JwtAuthenticationFilter(private val jwtHelper: JwtHelper) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val jwtAuthenticationToken = jwtHelper.getTokenFromRequest(request as HttpServletRequest)
        SecurityContextHolder.getContext().authentication = jwtAuthenticationToken
        chain.doFilter(request, response)
    }
/*
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
        println("SecurityContextHolder.getContext()" + SecurityContextHolder.getContext().authentication)
        println("requestOauthCode" + request.getParameter("code"))
        println("requestOauthState" + request.getParameter("state"))
        println("requestOauthSession" + request.getParameter("session_state"))
        println("responseOauth" + response.toString())
        var authentication: Authentication? = null
        println("authentication" + authentication!!)
        //todo: need to check the referer
        try {
            if (SecurityContextHolder.getContext().authentication == null) {
                authentication = TokenAuthenticationService.getAuthentication(request as HttpServletRequest)
                SecurityContextHolder.getContext().authentication = authentication
                println("authentication1" + authentication!!)
            }
            filterChain.doFilter(request, response)
        } catch (e: ExpiredJwtException) {
            (response as HttpServletResponse).status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = MediaType.APPLICATION_JSON_UTF8_VALUE
            println("authentication2" + authentication!!)
            val errorResponse = ErrorResponse()
            errorResponse.setDescription(e.message)
            errorResponse.setError(HttpStatus.UNAUTHORIZED.name)
            errorResponse.setMessage(HttpStatus.UNAUTHORIZED.name)
            val body = ObjectMapper().writeValueAsBytes(errorResponse)
            response.outputStream.write(body)
        } catch (e: Exception) {
            (response as HttpServletResponse).status = HttpServletResponse.SC_FORBIDDEN
            response.contentType = MediaType.APPLICATION_JSON_UTF8_VALUE
            println("authentication3" + authentication!!)
            val errorResponse = ErrorResponse()
            errorResponse.setDescription(e.message)
            errorResponse.setError(HttpStatus.FORBIDDEN.name)
            errorResponse.setMessage(HttpStatus.FORBIDDEN.name)
            val body = ObjectMapper().writeValueAsBytes(errorResponse)
            response.outputStream.write(body)
        }
    }*/
}
