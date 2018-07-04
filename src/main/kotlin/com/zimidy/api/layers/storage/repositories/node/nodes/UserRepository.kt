package com.zimidy.api.layers.storage.repositories.node.nodes

import com.plivo.helper.api.client.RestAPI
import com.plivo.helper.exception.PlivoException
import com.zimidy.api.configurations.security.SecurityConfig
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.CreateUserData
import com.zimidy.api.layers.storage.entities.node.nodes.UpdateUserData
import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.repositories.node.NodeRepository
import com.zimidy.api.layers.storage.repositories.node.NodeRepositoryCustomImpl
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.neo4j.ogm.session.Session
import org.springframework.core.env.Environment
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.io.BufferedReader
import java.io.InputStreamReader

interface UserRepository : NodeRepository<User>, UserRepositoryCustom {
    fun findByEmail(email: String): User?

    @Query(
        "MATCH (u1:User)-[r:OTHERUSERS]-(u2:User)" +
            "WHERE u1.zimId = {0} AND id(u2)= {1} AND (r.lowFriendState = 'ACCEPTED' AND r.highFriendState = 'ACCEPTED')" +
            " RETURN count(r) > 0"
    )
    fun isConnected(userId: NodeId, otherUserId: Long): Boolean

    @Query("MATCH (n:User) WHERE id(n)<> {userId} AND n.dfa=true RETURN n")
    fun getDfaUsers(@Param("userId") userId: NodeId): List<User>

    @Query("MATCH (n:User) WHERE id(n)<> {userId} AND n.dfa=true RETURN n")
    fun getDfaOtherUsers(@Param("userId") userId: NodeId): List<User>
}

interface UserRepositoryCustom {
    fun create(data: CreateUserData): User
    fun update(data: UpdateUserData): User
    fun plivoInvite(plivouser: String, plivoSignInUrl: String?, eventId: Long, userId: NodeId): String?
}

