package com.zimidy.api.layers.dto

class ChatMessageDto {

    var senderId: Long? = null
    var recipientId: Long? = null
    var message: String? = null
    var unread: Boolean = false
    var timestamp: String? = null
}
