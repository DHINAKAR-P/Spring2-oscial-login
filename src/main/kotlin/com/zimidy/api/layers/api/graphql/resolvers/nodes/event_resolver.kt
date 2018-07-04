package com.zimidy.api.layers.api.graphql.resolvers.nodes

import com.zimidy.api.AccessibleByAnyone
import com.zimidy.api.AccessibleByUsers
import com.zimidy.api.DistanceUnit
import com.zimidy.api.layers.storage.entities.node.nodes.Location
import com.zimidy.api.layers.api.graphql.resolvers.AbstractMutationResolver
import com.zimidy.api.layers.api.graphql.resolvers.AbstractQueryResolver
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.Connection
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.ConnectionFactory
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.Cursor
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.Edge
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.GraphQlNode
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.PageInfo
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.CreateEventData
import com.zimidy.api.layers.storage.entities.node.nodes.DeleteEventData
import com.zimidy.api.layers.storage.entities.node.nodes.Event
import com.zimidy.api.layers.storage.entities.node.nodes.UpdateEventData
import com.zimidy.api.layers.storage.repositories.node.nodes.EventRepository
import org.neo4j.ogm.session.Session
import org.springframework.stereotype.Component
import com.zimidy.api.layers.mapper.EventMapper
import com.zimidy.api.layers.dto.EventListDto
import lombok.NonNull
import org.springframework.web.bind.annotation.RequestParam
import java.util.Calendar
import java.util.Collections

@Component
class EventNodeQueryResolver(private val repository: EventRepository, private val session: Session) : AbstractQueryResolver() {

    @AccessibleByUsers
    fun event(id: NodeId): EventNode? {
        val event = repository.get(id)
        return if (event != null) EventNode(event) else null
    }

    @AccessibleByAnyone
    fun events(after: Cursor?, before: Cursor?, first: Int?, last: Int?): EventConnection {
        val allEvents = repository.all().map(::EventNode)
        return EventConnectionFactory(allEvents).create(after = after, before = before, first = first, last = last)
    }

    @AccessibleByUsers
    fun findEventsByLocation(location: Location, radiusInUnits: Double, unit: DistanceUnit): Iterable<EventNode> {
        return repository.findByLocation(location, radiusInUnits, unit).map(::EventNode)
    }

    @AccessibleByAnyone
    fun findEventsByName(name: String): Iterable<EventNode> {
        return repository.findByName(name).map(::EventNode)
    }

    @AccessibleByUsers
    fun setCohost(eventId: Long, userId: Long, value: Boolean): Boolean? {
        val cohostdata = repository.setCohost(eventId, userId, value)
        if (cohostdata == null) {
            return false
        } else {
            session.clear()
        }
        return true
    }

    @AccessibleByUsers
    fun getUpcomingEvents(@RequestParam userId: NodeId): EventListDto {
        val currCal = Calendar.getInstance()
        val longDate = currCal.time.time
        val currUser = repository.getUpcomingEventsById(userId, longDate)
        val eventListDto = EventListDto()
        eventListDto.events = EventMapper.map(currUser)
        val eventList = ArrayList<Event>()
        for (result in currUser) {
            val resultId = result.id as NodeId
            eventList.add(repository.get(resultId)!!)
        }
        eventList.sortWith(compareBy { Event().startDate })
        return eventListDto
    }

    @AccessibleByUsers
    fun getUpcomingEventsExt(userId: NodeId): EventListDto {
        val currCal = Calendar.getInstance()
        val longDate = currCal.time.time
        val currUser = repository.getUpcomingEventsByExtId(userId, longDate) // ?: return ArrayList()
        val eventListDto = EventListDto()
        eventListDto.events = EventMapper.mapExt(getEventsByQueryAndSortByStartDate(currUser))
        return eventListDto
    }

    @AccessibleByUsers
    fun getPastEventsExt(userId: NodeId): EventListDto {
        val currCal = Calendar.getInstance()
        val longDate = currCal.time.time
        val currUser = repository.getPastEventById(userId, longDate) // ?: return ArrayList()
        val eventListDto = EventListDto()
        eventListDto.events = EventMapper.mapExt(getEventsByQueryAndSortByStartDate(currUser))
        return eventListDto
    }

