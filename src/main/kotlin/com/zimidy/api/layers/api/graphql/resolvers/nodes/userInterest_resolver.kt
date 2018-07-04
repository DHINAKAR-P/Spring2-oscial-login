package com.zimidy.api.layers.api.graphql.resolvers.nodes

import com.zimidy.api.AccessibleByAnyone
import com.zimidy.api.AccessibleByUsers
import com.zimidy.api.layers.api.graphql.resolvers.AbstractQueryResolver
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.ConnectionFactory
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.Cursor
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.GraphQlNode
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.Edge
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.PageInfo
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.Connection
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.UserInterest
import com.zimidy.api.layers.storage.repositories.node.nodes.UserInterestRepository
import org.springframework.stereotype.Component
import java.util.Collections

@Component
class UserInterestNodeQueryResolver(
    private val userInterestRepository: UserInterestRepository
) : AbstractQueryResolver() {

                @AccessibleByUsers
				fun userInterest(id: NodeId): UserInterestNode? {
								val userInterest = userInterestRepository.get(id)
								return if (userInterest != null) UserInterestNode(userInterest) else null
				}

				@AccessibleByAnyone
				fun userInterests(before: Cursor?, after: Cursor?, first: Int?, last: Int?): UserInterestConnection {
								val alltags = userInterestRepository.all().map { userInterest -> UserInterestNode(userInterest) }
								return UserInterestConnectionFactory(alltags).create(after = after, before = before, first = first, last = last)
				}
}

data class UserInterestNode(private val userInterest: UserInterest) : GraphQlNode(userInterest) {
				val name: String? = userInterest.name
}

class UserInterestEdge(override val node: UserInterestNode, override val cursor: Cursor) : Edge {
				override fun toString() = asString()
}

class UserInterestConnection(edges: List<UserInterestEdge>, override val pageInfo: PageInfo) : Connection {
				override val edges: List<UserInterestEdge> = Collections.unmodifiableList<UserInterestEdge>(edges)
				override fun toString() = asString()
}

class UserInterestConnectionFactory(alltags: List<UserInterestNode>) : ConnectionFactory<UserInterestNode, UserInterestEdge, UserInterestConnection>(alltags) {
				override fun createEdge(node: UserInterestNode, cursor: Cursor) = UserInterestEdge(node, cursor)
				override fun createConnection(edges: List<UserInterestEdge>, pageInfo: PageInfo) = UserInterestConnection(edges, pageInfo)
}
