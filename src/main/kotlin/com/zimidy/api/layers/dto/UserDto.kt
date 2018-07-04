package com.zimidy.api.layers.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.plivo.helper.api.response.account.Account
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.Location
import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.repositories.relationship.UserAttendsEventRelationship
import java.math.BigDecimal
import lombok.Getter
import lombok.EqualsAndHashCode
import lombok.Setter
import java.util.Date

@Getter
@Setter
@EqualsAndHashCode(of = arrayOf("id"))
open class UserDto {

    var id: NodeId? = null
    var password: String? = null
    var mobileNumber: String? = null
    var lastPasswordUpdatedInDays: Long = 0
    var lastPasswordUpdated: Long? = null
    var lastEmailUpdated: Long? = null
    var lastMobileNumberUpdated: Long? = null
    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var description: String? = null
    var interests: String? = null
    var birthDate: Long? = null
    var gender: User.Gender? = null
    var sound: Boolean? = null
    var location: Location? = null
    var homeLocation: String? = null
    var fuzzyHomeLocation: String? = null
    var homeLocality: String? = null
    var homeLocationLat: BigDecimal? = null
    var homeLocationLon: BigDecimal? = null
    var currentLocation: String? = null
    var fuzzyCurrentLocation: String? = null
    var currentLocality: String? = null
    var currentLocationLat: Double? = null
    var currentLocationLon: Double? = null
    var emailVerificationCode: String? = null
    var emailVerificationCodeExpiry: Date? = null
    var mobileVerificationCode: String? = null
    var mobileVerificationExpiry: Date? = null
    var mobileTextAlerts: Boolean? = null
    var dfa: Boolean? = false
    var defaultImageFileName: String? = null
    var groups = HashSet<String>()
    var zipCode: Int? = null
    var joinDate: Date? = null
    var reviewable: Boolean? = null
    var pointBalance: Long? = null
    var pointsPending: Long? = null
    var attendeeRating: Double? = null
    var organizerRating: Double? = null
    var gcmToken: String? = null
    var commOptOut: Boolean? = null
    var staff: Boolean = false
    var stripeID: String? = null
    // var accountStatus: AccountStatus? = null
    var accountSet = HashSet<Account>()
    var userSettings = UserSettingsDto()
    var events: Iterable<UserAttendsEventRelationship>? = null

    @JsonProperty("name")
    fun buildFullName(): String {
        return "$firstName $lastName"
    }
}
