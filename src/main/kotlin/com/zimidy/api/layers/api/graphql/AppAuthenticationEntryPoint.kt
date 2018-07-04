package com.zimidy.api.layers.api.graphql

import org.springframework.context.annotation.Configuration
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class AppAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.contentType = "application/json"
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.outputStream.println("{ \"error\": \"" + authException.message + "\" }")
    }

    // todo: should it be added as an access denied handler in security config?
    @ExceptionHandler(AccessDeniedException::class)
    fun commence(request: HttpServletRequest, response: HttpServletResponse, ex: AccessDeniedException) {
        response.contentType = "application/json"
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.outputStream.println("{ \"error\": \"" + ex.message + "\" }")
    }
}
