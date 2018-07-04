package com.zimidy.api.layers.dto

import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.review.ReviewStatus

class ReviewDto {
    var id: Long? = null
    var fromUserId: NodeId? = null
    var toUserId: NodeId? = null
    var eventId: Long? = null
    var creator: Boolean? = null
    var visible: Boolean? = null
    var rating: Int? = null
    var message: String? = null
    var reviewDate: Long? = null
    var reviewStatus: ReviewStatus? = null
}