    @NonNull
    private fun getEventsByQueryAndSortByStartDate(currUser: Iterable<HashMap<String, Any>>): ArrayList<Array<Any>> {
        val resultMap = currUser
        val ecList = ArrayList<Array<Any>>()
        for (result in resultMap) {
            val event = result["eventId"] as Event
            val creator = result["r.creator"] as Boolean
            val detached = result["r.detached"] as Boolean
            val aStatus = result["r.approvalStatus"] as String
            ecList.add(arrayOf(event, creator, detached, aStatus))
        }
        ecList.sortWith(compareBy { o -> (o[0] as Event).startDate })
        return ecList
    }
}

@Component
class EventNodeMutationResolver(private val repository: EventRepository) : AbstractMutationResolver() {

    @AccessibleByUsers
    fun createEvent(input: CreateEventInput): EventPayload? {
        val event = repository.create(input.data)
        return EventPayload(clientMutationId = input.clientMutationId, node = EventNode(event))
    }

    @AccessibleByUsers
    fun updateEvent(input: UpdateEventInput): EventPayload? {
        val event = repository.update(input.data)
        return EventPayload(clientMutationId = input.clientMutationId, node = EventNode(event))
    }

    @AccessibleByUsers
    fun deleteEvent(input: DeleteEventInput): EventPayload? {
        val event = repository.delete(input.data)
        return EventPayload(clientMutationId = input.clientMutationId, node = EventNode(event))
    }
}

data class EventNode(private val event: Event) : GraphQlNode(event) {
    val location: Location? = event.location
    val name: String = event.name
    val description: String? = event.description

    var startDate: Long = event.startDate
    var endDate: Long = event.endDate
    var pictureURL: String? = event.pictureURL
    var eventType: String? = event.eventType
    var eventSubType: String? = event.eventSubType
    var tags: String? = event.tags
    var address: String? = event.address
    var rating: String? = event.rating
    var website: String? = event.website
    var placeId: String? = event.placeId
    var placeType: String? = event.placeType
    var countryCode: String? = event.countryCode
    var postalCode: String? = event.postalCode
    var privateEvent: Boolean = event.privateEvent
    var maxAttendees: Int? = event.maxAttendees
    var payingEvent: Boolean = event.payingEvent
    // var cost:Double?=0.0,
    var bringAnything: Boolean = event.bringAnything
    var bringToEvent: String? = event.bringToEvent
    var anySpecialCriteria: Boolean = event.anySpecialCriteria
    var specialCriteria: String? = event.specialCriteria

    var sponsored: Boolean = event.sponsored
    var openEvent: Boolean = event.openEvent
    var fromChat: Boolean = event.fromChat
    var pointsCost: Int? = event.pointsCost
    var add2rewardEvent: Boolean = event.add2rewardEvent

    var groups: Set<String>? = event.groups
    var friendIds: Set<Long>? = event.friendIds
    val creator: UserNode?
        get() {
            val eventCreator = event.creator
            return if (eventCreator != null) UserNode(eventCreator) else null
        }
}

class EventEdge(override val node: EventNode, override val cursor: Cursor) : Edge {
    override fun toString() = asString()
}

class EventConnection(edges: List<EventEdge>, override val pageInfo: PageInfo) : Connection {
    override val edges: List<EventEdge> = Collections.unmodifiableList<EventEdge>(edges)
    override fun toString() = asString()
}

class EventConnectionFactory(allEvents: List<EventNode>) : ConnectionFactory<EventNode, EventEdge, EventConnection>(allEvents) {
    override fun createEdge(node: EventNode, cursor: Cursor) = EventEdge(node, cursor)
    override fun createConnection(edges: List<EventEdge>, pageInfo: PageInfo) = EventConnection(edges, pageInfo)
}

class CreateEventInput(val clientMutationId: String?, val data: CreateEventData)
class UpdateEventInput(val clientMutationId: String?, val data: UpdateEventData)
class DeleteEventInput(val clientMutationId: String?, val data: DeleteEventData)

class EventPayload(val clientMutationId: String?, val node: EventNode)
