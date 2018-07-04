package com.zimidy.api.layers.mapper

import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.node.nodes.User
import lombok.Getter
import lombok.Setter
import lombok.ToString
import java.util.Date

@Getter
@Setter
@ToString(callSuper = true, exclude = arrayOf("user"))
class EventMessage : Node() {
    var text: String? = null
    var sendDate: Date? = null
    var user: User? = null
}
