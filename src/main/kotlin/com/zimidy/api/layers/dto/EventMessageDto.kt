package com.zimidy.api.layers.dto

import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.User
import java.io.Serializable
import java.util.Date

class EventMessageDto : Serializable {
    var id: NodeId? = null
    var text: String? = null
    var sendDate: Date? = null
    var event: Long? = null
    var user: User? = null
}
