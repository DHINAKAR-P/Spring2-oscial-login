package com.zimidy.api.layers.api.graphql.resolvers.nodes

import com.zimidy.api.AccessibleByAnyone
import com.zimidy.api.AccessibleByUsers
import com.zimidy.api.configurations.security.SecurityConfig
import com.zimidy.api.layers.api.graphql.resolvers.AbstractMutationResolver
import com.zimidy.api.layers.api.graphql.resolvers.AbstractQueryResolver
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.Connection
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.ConnectionFactory
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.Cursor
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.Edge
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.GraphQlNode
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.PageInfo
import com.zimidy.api.layers.dto.UserHasChatListDto
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.repositories.node.nodes.UserRepository
import org.springframework.stereotype.Component
import java.util.Collections.unmodifiableList
import com.zimidy.api.configurations.security.SessionContextHolder
import com.zimidy.api.layers.dto.UserHasChatDto
import com.zimidy.api.layers.mapper.UserAlert
import com.zimidy.api.layers.mapper.UserHasChatMapper
import com.zimidy.api.layers.storage.entities.node.nodes.CreateUserData
import com.zimidy.api.layers.storage.entities.node.nodes.UpdateUserData
import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.entities.relationship.relationships.OtherUserRelationship
import com.zimidy.api.layers.storage.repositories.relationship.UserAttendsEventRelationship

@Component
class UserNodeQueryResolver(
    private val repository: UserRepository,
    private val sessionContextHolder: SessionContextHolder
) : AbstractQueryResolver() {

    @AccessibleByUsers
    fun user(id: NodeId): UserNode? {
        val user = repository.get(id)
        return if (user != null) UserNode(user) else null
    }

    @AccessibleByAnyone
    fun users(before: Cursor?, after: Cursor?, first: Int?, last: Int?): UserConnection {
        val allUsers = repository.all().map { user -> UserNode(user) }
        return UserConnectionFactory(allUsers).create(after = after, before = before, first = first, last = last)
    }

    @AccessibleByUsers
    fun isConnection(userId: NodeId, otherUserId: Long): Boolean {
        return repository.isConnected(userId, otherUserId)
    }

    @AccessibleByUsers
    fun plivoInvite(plivouser: String, plivoSignInUrl: String?, eventId: Long, userId: NodeId): String? {
        return repository.plivoInvite(plivouser, plivoSignInUrl, eventId, userId)
    }

    @AccessibleByUsers
    fun getDfaUsers(name: String): UserHasChatListDto {
        val currUser = repository.getOrThrow(sessionContextHolder.userId!!)
        var dfaUsers = repository.getDfaUsers(sessionContextHolder.userId!!)
        val resultMap = dfaUsers
        var results: MutableList<User> = ArrayList()
        for (result in resultMap) {
            val resultId = result.id as NodeId
            results.add(repository.get(resultId)!!)
        }
        results.sortWith(compareBy { User().buildFullName() })
        if (results.size > 150) { // Only return closest 150
            results = results.subList(0, 50)
        }
        val userHasChatList = ArrayList<UserHasChatDto>()
        for (user in results) {
            if (user.buildFullName().toLowerCase().startsWith(name.toLowerCase())) {
                val relationship = currUser.otherUserRels.stream().filter({ rel -> isConnection(user.id!!, rel.id!!) })
                        .findFirst().orElse(null)
                val userChatDto = UserHasChatMapper.map(user)
                if (hasOpenChat(relationship)) {
                    userChatDto.hasOpenChat = true
                }
                if (hasConnection(relationship)) {
                    userChatDto.connected = true
                }
                val userAlertList = user.userAlerts
                for (userAlert in userAlertList) {
                    if (userAlert.originatorId!!.toLong() == sessionContextHolder.userId!!.toLong() && UserAlert.AlertStatus.NEW.toString().toLowerCase() == userAlert.status.toString().toLowerCase()) {
                        userChatDto.hasRequestSent = true
                    }
                }
                userHasChatList.add(userChatDto)
            }
        }
        val chatList = UserHasChatListDto()
        chatList.users = userHasChatList
        return chatList
    }

    @AccessibleByUsers
    fun getDfaOtherUsers(userId: NodeId, otherUserId: NodeId): UserHasChatListDto {
        val currUser = repository.get(userId)
        val otherUsers = repository.getDfaOtherUsers(userId)
        val resultMap = otherUsers
        var results: MutableList<User> = ArrayList()
        for (result in resultMap) {
            val resultId = result.id as NodeId
            results.add(repository.get(resultId)!!)
        }
        results.sortWith(compareBy { User().buildFullName() })
        if (results.size > 150) { // Only return closest 150
            results = results.subList(0, 50)
        }
        val userHasChatList = ArrayList<UserHasChatDto>()
        for (user in results) {
            if (user.id.equals(otherUserId)) {
                val relationship = currUser!!.otherUserRels.stream().filter({ rel -> isConnection(user.id!!, rel.id!!) })
                        .findFirst().orElse(null)
                val userChatDto = UserHasChatMapper.map(user)
                if (hasOpenChat(relationship)) {
                    userChatDto.hasOpenChat = true
                }
                if (hasConnection(relationship)) {
                    userChatDto.connected = true
                }
                val userAlertList = user.userAlerts
                for (userAlert in userAlertList) {
                    if (userAlert.originatorId!!.toLong() == userId.toLong() && UserAlert.AlertStatus.NEW.toString().toLowerCase() == userAlert.status.toString().toLowerCase()) {
                        userChatDto.hasRequestSent = true
                    }
                }
                userHasChatList.add(userChatDto)
            }
        }
        val chatList = UserHasChatListDto()
        chatList.users = userHasChatList
        return chatList
    }

    private fun hasConnection(relationship: OtherUserRelationship?): Boolean {
        return relationship != null && OtherUserRelationship.FriendState.ACCEPTED.toString().equals(relationship.lowFriendState.toString(), ignoreCase = true) && OtherUserRelationship.FriendState.ACCEPTED.toString().equals(relationship.highFriendState.toString(), ignoreCase = true)
    }

    private fun hasOpenChat(relationship: OtherUserRelationship?): Boolean {
        if (relationship != null && OtherUserRelationship.FriendState.ACCEPTED.toString().equals(relationship.lowFriendState.toString(), ignoreCase = true) && OtherUserRelationship.FriendState.ACCEPTED.toString().equals(relationship.highFriendState.toString(), ignoreCase = true)) {
            return true
        } else if (relationship != null && OtherUserRelationship.ChatState.ACCEPTED.toString().equals(relationship.lowChatState.toString(), ignoreCase = true) && OtherUserRelationship.ChatState.ACCEPTED.toString().equals(relationship.highChatState.toString(), ignoreCase = true)) {
            return true
        }
        return false
    }
}

