package com.zimidy.api.layers.storage.entities.node.nodes

import com.zimidy.api.Spatial
import com.zimidy.api.layers.mapper.EventMessage
import com.zimidy.api.layers.storage.entities.LocationConverter
import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.relationship.relationships.GroupChatRelationship
import com.zimidy.api.layers.storage.repositories.relationship.UserAttendsEventRelationship
import org.neo4j.ogm.annotation.Relationship
import org.neo4j.ogm.annotation.typeconversion.Convert

data class Event(
    @Convert(LocationConverter::class)
    override var location: Location? = null,
    var startDate: Long = 0,
    var endDate: Long = 0,
    var name: String = "",
    var description: String? = "",
    var eventType: String? = "",
    var eventSubType: String? = "",
    var tags: String? = "",
    var privateEvent: Boolean = false,
    var maxAttendees: Int? = 0,
    var payingEvent: Boolean = false,
    // var cost:Double?=0.0,
    var bringAnything: Boolean = false,
    var bringToEvent: String? = "",
    var anySpecialCriteria: Boolean = false,
    var specialCriteria: String? = "",
    var pictureURL: String? = "",
    var sponsored: Boolean = false,
    var openEvent: Boolean = false,
    var fromChat: Boolean = false,
    var pointsCost: Int? = 0,
    var add2rewardEvent: Boolean = false,
    var state: State? = null,
    var groups: Set<String>? = null,
    var friendIds: Set<Long>? = null,
    /**
     * Location fields
     */
    var address: String? = "",
    var rating: String? = "",
    var website: String? = "",
    var placeId: String? = "",
    var placeType: String? = "",
    var countryCode: String? = "",
    var postalCode: String? = "",
    @Relationship(type = UserAttendsEventRelationship.TYPE, direction = Relationship.INCOMING)
    var userAttendsRels: Set<UserAttendsEventRelationship> = HashSet<UserAttendsEventRelationship>(),
    @Relationship(type = CREATOR, direction = Relationship.OUTGOING)
    var creator: User? = null,
    @Relationship(type = GroupChatRelationship.TYPE, direction = Relationship.OUTGOING)
    var groupChatRel: GroupChatRelationship? = null,
    @Relationship
    var eventMessages: Set<EventMessage> = HashSet<EventMessage>()

) : Node(), Spatial {

    companion object {
        const val CREATOR = "CREATOR"
    }

    enum class State { // don't change order
        CANCELED, ACTIVE, COMPLETE
    }
}

data class CreateEventData(
    var location: Location?,

    var startDate: Long,
    var endDate: Long,
    var name: String,
    var description: String?,
    var creatorUserId: NodeId,
    var pictureURL: String?,

    var eventType: String?,
    var eventSubType: String?,
    var tags: String?,
    var address: String?,
    var rating: String?,
    var website: String?,
    var placeId: String?,
    var placeType: String?,
    var countryCode: String?,
    var postalCode: String?,
    var privateEvent: Boolean,
    var maxAttendees: Int?,
    var payingEvent: Boolean,
    // var cost:Double?=0.0,
    var bringAnything: Boolean,
    var bringToEvent: String?,
    var anySpecialCriteria: Boolean,
    var specialCriteria: String?,
    var sponsored: Boolean,
    var openEvent: Boolean,
    var fromChat: Boolean,
    var pointsCost: Int?,
    var add2rewardEvent: Boolean,

    var groups: Set<String>,
    var friendIds: Set<Long>
)

data class UpdateEventData(
    var id: NodeId,
    var location: Location?,

    var startDate: Long,
    var endDate: Long,
    var name: String,
    var description: String?,
    var creatorUserId: NodeId,
    var pictureURL: String?,

    var eventType: String?,
    var eventSubType: String?,
    var tags: String?,
    var address: String?,
    var rating: String?,
    var website: String?,
    var placeId: String?,
    var placeType: String?,
    var countryCode: String?,
    var postalCode: String?,
    var privateEvent: Boolean,
    var maxAttendees: Int?,
    var payingEvent: Boolean,
    // var cost:Double?=0.0,
    var bringAnything: Boolean,
    var bringToEvent: String?,
    var anySpecialCriteria: Boolean,
    var specialCriteria: String?,
    var sponsored: Boolean,
    var openEvent: Boolean,
    var fromChat: Boolean,
    var pointsCost: Int?,
    var add2rewardEvent: Boolean,

    var groups: Set<String>,
    var friendIds: Set<Long>
)

data class DeleteEventData(
    var id: NodeId
)
