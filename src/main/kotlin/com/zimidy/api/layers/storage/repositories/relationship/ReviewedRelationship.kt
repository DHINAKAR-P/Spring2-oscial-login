package com.zimidy.api.layers.storage.repositories.relationship

import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.entities.node.nodes.review.ReviewStatus
import com.zimidy.api.layers.storage.entities.node.nodes.review.ReviewStatusConverter
import com.zimidy.api.layers.storage.entities.relationship.Relationship
import org.neo4j.ogm.annotation.EndNode
import org.neo4j.ogm.annotation.StartNode
import lombok.Getter
import lombok.ToString
import lombok.Setter
import org.neo4j.ogm.annotation.RelationshipEntity
import org.neo4j.ogm.annotation.typeconversion.Convert

@RelationshipEntity(type = ReviewedRelationship.TYPE)
@Getter
@Setter
@ToString(callSuper = true, exclude = arrayOf("from", "to"))
class ReviewedRelationship : Relationship<User, User>() {

    @StartNode
    override var startNode: User? = null
    @EndNode
    override var endNode: User? = null
    var eventId: Long? = null
    var message: String? = null
    var rating = 0
    var creator: Boolean? = null
    var visible: Boolean? = null
    @Convert(ReviewStatusConverter::class)
    var reviewStatus: ReviewStatus? = null
    var reviewDate: Long? = null
    override val type: String
        get() = TYPE

    companion object {

        const val TYPE = "REVIEWED"
    }
}
