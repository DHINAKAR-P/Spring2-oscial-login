package com.zimidy.api.layers.storage.entities.relationship.relationships

import com.zimidy.api.layers.storage.entities.node.nodes.Event
import com.zimidy.api.layers.storage.entities.node.nodes.GroupChat
import com.zimidy.api.layers.storage.entities.relationship.Relationship
import lombok.Getter
import lombok.NonNull
import lombok.Setter
import lombok.ToString
import org.neo4j.ogm.annotation.EndNode
import org.neo4j.ogm.annotation.RelationshipEntity
import org.neo4j.ogm.annotation.StartNode

@RelationshipEntity(type = GroupChatRelationship.TYPE)
@Getter
@Setter
@ToString(callSuper = true, exclude = arrayOf("event", "groupChat"))
class GroupChatRelationship : Relationship<Event, GroupChat>() {

    @StartNode
    var event: Event? = null
    @EndNode
    var groupChat: GroupChat? = null

    override val type: String
        @NonNull
        get() = TYPE

    fun setGroupChat_data(groupChat: GroupChat) {
        this.endNode = groupChat
        groupChat.addGroupChatRel(this)
    }

    override var startNode: Event? = event
    override var endNode: GroupChat? = groupChat

    companion object {
        const val TYPE = "GROUP_CHAT"
    }
}