open class UserRepositoryImpl(
    session: Session,
    private val environment: Environment
) : NodeRepositoryCustomImpl<User>(session, User::class.java), UserRepositoryCustom {

    @Transactional
    override fun create(data: CreateUserData): User {
        val user = User(
            firstName = data.firstName,
            lastName = data.lastName,
            password = SecurityConfig.encodePassword(data.password),
            email = data.email,
            dfa = data.dfa,
            pointBalance = data.pointBalance,
            pointsPending = data.pointsPending,
            soundOn = data.soundOn,
            defaultImageFileName = data.defaultImageFileName,
            birthDate = data.birthDate,
            gender = data.gender,
            homeLocation = data.homeLocation,
            fuzzyHomeLocation = data.fuzzyHomeLocation,
            homeLocality = data.homeLocality,
            passwordVerificationCode = data.passwordVerificationCode,
            emailVerificationCode = data.emailVerificationCode,
            mobileVerificationCode = data.mobileVerificationCode,
            mobileTextAlerts = data.mobileTextAlerts,
            currentLocation = data.currentLocation,
            fuzzyCurrentLocation = data.fuzzyCurrentLocation,
            currentLocality = data.currentLocality,
            reviewable = data.reviewable,
            gcmToken = data.gcmToken,
            commOptOut = data.commOptOut,
            staff = data.staff,
            textEnabled = data.textEnabled,
            stripeID = data.stripeID,
            notificationsEnabled = data.notificationsEnabled,
            showUpcomingEvents = data.showUpcomingEvents,
            showRecentEvents = data.showRecentEvents,
            distanceUnit = data.distanceUnit,
            gcmRegistrationToken = data.gcmRegistrationToken,
            eventsCreated = data.eventsCreated,
            description = data.description,
            userAlerts = data.userAlerts,
            otherUserRels = data.otherUserRels,
            attendsEventRels = data.attendsEventRels
        )
        return create(user)
    }

    @Transactional
    override fun update(data: UpdateUserData): User {
        System.out.println("data while update  - > " + data.toString().toString())
        val user = getOrThrow(data.id)
        user.firstName = data.firstName!!
        user.lastName = data.lastName!!
        user.homeLocation = data.homeLocation
        // if (user.password != data.password) user.password = SecurityConfig.encodePassword(data.password)
        user.email = data.email
        user.dfa = data.dfa
        user.pointsPending = data.pointsPending
        user.pointBalance = data.pointBalance
        user.soundOn = data.soundOn
        user.defaultImageFileName = data.defaultImageFileName
        user.birthDate = data.birthDate
        user.gender = data.gender
        user.fuzzyHomeLocation = data.fuzzyHomeLocation
        user.homeLocality = data.homeLocality
        user.passwordVerificationCode = data.passwordVerificationCode
        user.emailVerificationCode = data.emailVerificationCode
        user.mobileVerificationCode = data.mobileVerificationCode
        user.mobileTextAlerts = data.mobileTextAlerts
        user.currentLocation = data.currentLocation
        user.fuzzyCurrentLocation = data.fuzzyCurrentLocation
        user.currentLocality = data.currentLocality
        user.reviewable = data.reviewable
        user.gcmToken = data.gcmToken
        user.commOptOut = data.commOptOut
        user.staff = data.staff
        user.textEnabled = data.textEnabled
        user.stripeID = data.stripeID
        user.notificationsEnabled = data.notificationsEnabled
        user.showUpcomingEvents = data.showUpcomingEvents
        user.showRecentEvents = data.showRecentEvents
        user.distanceUnit = data.distanceUnit
        user.gcmRegistrationToken = data.gcmRegistrationToken
        user.eventsCreated = data.eventsCreated
        user.description = data.description
        user.userAlerts = data.userAlerts
        user.otherUserRels = data.otherUserRels
        user.attendsEventRels = data.attendsEventRels
        return update(user)
    }

    override fun plivoInvite(plivouser: String, plivoSignInUrl: String?, eventId: Long, userId: NodeId): String? {
        var plivoSignInUrl = plivoSignInUrl
        if (plivoSignInUrl == null) {
            plivoSignInUrl = ""
        }
        val authId = environment.getProperty("sms_provider.plivo.auth_id")
        val authToken = environment.getProperty("sms_provider.plivo.auth_token")
        val api = RestAPI(authId, authToken, "v1")
        val parameters = LinkedHashMap<String, String>()
        parameters["src"] = environment.getProperty("sms_provider.plivo.phone_number")
        parameters["dst"] = plivouser
        val shortUrl = tinyUrl(plivoSignInUrl)
        val uservalue = getOrThrow(userId)
        var smsText = uservalue.firstName + " " + uservalue.lastName
        if (eventId.equals(0)) {
            smsText += " wants you to join them on Zimidy! Something fun is about to happen... $shortUrl"
        } else {
            smsText += " wants you to check out this event on Zimidy! They must have found something you'd like! $shortUrl"
        }
        parameters["text"] = smsText
        parameters["url"] = shortUrl // status sent here
        parameters["method"] = "GET" // The method used to call the url
        try {
            return api.sendMessage(parameters).error
        } catch (e: PlivoException) {
            return null
        }
        return null
    }

    fun tinyUrl(targetUrl: String): String {
        try {
            val httpClient = HttpClients.createDefault()
            val url = URIBuilder("http://tinyurl.com/api-create.php")
                .setParameter("url", targetUrl)
                .build()
            val httpGet = HttpGet(url)
            val response = httpClient.execute(httpGet)
            val reader = BufferedReader(InputStreamReader(response.entity.content))
            var inputLine: String
            val content = StringBuilder()
            inputLine = reader.readLine()
            content.append(inputLine)
            reader.close()
            httpClient.close()
            return content.toString()
        } catch (e: Exception) {
            println("Exception : $e")
        }

        return "error"
    }
}
