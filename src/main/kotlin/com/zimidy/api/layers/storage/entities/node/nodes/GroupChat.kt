package com.zimidy.api.layers.storage.entities.node.nodes

import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.relationship.relationships.GroupChatRelationship
import lombok.Getter
import lombok.Setter
import lombok.ToString
import org.neo4j.ogm.annotation.Relationship
import java.util.HashSet
import java.util.function.Consumer

@Getter
@Setter
@ToString(callSuper = true, exclude = arrayOf("chatMessages"))
data class GroupChat(
    @Relationship
    var chatMessages: Set<GroupChatMessage> = HashSet<GroupChatMessage>(),
    @Relationship(type = GroupChatRelationship.TYPE, direction = Relationship.INCOMING)
    var groupChatRels: MutableSet<GroupChatRelationship> = HashSet<GroupChatRelationship>()

) : Node() {

    fun setGroupChatRels_data(groupChatRels: MutableSet<GroupChatRelationship>) {
        this.groupChatRels = groupChatRels
        groupChatRels.forEach(Consumer<GroupChatRelationship> { this.syncGroupChatRel(it) })
    }

    fun addGroupChatRel(groupChatRel: GroupChatRelationship) {
        groupChatRels.add(groupChatRel)
        syncGroupChatRel(groupChatRel)
    }

    private fun syncGroupChatRel(groupChatRel: GroupChatRelationship) {
        if (groupChatRel.groupChat !== this) {
            groupChatRel.setGroupChat_data(this)
        }
    }
}
