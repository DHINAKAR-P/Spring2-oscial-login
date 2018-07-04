package com.zimidy.api.layers.api.graphql.resolvers.nodes

import com.zimidy.api.AccessibleByAnyone
import com.zimidy.api.AccessibleByUsers
import com.zimidy.api.configurations.security.SessionContextHolder
import com.zimidy.api.layers.api.graphql.resolvers.AbstractQueryResolver
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.ConnectionFactory
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.Cursor
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.GraphQlNode
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.Edge
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.PageInfo
import com.zimidy.api.layers.api.graphql.resolvers.aspects.relay.Connection
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.Tag
import com.zimidy.api.layers.storage.repositories.node.nodes.TagRepository
import org.springframework.stereotype.Component
import java.util.Collections

@Component
class TagNodeQueryResolver(
    private val repository: TagRepository,
    private val sessionContextHolder: SessionContextHolder
) : AbstractQueryResolver() {

    @AccessibleByUsers
    fun tag(id: NodeId): TagNode? {
        val tag = repository.get(id)
        return if (tag != null) TagNode(tag) else null
    }

    @AccessibleByAnyone
    fun tags(before: Cursor?, after: Cursor?, first: Int?, last: Int?): TagConnection {
        val alltags = repository.all().map { tag -> TagNode(tag) }
        return TagConnectionFactory(alltags).create(after = after, before = before, first = first, last = last)
    }
}

data class TagNode(private val tag: Tag) : GraphQlNode(tag) {
    val name: String? = tag.name
}

class TagEdge(override val node: TagNode, override val cursor: Cursor) : Edge {
    override fun toString() = asString()
}

class TagConnection(edges: List<TagEdge>, override val pageInfo: PageInfo) : Connection {
    override val edges: List<TagEdge> = Collections.unmodifiableList<TagEdge>(edges)
    override fun toString() = asString()
}

class TagConnectionFactory(alltags: List<TagNode>) : ConnectionFactory<TagNode, TagEdge, TagConnection>(alltags) {
    override fun createEdge(node: TagNode, cursor: Cursor) = TagEdge(node, cursor)
    override fun createConnection(edges: List<TagEdge>, pageInfo: PageInfo) = TagConnection(edges, pageInfo)
}
