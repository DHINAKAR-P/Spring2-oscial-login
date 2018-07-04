package com.zimidy.api.layers.dto

import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.repositories.relationship.UserAttendsEventRelationship
import java.io.Serializable
import java.util.Date

class UserAttendsEventRelationshipDto : Serializable {
    var id: Long? = null
    var review: String? = null // The review that the user has give for the event
    var reviewDate: Date? = null // The date that the review was created
    var creator: Boolean = false // True if it is the person who created the event
    var approvalStatus: UserAttendsEventRelationship.ApprovalStatus? = null // If the user is allowed to attend
    var event: NodeId? = null
    var user: NodeId? = null
    var userName: String? = null
    var userImage: String? = null
    var userPoints: Long? = null
}
