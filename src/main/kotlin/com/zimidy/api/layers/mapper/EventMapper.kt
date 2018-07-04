package com.zimidy.api.layers.mapper

import com.zimidy.api.layers.dto.EventDto
import com.zimidy.api.layers.storage.entities.node.nodes.Event
import java.util.ArrayList

object EventMapper {
    fun map(event: Event, creator: Boolean? = null, detached: Boolean? = null, aStatus: String? = null): EventDto {
        val dto = EventDto()
        dto.id = event.id
        dto.name = event.name
        dto.startDate = event.startDate
        dto.endDate = event.endDate
        /* if (event.getTags() != null) {
            dto.tags = event.getTags().stream().map(({ Tag.getName() })).toArray(String[]::new  *//* Currently unsupported in Kotlin *//*)
        } */
        // dto.location = LocationMapper.map(event.location)
        dto.privateEvent = event.privateEvent
        dto.maxAttendees = event.maxAttendees
        dto.payingEvent = event.payingEvent
        // dto.cost = event.getCost()
        dto.bringAnything = event.bringAnything
        dto.bringToEvent = event.bringToEvent
        dto.anySpecialCriteria = event.anySpecialCriteria
        dto.specialCriteria = event.specialCriteria
        dto.description = event.description
        dto.pictureURL = event.pictureURL
        dto.sponsored = event.sponsored
        dto.state = event.state
        dto.creator = creator
        dto.detached = detached
        dto.openEvent = event.openEvent
        dto.approvalStatus = aStatus
        dto.pointsCost = event.pointsCost
        // dto.users = UserAttendsEventRelationshipMapper.map(if (event.userAttendsRels == null) HashSet<E>() else event.userAttendsRels)
        dto.eventMessages = EventMessageMapper.map(event.eventMessages)
        dto.type = event.eventType
        dto.subType = event.eventSubType
        return dto
    }

    fun map(events: List<Event>): List<EventDto> {
        val dtos = ArrayList<EventDto>()
        for (event in events) {
            dtos.add(map(event))
        }
        return dtos
    }

    fun mapExt(events: List<Array<Any>>): List<EventDto> {
        val dtos = ArrayList<EventDto>()
        for (event in events) {
            val creator = event[1] as Boolean
            val detached = event[2] as Boolean
            val aStatus = event[3] as String
            dtos.add(map(event[0] as Event, creator, detached, aStatus))
        }
        return dtos
    }
}
