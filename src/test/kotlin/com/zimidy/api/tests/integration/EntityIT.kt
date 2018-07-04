package com.zimidy.api.tests.integration

import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.repositories.node.nodes.UserRepository
import com.zimidy.api.tests.IntegrationTest
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class EntityIT : IntegrationTest() {

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun create() {
        var user = User()

        Assert.assertNull(user.id)
        Assert.assertEquals(0, userRepository.count())

        user = userRepository.create(user)

        Assert.assertNotNull(user.id)
        Assert.assertEquals(1, userRepository.count())
    }

    @Test
    fun findById() {
        val id = userRepository.create(User()).getIdOrThrow()
        userRepository.getOrThrow(id).getIdOrThrow()
    }
}
