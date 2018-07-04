package com.zimidy.api.tests.unit

import com.zimidy.api.layers.storage.StorageServerError
import com.zimidy.api.layers.storage.entities.node.nodes.Event
import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.tests.withId
import org.junit.Test

class EntityTest {

    // todo: Replace expected exception by a specific type
    // A type corresponding to CANNOT_GENERATE_HASH_CODE_FOR_ENTITY_WITHOUT_ID should be created
    @Test(expected = StorageServerError::class)
    fun entityWithoutIdCannotBeAddedToHashCollection() {
        hashSetOf(User())
    }

    @Test
    fun entityWithIdCanBeAddedToHashCollection() {
        hashSetOf(withId(User()))
    }

    @Test
    fun entityToStringDoesntCauseStackOverflowOnCyclicDependency() {
        val user = User()
        val event = Event()
        event.creator = user
        user.events += event
        user.toString()
    }
}
