package com.zimidy.api.layers.storage.entities.node.nodes

import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.node.UniquePropertyPart
import lombok.Getter
import lombok.Setter
import lombok.ToString
import org.neo4j.ogm.annotation.Relationship

@Getter
@Setter
@ToString(callSuper = true, exclude = arrayOf("user"))
class Account : Node() {

    @UniquePropertyPart("provider-user")
    private val providerId: String? = null
    @UniquePropertyPart("connection-key")
    private val providerUserId: String? = null
    private val profileUrl: String? = null
    private val displayName: String? = null
    private val imageUrl: String? = null
    private val accessToken: String? = null
    private val secret: String? = null
    private val refreshToken: String? = null
    private val expireTime: Long? = null
    @UniquePropertyPart("provider-user")
    @Relationship(type = RELATIONSHIP_USER, direction = Relationship.UNDIRECTED)
    private val user: User? = null

    companion object {

        const val RELATIONSHIP_USER = "ACCOUNT_USER"
    }
}
