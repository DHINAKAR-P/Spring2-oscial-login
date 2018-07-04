package com.zimidy.api.layers.storage.repositories.node.nodes

import com.zimidy.api.layers.storage.entities.node.nodes.Metadata
import com.zimidy.api.layers.storage.repositories.node.NodeRepository
import com.zimidy.api.layers.storage.repositories.node.NodeRepositoryCustomImpl
import org.neo4j.ogm.session.Session

interface MetadataRepository : NodeRepository<Metadata>, MetadataRepositoryCustom

interface MetadataRepositoryCustom {
    fun load(): Metadata
}

open class MetadataRepositoryImpl(
    private val session: Session
) : NodeRepositoryCustomImpl<Metadata>(session, Metadata::class.java), MetadataRepositoryCustom {

    override fun load(): Metadata = session.loadAll(Metadata::class.java).first()
}
