package com.zimidy.api.layers.mapper

import com.zimidy.api.UniquePropertyPart
import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.node.nodes.User
import lombok.Getter
import lombok.Setter
import lombok.ToString
import org.neo4j.ogm.annotation.Relationship

@Getter
@Setter
@ToString(callSuper = true, exclude = arrayOf("user"))
class Account : Node() {
    @UniquePropertyPart("provider-user")
    // @UniquePropertyPart("connection-key")
    var providerId: String? = null
    @UniquePropertyPart("connection-key")
    var providerUserId: String? = null
    var profileUrl: String? = null
    var displayName: String? = null
    var imageUrl: String? = null
    var accessToken: String? = null
    var secret: String? = null
    var refreshToken: String? = null
    var expireTime: Long? = null
    @UniquePropertyPart("provider-user")
    @Relationship(type = RELATIONSHIP_USER, direction = Relationship.UNDIRECTED)
    var user: User? = null

    companion object {

        const val RELATIONSHIP_USER = "ACCOUNT_USER"
    }
}
