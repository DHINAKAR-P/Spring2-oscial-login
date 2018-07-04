package com.zimidy.api.layers.storage.entities.relationship.relationships

interface OtherUserRelationshipRepositoryCustom {

    fun findOneChatRelationship(userId: Long, otherUserId: Long): OtherUserRelationship

    fun findOneFriendRelationship(userId: Long, otherUserId: Long): OtherUserRelationship

    fun findOneChatRequest(userId: Long, otherUserId: Long): OtherUserRelationship

    fun findOneFriendRequest(userId: Long, otherUserId: Long): OtherUserRelationship

    fun findOneIncomingChatRequest(userId: Long, otherUserId: Long): OtherUserRelationship

    fun findOneIncomingFriendRequest(userId: Long, otherUserId: Long): OtherUserRelationship

    fun findOneOutgoingChatRequest(userId: Long, otherUserId: Long): OtherUserRelationship

    fun findOneOutgoingFriendRequest(userId: Long, otherUserId: Long): OtherUserRelationship

    fun findOneAcceptedChat(userId: Long, otherUserId: Long): OtherUserRelationship

    fun findOneAcceptedFriend(userId: Long, otherUserId: Long): OtherUserRelationship

    fun findAcceptedChatRelationships(userId: Long): List<OtherUserRelationship>

    fun findAcceptedFriendRelationships(userId: Long): List<OtherUserRelationship>

    fun findIncomingChatRequests(userId: Long): List<OtherUserRelationship>

    fun findIncomingFriendRequests(userId: Long): List<OtherUserRelationship>
}
