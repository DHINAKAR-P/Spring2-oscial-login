package com.zimidy.api.layers.dto

import com.zimidy.api.layers.enumClass.UserRelationsType

class UserHasChatDto : UserDto() {

    var hasOpenChat = false

    var connected = false

    var hasRequestSent = false

    var userRelationsType: UserRelationsType? = null

    var distance: Double? = null

    var connectStatus: Map<String, Any>? = null
}
