package com.zimidy.api.layers.storage.repositories.node.nodes

import com.zimidy.api.layers.storage.entities.node.nodes.UserInterest
import com.zimidy.api.layers.storage.repositories.node.NodeRepository
import com.zimidy.api.layers.storage.repositories.node.NodeRepositoryCustomImpl
import org.neo4j.ogm.session.Session
import org.springframework.data.neo4j.annotation.Query
import org.springframework.transaction.annotation.Transactional

interface UserInterestRepository : NodeRepository<UserInterest>, UserInterestRepositoryCustom {

    @Query("MATCH (ui:UserInterest) where toLower(ui.name) = toLower({0}) return ui")
    fun findOneByNameIgnoreCase(name: String): UserInterest
}

interface UserInterestRepositoryCustom {
    fun update(data: String): UserInterest
    fun display(data: String): String
}

open class UserInterestRepositoryImpl(
    session: Session
) : NodeRepositoryCustomImpl<UserInterest>(session, UserInterest::class.java), UserInterestRepositoryCustom {

    @Transactional
    override fun update(data: String): UserInterest {
        val user = getOrThrow(data)
        return update(user)
    }

    override fun display(data: String): String {
        return data.capitalize()
    }
}
