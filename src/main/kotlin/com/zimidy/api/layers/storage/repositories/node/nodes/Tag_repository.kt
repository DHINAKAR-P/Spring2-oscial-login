package com.zimidy.api.layers.storage.repositories.node.nodes

import com.zimidy.api.configurations.security.SessionContextHolder
import com.zimidy.api.layers.dto.TagDto
import com.zimidy.api.layers.storage.entities.node.nodes.Tag
import com.zimidy.api.layers.storage.repositories.node.NodeRepository
import com.zimidy.api.layers.storage.repositories.node.NodeRepositoryCustomImpl
import org.neo4j.ogm.session.Session
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

interface TagRepository : NodeRepository<Tag>, TagRepositoryCustom {
    fun findByName(name: String): Tag
}

interface TagRepositoryCustom {
    fun update(data: String): Tag
    fun display(data: String): String
    fun getTags(): List<TagDto>
}

open class TagRepositoryImpl(
    private val session: Session,
    private val sessionContextHolder: SessionContextHolder
) : NodeRepositoryCustomImpl<Tag>(session, Tag::class.java), TagRepositoryCustom {

    override fun display(data: String): String {
        return data.capitalize()
    }

    @Transactional
    override fun update(data: String): Tag {
        val user = getOrThrow(data)
        return update(user)
    }

    @Transactional
    override fun getTags(): List<TagDto> {
        val tags = all()
        return tags.stream().map({ tag ->
            val tagDto = TagDto()
            tagDto.name = tag.name
            tagDto
        }).collect(Collectors.toList<TagDto>())
    }
}
