package com.zimidy.api.layers.storage.entities.node

import com.zimidy.api.layers.storage.entities.Entity
import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.id.UuidStrategy

typealias NodeId = String

@NodeEntity(label = "Entity") // todo: use "Node" instead (default)
abstract class Node(
    @Property(name = "zimId") // todo: use "id" instead
    @Id @GeneratedValue(strategy = UuidStrategy::class)
    final override var id: NodeId? = null
) : Entity<NodeId>(), NodeData

interface NodeData {
    val id: NodeId?
}
