package com.zimidy.api.layers.storage.entities.node.nodes

import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.node.NodeId
import lombok.Getter
import lombok.Setter
import lombok.ToString
import java.util.Date

@Getter
@Setter
@ToString(callSuper = true)
data class GroupChatMessage(
    var text: String? = null,
    var sendDate: Date? = null,
    var userId: NodeId? = null,
    var username: String? = null
) : Node()
