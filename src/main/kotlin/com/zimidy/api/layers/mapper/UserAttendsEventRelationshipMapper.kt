package com.zimidy.api.layers.mapper

import java.util.stream.Collectors
import com.zimidy.api.layers.storage.repositories.relationship.UserAttendsEventRelationship
import com.zimidy.api.layers.dto.UserAttendsEventRelationshipDto

object UserAttendsEventRelationshipMapper {

    fun map(rel: UserAttendsEventRelationship): UserAttendsEventRelationshipDto {
        val dto = UserAttendsEventRelationshipDto()
        dto.id = rel.id
        dto.review = rel.review
        dto.reviewDate = rel.reviewDate
        dto.creator = rel.creator
        dto.approvalStatus = rel.approvalStatus
        dto.event = rel.endNode!!.id
        dto.user = rel.startNode!!.id
        dto.userName = rel.startNode!!.buildFullName()
        dto.userImage = rel.startNode!!.defaultImageFileName
        dto.userPoints = rel.startNode!!.pointBalance
        return dto
    }

    fun map(rels: Set<UserAttendsEventRelationship>): Set<UserAttendsEventRelationshipDto> {
        return rels.stream().map<UserAttendsEventRelationshipDto>({ map(it) }).collect(Collectors.toSet())
    }
}
