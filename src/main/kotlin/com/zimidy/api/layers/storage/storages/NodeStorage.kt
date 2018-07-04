package com.zimidy.api.layers.storage.storages

import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.node.NodeId
import org.neo4j.ogm.session.Session
import org.springframework.stereotype.Component

@Component
class NodeStorage(private val session: Session) {
    fun get(id: NodeId): Node? = session.load(Node::class.java, id)
}
