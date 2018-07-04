package com.zimidy.api.layers.mapper

import com.zimidy.api.layers.dto.ReviewDto
import com.zimidy.api.layers.storage.repositories.relationship.ReviewedRelationship

object ReviewMapper {

    fun map(review: ReviewedRelationship): ReviewDto {
        val reviewDto = ReviewDto()
        reviewDto.id = review.id
        reviewDto.fromUserId = review.startNode!!.id
        reviewDto.toUserId = review.endNode!!.id
        reviewDto.eventId = review.eventId
        reviewDto.message = review.message
        reviewDto.rating = review.rating
        reviewDto.creator = review.creator
        reviewDto.visible = review.visible
        reviewDto.reviewStatus = review.reviewStatus
        reviewDto.reviewDate = review.reviewDate
        return reviewDto
    }
}
