package com.zimidy.api.layers.storage.repositories.node.nodes

import com.zimidy.api.configurations.security.SessionContextHolder
import com.zimidy.api.layers.dto.GroupChatMessageDto
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.Event
import com.zimidy.api.layers.storage.entities.node.nodes.GroupChat
import com.zimidy.api.layers.storage.entities.node.nodes.GroupChatMessage
import com.zimidy.api.layers.storage.entities.relationship.relationships.GroupChatRelationship
import com.zimidy.api.layers.storage.repositories.node.NodeRepository
import com.zimidy.api.layers.storage.repositories.node.NodeRepositoryCustomImpl
import org.neo4j.ogm.session.Session
import org.springframework.transaction.annotation.Transactional
import java.util.ArrayList
import java.util.Date

interface GroupChatRepository : NodeRepository<GroupChat>, GroupChatRepositoryCustom

interface GroupChatRepositoryCustom {
    fun update(data: String): GroupChat
    fun display(data: String): String
    fun createAndSaveGroupChat(event: Event): GroupChat
    fun saveGroupChatMessage(userId: NodeId, eventId: NodeId, messageText: String): Boolean
    fun getAllMessages(eventId: NodeId?): List<GroupChatMessageDto>
}

open class GroupChatRepositoryImpl(
    private val session: Session,
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val sessionContextHolder: SessionContextHolder
) : NodeRepositoryCustomImpl<GroupChat>(session, GroupChat::class.java), GroupChatRepositoryCustom {

    companion object {
        private val noParameters = emptyMap<String, String>()
    }

    override fun display(data: String): String {
        return data.capitalize()
    }

    @Transactional
    override fun update(data: String): GroupChat {
        val user = getOrThrow(data)
        return update(user)
    }

    @Transactional
    override fun createAndSaveGroupChat(event: Event): GroupChat {
        val groupChat = GroupChat()
        var groupChatRel = event.groupChatRel
        if (groupChatRel == null) {
            groupChatRel = GroupChatRelationship()
            groupChatRel!!.event = event
            groupChatRel!!.groupChat = groupChat
        }
        groupChat.addGroupChatRel(groupChatRel!!)
        return save(groupChat)
    }

    @Transactional
    override fun saveGroupChatMessage(userId: NodeId, eventId: NodeId, messageText: String): Boolean {
        val event = eventRepository.getOrThrow(eventId)
        val user = userRepository.getOrThrow(userId)
        val groupChat: GroupChat
        if (event.groupChatRel == null) {
            groupChat = createAndSaveGroupChat(event)
        } else {
            groupChat = event.groupChatRel!!.groupChat!!
        }
        val groupChatMessage = GroupChatMessage()
        groupChatMessage.sendDate = Date()
        groupChatMessage.text = messageText
        groupChatMessage.userId = userId
        groupChatMessage.username = user.buildFullName()
        groupChat.chatMessages.plus(groupChatMessage)
        save(groupChat)
        return true
    }

    @Transactional

    override fun getAllMessages(eventId: NodeId?): List<GroupChatMessageDto> {
        val event = eventRepository.get(eventId!!)
        val groupMessage = getOrThrow(event?.groupChatRel?.groupChat?.id!!)
        val messages = groupMessage.chatMessages
        val groupChatMessageDtos = ArrayList<GroupChatMessageDto>()
        for (groupChatMessage in messages) {
            groupChatMessageDtos.add(GroupChatMessageDto.valueOf(groupChatMessage))
        }
        // if u want sort here
        return groupChatMessageDtos
    }
}
