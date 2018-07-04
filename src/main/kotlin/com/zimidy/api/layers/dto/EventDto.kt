package com.zimidy.api.layers.dto

import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.Event
import java.io.Serializable

class EventDto : Serializable {
    var id: NodeId? = null
    var name: String? = null
    var startDate: Long? = null
    var endDate: Long? = null
    var tags: Array<String>? = null
    var location: LocationDto? = null
    var privateEvent: Boolean = false
    var maxAttendees: Int? = 0
    var payingEvent: Boolean = false
    var cost: Double = 0.toDouble()
    var bringAnything: Boolean = false
    var bringToEvent: String? = null
    var anySpecialCriteria: Boolean = false
    var specialCriteria: String? = null
    var description: String? = null
    var pictureURL: String? = null
    var sponsored: Boolean = false
    var fromChat: Boolean = false
    var state: Event.State? = null
    var creator: Boolean? = null
    var detached: Boolean? = null
    var openEvent: Boolean = false
    var approvalStatus: String? = null
    var users: Set<UserAttendsEventRelationshipDto>? = null
    var eventMessages: Set<EventMessageDto>? = null
    var pointsCost: Int? = 0
    var type: String? = null
    var subType: String? = null
}