@Component
class UserNodeMutationResolver(private val repository: UserRepository) : AbstractMutationResolver() {

    @AccessibleByAnyone
    fun createUser(input: CreateUserInput): UserPayload? {
        val user = repository.create(input.data)
        return UserPayload(clientMutationId = input.clientMutationId, node = UserNode(user))
    }

    @AccessibleByUsers // todo: user must be unable to update another user, only himself (similar applies to events)
    fun updateUser(input: UpdateUserInput): UserPayload? {
        val user = repository.update(input.data)
        return UserPayload(clientMutationId = input.clientMutationId, node = UserNode(user))
    }
}

data class UserNode(private val user: User) : GraphQlNode(user) {
    val firstName: String = user.firstName
    val lastName: String = user.lastName
    val email: String? = user.email
    val password: String? = SecurityConfig.encodePassword(user.password)
    val dfa: Boolean? = user.dfa
    val soundOn: Boolean? = user.soundOn
    val pointBalance: Long? = user.pointBalance
    val pointsPending: Long? = user.pointsPending
    var defaultImageFileName: String? = user.defaultImageFileName
    var gender: User.Gender? = user.gender
    var homeLocation: String? = user.homeLocation
    var fuzzyHomeLocation: String? = user.fuzzyHomeLocation
    var homeLocality: String? = user.homeLocality
    var passwordVerificationCode: String? = user.passwordVerificationCode
    var emailVerificationCode: String? = user.emailVerificationCode
    var mobileVerificationCode: String? = user.mobileVerificationCode
    var mobileTextAlerts: Boolean? = user.mobileTextAlerts
    var currentLocation: String? = user.currentLocation
    var fuzzyCurrentLocation: String? = user.fuzzyCurrentLocation
    var currentLocality: String? = user.currentLocality
    var birthDate: Long? = user.birthDate
    var reviewable: Boolean? = user.reviewable
    var gcmToken: String? = user.gcmToken
    var commOptOut: Boolean? = user.commOptOut
    var staff: Boolean = user.staff
    var textEnabled: Boolean = user.textEnabled
    var stripeID: String? = user.stripeID
    var notificationsEnabled: Boolean = user.notificationsEnabled
    var showUpcomingEvents: Boolean = user.showUpcomingEvents
    var showRecentEvents: Boolean = user.showRecentEvents
    var distanceUnit: String? = user.distanceUnit
    var gcmRegistrationToken: String? = user.gcmRegistrationToken
    var eventsCreated: Long = user.eventsCreated
    var description: String? = user.description
    var userAlerts: Set<UserAlert> = user.userAlerts
    var otherUserRels: Set<OtherUserRelationship> = user.otherUserRels
    var attendsEventRels: Set<UserAttendsEventRelationship> = user.attendsEventRels

    fun events(before: Cursor?, after: Cursor?, first: Int?, last: Int?): EventConnection {
        val eventConnectionFactory = EventConnectionFactory(user.events.map(::EventNode))
        return eventConnectionFactory.create(after = after, before = before, first = first, last = last)
    }
}

class UserEdge(override val node: UserNode, override val cursor: Cursor) : Edge {
    override fun toString() = asString()
}

class UserConnection(edges: List<UserEdge>, override val pageInfo: PageInfo) : Connection {
    override val edges: List<UserEdge> = unmodifiableList<UserEdge>(edges)
    override fun toString() = asString()
}

class UserConnectionFactory(allUsers: List<UserNode>) : ConnectionFactory<UserNode, UserEdge, UserConnection>(allUsers) {
    override fun createEdge(node: UserNode, cursor: Cursor) = UserEdge(node, cursor)
    override fun createConnection(edges: List<UserEdge>, pageInfo: PageInfo) = UserConnection(edges, pageInfo)
}

class CreateUserInput(val clientMutationId: String?, val data: CreateUserData)
class UpdateUserInput(val clientMutationId: String?, val data: UpdateUserData)

class UserPayload(val clientMutationId: String?, val node: UserNode?)
