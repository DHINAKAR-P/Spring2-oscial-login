package com.zimidy.api.layers.storage.entities.relationship.relationships

import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.entities.relationship.Relationship
import lombok.Getter
import lombok.NonNull
import lombok.Setter
import lombok.ToString
import org.neo4j.ogm.annotation.EndNode
import org.neo4j.ogm.annotation.RelationshipEntity
import org.neo4j.ogm.annotation.StartNode

@RelationshipEntity(type = CommonAttendanceRelationship.TYPE)
@Getter
@Setter
@ToString(callSuper = true, exclude = arrayOf("creator", "attendee"))
class CommonAttendanceRelationship : Relationship<User, User>() {

    @StartNode
    override var startNode: User? = null
    @EndNode
    override var endNode: User? = null
    private val creatorId: Long? = null
    private var numberCommonAttendancies: Int? = 0
    private var completedEvents: Int? = 0
    private var everConnectedToCreator = false
    private val pointsPending = 0L
    private val pointsEarned = 0L

    override val type: String
        @NonNull
        get() = TYPE

    fun initCommonAttendanceRelationship(creator: User, attendee: User) {
        this.startNode = creator
        this.endNode = attendee
        this.numberCommonAttendancies = 0
        this.everConnectedToCreator = false
        this.completedEvents = 0
    }

    companion object {

        const val TYPE = "COMMON_ATTENDANCE"
    }
}
