package com.zimidy.api.layers.dto

import org.springframework.data.neo4j.annotation.QueryResult
import lombok.Getter
import lombok.ToString
import lombok.Setter

@Getter
@Setter
@ToString(callSuper = true)
@QueryResult
class ReviewRelationshipDto {
    var id: Long? = null
    var fromUserId: Long? = null
    var toUserId: Long? = null
    var toFirstName: String? = null
    var fromFirstName: String? = null
    var message: String? = null
    var rating: Int? = null
    var eventId: Long? = null
}
