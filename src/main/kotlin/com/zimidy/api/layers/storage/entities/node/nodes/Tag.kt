package com.zimidy.api.layers.storage.entities.node.nodes

import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.userSupport.NaturallyIdentifiedNode
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import lombok.ToString
import java.util.stream.Collectors

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
data class Tag(

    var name: String? = ""

) : Node() {

    init {
        this.name = name?.trim { it <= ' ' }?.toLowerCase()
    }

    fun updateTags(
        tags: MutableCollection<Tag>,
        tagNames: Collection<String>
    ) {
        val newTags = tagNames.stream()
            .filter { tagName -> !tagName.isEmpty() }
            .map({ Tag::class })
            .collect(Collectors.toSet<Any>())
        // updateCollection(tags,tags);
    }

    fun <T : NaturallyIdentifiedNode> updateCollection(
        collection: MutableCollection<T>,
        elements: Collection<T>
    ) {
        collection.retainAll(elements)
        collection.addAll(elements)
    }
}
