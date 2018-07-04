package com.zimidy.api.layers.storage.repositories.relationship

import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.relationship.Relationship
import com.zimidy.api.layers.storage.entities.relationship.RelationshipId
import com.zimidy.api.layers.storage.repositories.EntityRepository
import com.zimidy.api.layers.storage.repositories.EntityRepositoryCustomImpl
import org.neo4j.ogm.session.Session
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
@SuppressWarnings("rawtypes")
interface RelationshipRepository<T : Relationship<Node, Node>> : EntityRepository<T, RelationshipId>, RelationshipRepositoryCustom

interface RelationshipRepositoryCustom

abstract class RelationshipRepositoryCustomImpl<T : Relationship<Node, Node>>(
    session: Session,
    entityClass: Class<T>
) : EntityRepositoryCustomImpl<T, RelationshipId>(session, entityClass), RelationshipRepositoryCustom
