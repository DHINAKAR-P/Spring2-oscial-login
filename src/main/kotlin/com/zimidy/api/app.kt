package com.zimidy.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.zimidy.api.configurations.security.SecurityConfig
import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.repositories.node.nodes.UserRepository
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories

const val PACKAGE = "com.zimidy.api"
const val PACKAGE_SERVICE = "com.zimidy.service"

@SpringBootApplication
@EntityScan("$PACKAGE.layers.storage.entities", "$PACKAGE_SERVICE")
@EnableNeo4jRepositories("$PACKAGE.layers.storage.repositories")
class App(private val app: AppProperties, private val userRepository: UserRepository) {
    companion object {
        val JSON = jacksonObjectMapper()
    }

    private var initialized = false

    @EventListener(ContextRefreshedEvent::class)
    fun init() {
        if (initialized) return
        createSuperAdminUser()
        initialized = true
    }

    fun createSuperAdminUser() {
        val superAdminUserId = "00000000-0000-0000-0000-000000000000"
        if (userRepository.exists(superAdminUserId)) return
        val superAdminUser = User(
            firstName = app.superAdmin.defaultFirstName,
            lastName = app.superAdmin.defaultLastName,
            email = app.superAdmin.defaultEmail,
            password = SecurityConfig.encodePassword(app.superAdmin.defaultPassword)
        )
        userRepository.create(superAdminUser, superAdminUserId)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(App::class.java, *args)
}
