// Relay Global Object Identification Specification: https://facebook.github.io/relay/graphql/objectidentification.htm

package com.zimidy.api.layers.api.graphql.resolvers.aspects.relay

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.zimidy.api.AccessibleByUsers
import com.zimidy.api.layers.api.graphql.resolvers.nodes.EventNode
import com.zimidy.api.layers.api.graphql.resolvers.nodes.UserNode
import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.Event
import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.storages.NodeStorage
import org.springframework.stereotype.Component

@Component
class RelayAspectQueryResolver(private val nodeStorage: NodeStorage) : GraphQLQueryResolver {

    @AccessibleByUsers
    fun node(id: NodeId): GraphQlNode? {
        val node = nodeStorage.get(id) ?: return null
        return when (node) {
            is User -> UserNode(node)
            is Event -> EventNode(node)
            else -> throw RuntimeException("Unexpected node type: ${node.javaClass.simpleName}")
        }
    }
}

// todo: Can we go without the parallel hierarchy of nodes for api layer?
abstract class GraphQlNode(node: Node) {
    val id: NodeId? = node.id
}
