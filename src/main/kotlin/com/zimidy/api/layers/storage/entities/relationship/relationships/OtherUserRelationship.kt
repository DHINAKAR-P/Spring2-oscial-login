package com.zimidy.api.layers.storage.entities.relationship.relationships

import com.zimidy.api.layers.dto.ChatMessageDto
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.entities.relationship.Relationship
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.NonNull
import lombok.Setter
import lombok.ToString
import org.apache.commons.lang3.BooleanUtils
import org.neo4j.ogm.annotation.EndNode
import org.neo4j.ogm.annotation.RelationshipEntity
import org.neo4j.ogm.annotation.StartNode
import org.springframework.util.Assert
import java.util.ArrayList
import java.util.Date
import java.util.HashSet
import java.util.LinkedList

@RelationshipEntity(type = OtherUserRelationship.TYPE)
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = arrayOf("lowIdUser", "highIdUser"))
class OtherUserRelationship : Relationship<User, User>() {

    @StartNode
    var lowIdUser: User? = null
    @EndNode
    var highIdUser: User? = null
    var lowFriendState = FriendState.NONE
    var highFriendState = FriendState.NONE
    var lowChatState = ChatState.NONE
    var highChatState = ChatState.NONE
    var chatRequestDate: Date? = null
    var friendRequestDate: Date? = null
    val lowNumStars: Int? = null // Num stars given by the low id person to the high id person
    val lowReview: String? = null // Review given by the low id person to the high id person
    val lowReviewDate: Date? = null // Review date of review given by the low id person to the high id person
    val highNumStars: Int? = null // Num stars given by the high id person to the low id person
    var highReview: String? = null // Review given by the high id person to the low id person
    var highReviewDate: Date? = null // Date of review given by the high id person to the low id person
    var lowIdUserTags: Array<String>? = null
    var highIdUserTags: Array<String>? = null
    var highIdUserGroups = HashSet<String>()
    var lowIdUserGroups = HashSet<String>()
    var rating: String? = null
    var referenceMessage: String? = null

