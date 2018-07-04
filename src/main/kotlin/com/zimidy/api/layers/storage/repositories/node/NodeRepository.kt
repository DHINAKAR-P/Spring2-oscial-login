package com.zimidy.api.layers.storage.repositories.node

import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.repositories.EntityRepository
import com.zimidy.api.layers.storage.repositories.EntityRepositoryCustomImpl
import org.neo4j.ogm.session.Session
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface NodeRepository<T : Node> : EntityRepository<T, NodeId>, NodeRepositoryCustom

interface NodeRepositoryCustom

abstract class NodeRepositoryCustomImpl<T : Node>(
    session: Session,
    entityClass: Class<T>
) : EntityRepositoryCustomImpl<T, NodeId>(session, entityClass), NodeRepositoryCustom
