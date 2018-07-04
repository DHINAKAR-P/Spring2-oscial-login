package com.zimidy.api.layers.storage.entities.node.nodes

import com.zimidy.api.layers.enumClass.AccountStatus
import com.zimidy.api.layers.mapper.Account
import com.zimidy.api.layers.mapper.UserAlert
import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.relationship.relationships.CommonAttendanceRelationship
import com.zimidy.api.layers.storage.entities.relationship.relationships.OtherUserRelationship
import com.zimidy.api.layers.storage.repositories.relationship.UserAttendsEventRelationship
import org.neo4j.ogm.annotation.Index
import org.neo4j.ogm.annotation.Relationship
import java.util.ArrayList
import java.util.HashSet
import javax.annotation.Nonnull

data class User(
    var firstName: String = "",
    var lastName: String = "",
    @Index(unique = true)
    var email: String? = null,
    var password: String? = null,
    var dfa: Boolean? = null,
    var pointBalance: Long? = 0,
    var pointsPending: Long? = 0,
    var soundOn: Boolean? = null,
    var defaultImageFileName: String? = null,
    var gender: Gender? = null,
    var homeLocation: String? = null, // Where this user lives or spends most of their time
    var fuzzyHomeLocation: String? = null, // Approximate location of the user, shown to other users
    var homeLocality: String? = null, // City or other identifier of the location
    var passwordVerificationCode: String? = null,
    var emailVerificationCode: String? = null,
    var mobileVerificationCode: String? = null,
    var mobileTextAlerts: Boolean? = null,
    var currentLocation: String? = null, // Where this user is currently
    var fuzzyCurrentLocation: String? = null, // Approximate location of the user, shown to other users
    var currentLocality: String? = null, // City or other identifier of the location
    var birthDate: Long? = null,
    var reviewable: Boolean? = null,
    var gcmToken: String? = null,
    var commOptOut: Boolean? = null,
    var staff: Boolean = false,
    var textEnabled: Boolean = false,
    var stripeID: String? = null,
    var notificationsEnabled: Boolean = false,
    var showUpcomingEvents: Boolean = false,
    var showRecentEvents: Boolean = false,
    var distanceUnit: String? = null,
    var gcmRegistrationToken: String? = null,
    var eventsCreated: Long = 0, // Number of events, created by this user
    var description: String? = null,
    @Nonnull
    var accountStatus: AccountStatus = AccountStatus.PENDING,
    var roles: MutableList<String> = mutableListOf("ROLE_USER"),
    @Relationship(type = Event.CREATOR, direction = Relationship.INCOMING)
    var events: List<Event> = ArrayList(),
    @Relationship(type = OtherUserRelationship.TYPE, direction = Relationship.DIRECTION)
    var otherUserRels: Set<OtherUserRelationship> = HashSet<OtherUserRelationship>(),
    @Relationship(type = UserAttendsEventRelationship.TYPE)
    var attendsEventRels: Set<UserAttendsEventRelationship> = HashSet<UserAttendsEventRelationship>(),
    @Relationship(type = RELATIONSHIP_ALERTS)
    var userAlerts: Set<UserAlert> = HashSet<UserAlert>(),
    @Relationship(type = CommonAttendanceRelationship.TYPE)
    var commonRels: Set<CommonAttendanceRelationship> = HashSet<CommonAttendanceRelationship>(),
    @Relationship(type = Account.RELATIONSHIP_USER, direction = Relationship.INCOMING)
    var accounts: Set<Account> = HashSet<Account>()
) : Node() {

    companion object {
        const val RELATIONSHIP_ALERTS = "ALERTS"
        const val RELATIONSHIP_INTERESTED_IN = "INTERESTED_IN"
    }

    enum class Gender {
        MALE, FEMALE, OTHER, PREFERNOTTOANSWER
    }

    fun buildFullName(): String {
        return "$firstName $lastName"
    }
}

data class CreateUserData(
    var firstName: String,
    var lastName: String,
    var email: String?,
    var password: String?,
    var dfa: Boolean,
    var pointBalance: Long?,
    var pointsPending: Long?,
    var soundOn: Boolean?,
    var defaultImageFileName: String?,
    var birthDate: Long?,
    var gender: User.Gender?,
    var homeLocation: String?,
    var fuzzyHomeLocation: String?,
    var homeLocality: String?,
    var passwordVerificationCode: String?,
    var emailVerificationCode: String?,
    var mobileVerificationCode: String?,
    var mobileTextAlerts: Boolean?,
    var currentLocation: String?,
    var fuzzyCurrentLocation: String?,
    var currentLocality: String?,
    var reviewable: Boolean?,
    var gcmToken: String?,
    var commOptOut: Boolean?,
    var staff: Boolean = false,
    var textEnabled: Boolean = false,
    var stripeID: String?,
    var notificationsEnabled: Boolean = false,
    var showUpcomingEvents: Boolean = false,
    var showRecentEvents: Boolean = false,
    var distanceUnit: String?,
    var gcmRegistrationToken: String?,
    var eventsCreated: Long,
    var description: String? = null,
    var userAlerts: Set<UserAlert> = HashSet<UserAlert>(),
    var otherUserRels: Set<OtherUserRelationship> = HashSet<OtherUserRelationship>(),
    var attendsEventRels: Set<UserAttendsEventRelationship> = HashSet<UserAttendsEventRelationship>()
)

data class UpdateUserData(
    var id: NodeId,
    var firstName: String?,
    var lastName: String?,
    var email: String?,
    var dfa: Boolean,
    var pointBalance: Long? = 0,
    var pointsPending: Long? = 0,
    var soundOn: Boolean?,
    var defaultImageFileName: String?,
    var birthDate: Long?,
    var gender: User.Gender?,
    var homeLocation: String?,
    var fuzzyHomeLocation: String?,
    var homeLocality: String?,
    var passwordVerificationCode: String?,
    var emailVerificationCode: String?,
    var mobileVerificationCode: String?,
    var mobileTextAlerts: Boolean?,
    var currentLocation: String?,
    var fuzzyCurrentLocation: String?,
    var currentLocality: String?,
    var reviewable: Boolean?,
    var gcmToken: String?,
    var commOptOut: Boolean?,
    var staff: Boolean = false,
    var textEnabled: Boolean = false,
    var stripeID: String?,
    var notificationsEnabled: Boolean = false,
    var showUpcomingEvents: Boolean = false,
    var showRecentEvents: Boolean = false,
    var distanceUnit: String?,
    var gcmRegistrationToken: String?,
    var eventsCreated: Long,
    var description: String? = null,
    var userAlerts: Set<UserAlert> = HashSet<UserAlert>(),
    var otherUserRels: Set<OtherUserRelationship> = HashSet<OtherUserRelationship>(),
    var attendsEventRels: Set<UserAttendsEventRelationship> = HashSet<UserAttendsEventRelationship>()
)
