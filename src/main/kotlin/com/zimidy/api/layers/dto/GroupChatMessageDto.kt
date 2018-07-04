package com.zimidy.api.layers.dto

import com.zimidy.api.layers.storage.entities.node.nodes.GroupChatMessage
import lombok.Getter
import lombok.Setter
import java.util.Date

@Getter
@Setter
class GroupChatMessageDto {
    var text: String? = null
    var sendDate: Date? = null
    var userName: String? = null
    var userId: Long? = null
    var id: Int = 0

    companion object {

        fun valueOf(groupChatMessage: GroupChatMessage): GroupChatMessageDto {
            val groupChatMessageDto = GroupChatMessageDto()
            groupChatMessageDto.text = groupChatMessage.text
            groupChatMessageDto.sendDate = groupChatMessage.sendDate
            groupChatMessageDto.userName = groupChatMessage.username
            groupChatMessageDto.userId = groupChatMessage.userId!!.toLong()
            groupChatMessageDto.id = Integer.parseInt(groupChatMessage.id!!.toString())
            return groupChatMessageDto
        }
    }
}
