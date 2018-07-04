package com.zimidy.api.layers.mapper

import java.util.ArrayList
import com.zimidy.api.layers.dto.UserHasChatDto
import com.zimidy.api.layers.storage.entities.node.nodes.User

object UserHasChatMapper {
    fun map(user: User): UserHasChatDto {
        val dto = UserHasChatDto()
        dto.id = user.id
        dto.firstName = user.firstName
        dto.lastName = user.lastName
        dto.email = user.email
        dto.password = user.password // TODO: rename dto.password to dto.passwordHash
        dto.dfa = user.dfa
        dto.birthDate = user.birthDate
        dto.defaultImageFileName = user.defaultImageFileName
        dto.gender = user.gender
        // dto.interests = user.interestsAsString
        // dto.attendeeRating = user.attendeeRating
        return dto
    }

    fun map(users: List<User>): List<UserHasChatDto> {
        val dtos = ArrayList<UserHasChatDto>()
        for (user in users) {
            dtos.add(map(user))
        }
        return dtos
    }
}
