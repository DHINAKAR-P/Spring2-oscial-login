package com.zimidy.api.configurations.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority

internal class EmailPasswordAuthenticationToken : UsernamePasswordAuthenticationToken {
    constructor(credentials: EmailPasswordCredentials) : super(credentials.email, credentials.password)
    constructor(credentials: EmailPasswordCredentials, authorities: Collection<GrantedAuthority>) : super(
            credentials.email, credentials.password, authorities
    )
}
