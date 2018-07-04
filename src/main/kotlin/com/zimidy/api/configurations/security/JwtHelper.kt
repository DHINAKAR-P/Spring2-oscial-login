package com.zimidy.api.configurations.security

import com.zimidy.api.AppProperties
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.crypto.MacProvider
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
internal class JwtHelper(private val app: AppProperties) {

    companion object {
        internal const val TOKEN_COOKIE_NAME = "token"
        private val SECRET_KEY: Key = MacProvider.generateKey()
    }

    fun addTokenToResponse(response: HttpServletResponse, user: User) {
        val compactSerializedClaimsJws = Jwts.builder()
                .setSubject(user.getIdOrThrow())
                .setExpiration(Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)))
                .claim(Claim.ROLES.name, user.roles)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact()

        val tokenCookie = Cookie(TOKEN_COOKIE_NAME, compactSerializedClaimsJws)
        tokenCookie.path = "/"
        tokenCookie.isHttpOnly = true
        tokenCookie.secure = app.env == AppProperties.PROD
        response.addCookie(tokenCookie)
    }

    fun getTokenFromRequest(request: HttpServletRequest): JwtAuthenticationToken? {
        val compactSerializedClaimsJws = getCookieFromRequestByName(request, TOKEN_COOKIE_NAME) ?: return null
        val claims = getClaimsFromToken(compactSerializedClaimsJws) ?: return null
        val userId: NodeId = claims.subject
        @Suppress("UNCHECKED_CAST")
        val roles = claims[Claim.ROLES.name] as List<String>
        return JwtAuthenticationToken(userId, roles)
    }

    private fun getCookieFromRequestByName(request: HttpServletRequest, name: String): String? {
        val cookies = request.cookies ?: return null
        for (cookie in cookies) if (cookie.name == name) return cookie.value
        return null
    }

    private fun getClaimsFromToken(compactSerializedClaimsJws: String): Claims? {
        return try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(compactSerializedClaimsJws).body
        } catch (e: JwtException) {
            // todo: log that if token is bad we treat it as if there were no token at all
            null
        }
    }

    private enum class Claim {
        ROLES
    }
}
