package com.zimidy.api.configurations.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.LinkedList

internal class JwtAuthenticationToken : UsernamePasswordAuthenticationToken {
    constructor(userId: String) : super(userId, null)
    constructor(userId: String, roles: Collection<String>) : super(
            userId, null, roles.mapTo(LinkedList<GrantedAuthority>()) { SimpleGrantedAuthority(it) }
    )

    fun getUserId(): String? = super.getPrincipal() as String?
}
