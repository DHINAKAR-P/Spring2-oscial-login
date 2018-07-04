package com.zimidy.api.layers.api.graphql.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.zimidy.api.Inaccessible
import com.zimidy.api.configurations.security.JwtAuthenticationToken
import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.repositories.node.nodes.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder

@Inaccessible
abstract class AbstractResolver {

    @Autowired
    private lateinit var userRepository: UserRepository

    companion object {
        protected fun getAuthenticatedUserId(): String? {
            val jwtAuthentication = SecurityContextHolder.getContext().authentication as JwtAuthenticationToken
            return jwtAuthentication.getUserId()
        }

        protected fun getAuthenticatedUserIdOrThrow(): String {
            return getAuthenticatedUserId() ?: throw RuntimeException("No user is authenticated.")
        }
    }

    protected fun getAuthenticatedUser(): User? {
        val userId = getAuthenticatedUserId() ?: return null
        return userRepository.getOrThrow(userId) // user id, when presented, is expected to be valid
    }

    protected fun getAuthenticatedUserOrThrow(): User {
        val userId = getAuthenticatedUserIdOrThrow()
        return userRepository.getOrThrow(userId)
    }
}

abstract class AbstractQueryResolver : AbstractResolver(), GraphQLQueryResolver

abstract class AbstractMutationResolver : AbstractResolver(), GraphQLMutationResolver
