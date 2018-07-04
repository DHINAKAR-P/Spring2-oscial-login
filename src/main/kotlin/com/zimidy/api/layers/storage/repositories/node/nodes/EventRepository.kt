package com.zimidy.api.layers.storage.repositories.node.nodes

import com.zimidy.api.DistanceUnit
import com.zimidy.api.configurations.security.SessionContextHolder
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.CreateEventData
import com.zimidy.api.layers.storage.entities.node.nodes.DeleteEventData
import com.zimidy.api.layers.storage.entities.node.nodes.Event
import com.zimidy.api.layers.storage.entities.node.nodes.Location
import com.zimidy.api.layers.storage.entities.node.nodes.UpdateEventData
import com.zimidy.api.layers.storage.repositories.node.NodeRepository
import com.zimidy.api.layers.storage.repositories.node.NodeRepositoryCustomImpl
import com.zimidy.api.layers.storage.storages.SpatialStorage
import org.neo4j.ogm.session.Session
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.ArrayList

interface EventRepository : NodeRepository<Event>, EventRepositoryCustom {

    @Query("MATCH (d:Event) WHERE LOWER(d.name) CONTAINS LOWER({name}) RETURN d,[ [ (d)-[r_c1:`CREATOR`]->(u1:`User`) | [ r_c1, u1 ] ], [ (d)-[r_g1:`GROUP_CHAT`]->(g1:`GroupChat`) | [ r_g1, g1 ] ] ], ID(d)")
    fun findByName(@Param("name") name: String): Iterable<Event>

    @Query("MATCH (u:User)-[r:ATTENDS]-(e:Event) WHERE id(e) = {eventId} AND id(u) = {userId}  SET r.cohost = {value}")
    fun setCohost(@Param("eventId") eventId: Long, @Param("userId") userId: Long, @Param("value") value: Boolean): Boolean?

    @Query(
        "MATCH (event:Event)-[r:ATTENDS]-(user:User) WHERE user.zimId= {userId} and r.approvalStatus = \"Approved\" " +
            "and (event.startDate >= {longDate} or event.endDate >= {longDate} or event.startDate = 0) RETURN event AS eventId"
    )
    fun getUpcomingEventsById(@Param("userId") userId: NodeId, @Param("longDate") longDate: Long): ArrayList<Event>

    @Query(
        "MATCH (event:Event)-[r:ATTENDS]-(user:User) WHERE user.zimId= {userId} and r.approvalStatus <>\"Denied\" " +
            "and (event.startDate >= {longDate} or event.endDate >= {longDate} or event.startDate = 0)" +
            " RETURN event AS eventId, r.creator, r.detached, r.approvalStatus "
    )
    fun getUpcomingEventsByExtId(@Param("userId") userId: NodeId, @Param("longDate") longDate: Long): Iterable<HashMap<String, Any>>

    @Query(
        "MATCH (event:Event)-[r:ATTENDS]-(user:User) WHERE user.zimId= {userId} and r.creator = true " +
            "and (event.startDate < {longDate} or event.endDate < {longDate}) " +
            "RETURN event AS eventId, r.creator, r.detached, r.approvalStatus"
    )
    fun getPastEventById(@Param("userId") userId: NodeId, @Param("longDate") longDate: Long): Iterable<HashMap<String, Any>>
}

interface EventRepositoryCustom {
    fun findByLocation(location: Location, radiusInUnits: Double, unit: DistanceUnit): Iterable<Event>
    fun create(data: CreateEventData): Event
    fun update(data: UpdateEventData): Event
    fun delete(data: DeleteEventData): Event
}

open class EventRepositoryImpl(
    private val session: Session,
    private val userRepository: UserRepository,
    private val spatialStorage: SpatialStorage,
    private val sessionContextHolder: SessionContextHolder
) : NodeRepositoryCustomImpl<Event>(session, Event::class.java), EventRepositoryCustom {

    companion object {
        private val noParameters = emptyMap<String, String>()
    }

    override fun findByLocation(location: Location, radiusInUnits: Double, unit: DistanceUnit): Iterable<Event> {
        return spatialStorage.findNodesByLocation(Event::class.java, location, radiusInUnits, unit)
    }

    @Transactional
    override fun create(data: CreateEventData): Event {
        val creator = userRepository.getOrThrow(data.creatorUserId)
        val event = Event(
            location = data.location,
            name = data.name,
            description = data.description,
            creator = creator,
            eventType = data.eventType,
            eventSubType = data.eventSubType,
            startDate = data.startDate,
            endDate = data.endDate,
            tags = data.tags,
            address = data.address,
            rating = data.rating,
            website = data.website,
            placeId = data.placeId,
            placeType = data.placeType,
            countryCode = data.countryCode,
            postalCode = data.postalCode,
            privateEvent = data.privateEvent,
            maxAttendees = data.maxAttendees,
            payingEvent = data.payingEvent,
            bringAnything = data.bringAnything,
            bringToEvent = data.bringToEvent,
            anySpecialCriteria = data.anySpecialCriteria,
            specialCriteria = data.specialCriteria,
            sponsored = data.sponsored,
            openEvent = data.openEvent,
            fromChat = data.fromChat,
            pointsCost = data.pointsCost,
            add2rewardEvent = data.add2rewardEvent,
            groups = data.groups,
            friendIds = data.friendIds,
            pictureURL = data.pictureURL

        )
        return create(event)
    }

    @Transactional
    override fun update(data: UpdateEventData): Event {

        val event = getOrThrow(data.id)
        event.location = data.location
        event.name = data.name
        event.description = data.description
        event.eventType = data.eventType
        event.eventSubType = data.eventSubType

        return update(event)
    }

    @Transactional
    override fun delete(data: DeleteEventData): Event {
        val event = getOrThrow(data.id)
        delete(data.id)
        return event
    }
}
