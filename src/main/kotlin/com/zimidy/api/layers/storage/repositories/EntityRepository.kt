package com.zimidy.api.layers.storage.repositories

import com.zimidy.api.layers.storage.NoEntityByKeyStorageServerError
import com.zimidy.api.layers.storage.StorageServerError.Companion.storageServerError
import com.zimidy.api.layers.storage.StorageServerError.Type.CANNOT_CREATE_PERSISTENT_ENTITY
import com.zimidy.api.layers.storage.StorageServerError.Type.CANNOT_UPDATE_TRANSIENT_ENTITY
import com.zimidy.api.layers.storage.entities.Entity
import org.neo4j.ogm.session.Session
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.support.SimpleNeo4jRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable
import javax.annotation.PostConstruct

@NoRepositoryBean
interface EntityRepository<T : Entity<ID>, ID : Serializable> : Repository<T, ID>, EntityRepositoryCustom<T, ID>

interface EntityRepositoryCustom<T : Entity<ID>, ID : Serializable> {
    fun count(): Long
    fun exists(id: ID): Boolean
    fun checkExistence(id: ID)

    fun all(): List<T>
    fun page(pageable: Pageable): Page<T>
    fun get(id: ID): T?
    fun getOrThrow(id: ID): T

    fun save(entity: T): T
//    fun save(entity: T, depth: Int): T
    fun create(entity: T, id: ID? = null): T
    fun update(entity: T): T

    fun delete(id: ID): Boolean
}

abstract class EntityRepositoryCustomImpl<T : Entity<ID>, ID : Serializable>(
    private val session: Session,
    private val entityClass: Class<T>
) : EntityRepositoryCustom<T, ID> {

    private lateinit var repository: Neo4jRepository<T, ID>

    @PostConstruct
    private fun init() {
        repository = SimpleNeo4jRepository(entityClass, session)
    }

    override fun count(): Long = repository.count()
    override fun exists(id: ID): Boolean = repository.existsById(id)
    override fun checkExistence(id: ID) {
        if (!exists(id)) throw NoEntityByKeyStorageServerError(id)
    }

    override fun all(): List<T> = repository.findAll().toList()
    override fun page(pageable: Pageable): Page<T> = repository.findAll(pageable)
    override fun get(id: ID): T? = repository.findById(id).orElse(null)
    override fun getOrThrow(id: ID): T = repository.findById(id).orElseThrow({ NoEntityByKeyStorageServerError(id) })

    @Transactional
    override fun save(entity: T): T = repository.save(entity)

    @Transactional
    override fun create(entity: T, id: ID?): T {
        if (entity.persistent) throw storageServerError(CANNOT_CREATE_PERSISTENT_ENTITY)
        entity.id = id
        return save(entity)
    }

    @Transactional
    override fun update(entity: T): T {
        val id = entity.id ?: throw storageServerError(CANNOT_UPDATE_TRANSIENT_ENTITY)
        checkExistence(id)
        return save(entity)
    }

    @Transactional
    override fun delete(id: ID): Boolean {
        if (!exists(id)) return false
        repository.deleteById(id)
        return true
    }
}