    /**
     * Data related to chat messages:
     * todo: probably worth to encapsulate it?
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    var chatMessageSenderIds = LinkedList<Long>()
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    var chatMessageRecipientIds = LinkedList<Long>()
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    var chatMessageTexts = LinkedList<String>()
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    var chatMessageUnreadFlags = LinkedList<Boolean>()
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    var chatMessageTimestamps = LinkedList<String>()

    override var startNode: User? = lowIdUser
    override var endNode: User? = highIdUser

    override val type: String
        @NonNull
        get() = TYPE

    fun OtherUserRelationship(@NonNull user: User, @NonNull anotherUser: User) {
        Assert.notNull(user.id)
        Assert.notNull(anotherUser.id)
        if (user.id.equals(anotherUser.id)) { // need to change
            setHighIdUser_data(user)
            setHighIdUser_data(anotherUser)
        } else {
            setHighIdUser_data(anotherUser)
            setLowIdUser_data(user)
        }
    }

    fun setLowIdUser_data(lowIdUser: User) {
        this.startNode = lowIdUser
        lowIdUser.otherUserRels.plus(this)
    }

    fun setHighIdUser_data(highIdUser: User) {
        this.endNode = highIdUser
        highIdUser.otherUserRels.plus(this)
    }

    fun chatMessages(): List<ChatMessageDto> {
        val messageCount = chatMessageSenderIds.size
        Assert.isTrue(messageCount == chatMessageRecipientIds.size)
        Assert.isTrue(messageCount == chatMessageTexts.size)
        Assert.isTrue(messageCount == chatMessageTimestamps.size)
        val chatMessages = ArrayList<ChatMessageDto>()
        for (index in 0 until messageCount) {
            val chatMessage = ChatMessageDto()
            chatMessage.senderId = chatMessageSenderIds[index]
            chatMessage.recipientId = chatMessageRecipientIds[index]
            chatMessage.message = chatMessageTexts[index]
            chatMessage.unread = BooleanUtils.isTrue(chatMessageUnreadFlags[index])
            chatMessage.timestamp = chatMessageTimestamps[index]
            chatMessages.add(chatMessage)
        }
        return chatMessages
    }

    fun addChatMessage(chatMessage: ChatMessageDto) {
        chatMessageSenderIds.add(chatMessage.senderId!!)
        chatMessageRecipientIds.add(chatMessage.recipientId!!)
        chatMessageTexts.add(chatMessage.message!!)
        chatMessageUnreadFlags.add(chatMessage.unread)
        chatMessageTimestamps.add(chatMessage.timestamp!!)
    }

    fun getOtherUser(thisUserId: NodeId): User? {
        checkUserIsPartOfRelationship(thisUserId)
        return if (thisUserId == startNode!!.id) endNode else startNode
    }

    fun getOtherUserGroups(thisUserId: NodeId): Set<String> {
        checkUserIsPartOfRelationship(thisUserId)
        return if (thisUserId == startNode!!.id) highIdUserGroups else lowIdUserGroups
    }

    fun chatAccepted(): Boolean {
        return lowChatState == ChatState.ACCEPTED && highChatState == ChatState.ACCEPTED
    }

    fun acceptChat() {
        highChatState = ChatState.ACCEPTED
        lowChatState = highChatState
    }

    fun friendAccepted(): Boolean {
        return lowFriendState == FriendState.ACCEPTED && highFriendState == FriendState.ACCEPTED
    }

    fun acceptFriend() {
        highFriendState = FriendState.ACCEPTED
        lowFriendState = highFriendState
    }

    fun chatRequest(userId: NodeId, otherUser: NodeId): Boolean {
        val fromUser = outgoingChatRequest(userId) && incomingChatRequest(otherUser)
        val toUser = incomingChatRequest(userId) && outgoingChatRequest(otherUser)
        return fromUser || toUser
    }

    fun friendRequest(userId: NodeId, otherUser: NodeId): Boolean {
        val fromUser = outgoingFriendRequest(userId) && incomingFriendRequest(otherUser)
        val toUser = incomingFriendRequest(userId) && outgoingFriendRequest(otherUser)
        return fromUser || toUser
    }

    fun incomingChatRequest(userId: NodeId): Boolean {
        return if (startNode!!.id === userId) {
            lowChatState == ChatState.NONE && highChatState == ChatState.ACCEPTED
        } else {
            lowChatState == ChatState.ACCEPTED && highChatState == ChatState.NONE
        }
    }

    fun outgoingChatRequest(userId: NodeId): Boolean {
        return if (startNode!!.id === userId) {
            lowChatState == ChatState.ACCEPTED && highChatState == ChatState.NONE
        } else {
            lowChatState == ChatState.NONE && highChatState == ChatState.ACCEPTED
        }
    }

    fun incomingFriendRequest(userId: NodeId): Boolean {
        return if (startNode!!.id === userId) {
            lowFriendState == FriendState.NONE && highFriendState == FriendState.ACCEPTED
        } else {
            lowFriendState == FriendState.ACCEPTED && highFriendState == FriendState.NONE
        }
    }

    fun outgoingFriendRequest(userId: NodeId): Boolean {
        return if (startNode!!.id === userId) {
            lowFriendState == FriendState.ACCEPTED && highFriendState == FriendState.NONE
        } else {
            lowFriendState == FriendState.NONE && highFriendState == FriendState.ACCEPTED
        }
    }

    /*
    * some fields setLowChatState, setHighChatState, setLowFriendState, setHighFriendState
    */

    private fun updateChatRequestDate() {
        if (chatRequestDate == null) {
            chatRequestDate = Date()
        }
    }

    private fun updateFriendRequestDate() {
        if (friendRequestDate == null) {
            friendRequestDate = Date()
        }
    }

    private fun checkUserIsPartOfRelationship(userId: NodeId) {
        if (userId != startNode!!.id && userId != endNode!!.id) {
            val format = "User with id %s is not a part of this relationship."
            throw RuntimeException(String.format(format, userId))
        }
    }

    enum class FriendState {
        NONE, ACCEPTED, DECLINED
    }

    enum class ChatState {
        /**
         * User has not made actions toward the other user, or previously only declined chats.
         * In this case, we will display alerts
         */
        NONE,
        /**
         * User wants to chat, and has allowed it
         */
        ACCEPTED,
        /**
         * User does not want to chat, but could be invited again later
         */
        DECLINED,
        /**
         * User does not want to chat with the user, and cannot be invited
         */
        BLOCKED
    }

    companion object {

        const val TYPE = "OTHERUSERS"
    }
}
