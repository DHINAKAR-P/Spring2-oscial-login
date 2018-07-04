package com.zimidy.api.layers.storage.entities.relationship

import com.zimidy.api.layers.storage.entities.Entity
import com.zimidy.api.layers.storage.entities.node.Node
import lombok.NonNull

typealias RelationshipId = Long

abstract class Relationship<START_NODE : Node, END_NODE : Node> : Entity<RelationshipId>() {

    @get:NonNull

    abstract val type: String

    abstract var startNode: START_NODE?

    abstract var endNode: END_NODE?
}
