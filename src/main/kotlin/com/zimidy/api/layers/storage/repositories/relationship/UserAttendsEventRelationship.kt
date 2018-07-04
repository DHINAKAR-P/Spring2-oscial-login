package com.zimidy.api.layers.storage.repositories.relationship

import com.zimidy.api.layers.storage.entities.node.nodes.Event
import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.entities.relationship.Relationship
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.NonNull
import lombok.Setter
import lombok.ToString
import org.neo4j.ogm.annotation.EndNode
import org.neo4j.ogm.annotation.RelationshipEntity
import org.neo4j.ogm.annotation.StartNode
import java.util.Date

@RelationshipEntity(type = UserAttendsEventRelationship.TYPE)
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = arrayOf("user", "event"))
class UserAttendsEventRelationship(user: User, event: Event) : Relationship<User, Event>() {
    @StartNode
    override var startNode: User? = null
    @EndNode
    override var endNode: Event? = null
    var review: String? = null // The review that the user has give for the event
    var reviewDate: Date? = null // The date that the review was created
    var creator: Boolean = false // True if it is the person who created the event
    var approvalStatus: ApprovalStatus? = null // If the user is allowed to attend
    var paid: Boolean = false
    var pointsPending: Long? = null
    var detached: Boolean = false // True if it's the creator and they are not attending their own event
    var cohost: Boolean = false

    override val type: String
        @NonNull
        get() = TYPE

    init {
        setUser(user)
        setEvent(event)
    }

    fun setUser(user: User) {
        this.startNode = user
        user.attendsEventRels.plus(this)
    }

    fun setEvent(event: Event) {
        this.endNode = event
        event.userAttendsRels.plus(this)
    }

    fun detachedCreator(): Boolean {
        return creator && detached
    }

    fun attendee(): Boolean {
        return !detachedCreator() && (approved() || pendingPayment())
    }

    fun pending(): Boolean {
        return approvalStatus == ApprovalStatus.Pending
    }

    fun pendingPayment(): Boolean {
        return approvalStatus == ApprovalStatus.PendingPayment
    }

    fun approved(): Boolean {
        return approvalStatus == ApprovalStatus.Approved
    }

    fun denied(): Boolean {
        return approvalStatus == ApprovalStatus.Denied
    }

    enum class ApprovalStatus {
        Pending, PendingPayment, Approved, Denied
    }

    companion object {

        const val TYPE = "ATTENDS"
    }
}
