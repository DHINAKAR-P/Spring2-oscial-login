package com.zimidy.api.configurations.security

import com.zimidy.api.layers.storage.entities.node.nodes.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.LinkedList

internal class AppUserDetails(val user: User) : UserDetails {

    override fun getUsername(): String = user.getIdOrThrow()

    override fun getPassword(): String? = user.password

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return user.roles.mapTo(LinkedList<GrantedAuthority>()) { SimpleGrantedAuthority(it) }
    }

    override fun isEnabled(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isAccountNonExpired(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true
}
