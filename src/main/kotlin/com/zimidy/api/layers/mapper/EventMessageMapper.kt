package com.zimidy.api.layers.mapper

import java.util.HashSet
import com.zimidy.api.layers.dto.EventMessageDto

object EventMessageMapper {
    fun map(eventMessage: EventMessage): EventMessageDto {
        val dto = EventMessageDto()
        dto.id = eventMessage.id
        dto.text = eventMessage.text
        dto.sendDate = eventMessage.sendDate
        return dto
    }

    fun map(eventMessageList: Set<EventMessage>): Set<EventMessageDto> {
        val dtos = HashSet<EventMessageDto>()
        for (eventMessage in eventMessageList) {
            dtos.add(map(eventMessage))
        }
        return dtos
    }
}
