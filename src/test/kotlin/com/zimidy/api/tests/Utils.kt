package com.zimidy.api.tests

import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.node.NodeId
import java.util.UUID

fun nodeId(): NodeId = UUID.randomUUID().toString()
fun <T : Node> withId(node: T): T {
    node.id = nodeId()
    return node
}
