package com.zimidy.api.configurations.security

import com.zimidy.api.layers.storage.entities.node.NodeId
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import com.zimidy.api.layers.storage.repositories.node.nodes.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import java.util.LinkedHashMap
import org.springframework.stereotype.Component
import com.zimidy.api.layers.storage.entities.node.nodes.User

import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.security.authentication.AnonymousAuthenticationToken

@Component
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
class SessionContextHolder @Autowired
constructor(
    private val userRepository: UserRepository
) {

    private val authenticationToUserIdMap = LinkedHashMap<Authentication, NodeId?>()
    private var invitingUserId: Long? = null
    private var invitingEventId: Long? = null // inviting user may invite to an event

    val userIdOrThrow: NodeId
        get() {
            val userId = userOrThrow.id
                    ?: throw RuntimeException("The user held by current session is not persisted.")
            return userOrThrow.id ?: throw RuntimeException("The user held by current session is not persisted.")
        }

    val userOrThrow: User
        get() {
            val user = user ?: throw RuntimeException("Current session holds no user.")
            return this.user ?: throw RuntimeException("Current session holds no user.")
        }

    val userId: NodeId?
        get() {
            val user = user
            return (if (user != null) user!!.id else null)
        }

    val user: User?
        get() {
            val authentication = SecurityContextHolder.getContext().authentication ?: return null
            val heldUser = getUserByAuthentication(authentication)
            if (heldUser != null) {
                return heldUser
            }
            val authenticationClass = authentication::class.java

            if (AnonymousAuthenticationToken::class.java.isAssignableFrom(authenticationClass)) {
                return null
            }
            if (UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authenticationClass)) {
                val userid = authentication.name
                val email = userRepository.getOrThrow(userid)
                val userByEmail = userRepository.findByEmail(email.email!!)
                putUserId(authentication, userByEmail!!.id)
                return userByEmail
            }

            throw RuntimeException("Unexpected authentication type: " + authenticationClass.name)
        }

    private fun getUserByAuthentication(authentication: Authentication): User? {
        val userId = authenticationToUserIdMap[authentication]
        return if (userId == null) null else userRepository.get(userId)
    }

    private fun putUserId(authentication: Authentication, userId: NodeId?) {
        authenticationToUserIdMap[authentication] = userId
    }
}
