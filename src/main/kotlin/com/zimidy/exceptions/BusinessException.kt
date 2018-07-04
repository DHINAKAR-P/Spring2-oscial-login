package com.zimidy.exceptions

import com.zimidy.api.layers.storage.entities.node.nodes.Event
import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.entities.relationship.relationships.OtherUserRelationship

/**
 * @author Dhinakar Panneer Selvam
 * 07/05/18.
 */
class BusinessException(val messageCode: MessageCode, var args: Any) : RuntimeException() {

    val arguments: Any

    init {
        this.arguments = args
    }

    enum class MessageCode private constructor(val message: String) {
        ENTITY_NOT_FOUND("Entity with id %s not found"),
        USER_NOT_FOUND(User::class.java!!.getSimpleName() + " with id %s not found"),
        // USER_ALERT_NOT_FOUND(UserAlert::class.java!!.getSimpleName() + " with id %s not found"),
        EVENT_NOT_FOUND(Event::class.java!!.getSimpleName() + " with id %s not found"),
        OTHER_USER_REL_NOT_FOUND(OtherUserRelationship::class.java.simpleName + " with id %s not found"),
        // REVIEWED_RELATIONSHIP_NOT_FOUND(ReviewedRelationship::class.java!!.getSimpleName() + " with id %s not found")
    }
}
