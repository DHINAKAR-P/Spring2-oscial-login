package com.zimidy.api.layers.mapper

import com.zimidy.api.layers.storage.entities.node.Node
import lombok.Getter
import lombok.Setter
import lombok.ToString
import java.util.Date

@Getter
@Setter
@ToString(callSuper = true)
class UserAlert : Node() {
    var alertType: AlertType? = null
    var status = AlertStatus.NEW
    var creationDate = Date()
    var originatorId: Long? = null // The user that invited the current user, or such
    var targetId: Long? = null // Target node identifier (for example, Event id if AlertType EVENT_INVITE)
    var originatorName: String? = null
    // todo: originatorProfileImageUrl field is often left uninitialized
    // we need to ensure its initialization (and we can, because we have originator's id and can read its image url)
    // uninitialized field causes issue on client side: GET http://localhost:8080/page/null 404 (Not Found)
    var originatorProfileImageUrl: String? = null

    enum class AlertType {
        CHATINVITE, FRIENDINVITE, EVENT_INVITE, JOINED_OPENEVENT,
        APPROVAL_REQEST, JOIN_DENIED, PAYMENT_REQUEST,
        PAYMENT_COMPLETED, EVENT_FULL, ACCEPT_EVENT_JOIN, DENY_EVENT_JOIN,
        EVENT_COMPLETE, EVENT_CANCEL, JOIN_APPROVED
    }

    enum class AlertStatus {
        NEW, VIEWED, HISTORY // TODO: Deleted also?
    }
}
