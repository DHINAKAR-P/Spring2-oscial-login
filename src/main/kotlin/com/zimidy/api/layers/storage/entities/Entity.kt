package com.zimidy.api.layers.storage.entities

import com.zimidy.api.layers.storage.StorageServerError.Companion.storageServerError
import com.zimidy.api.layers.storage.StorageServerError.Type.CANNOT_GENERATE_HASH_CODE_FOR_ENTITY_WITHOUT_ID
import com.zimidy.api.layers.storage.StorageServerError.Type.PERSISTENT_ENTITY_HAS_NO_ID
import java.io.Serializable

abstract class Entity<ID : Serializable>(open var id: ID? = null) {

    val persistent: Boolean get() = id != null

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Entity<*>

        if (id != other.id) return false

        return true
    }

    final override fun hashCode(): Int {
        return id?.hashCode() ?: throw storageServerError(CANNOT_GENERATE_HASH_CODE_FOR_ENTITY_WITHOUT_ID)
    }

    final override fun toString(): String {
        val type = this::class.simpleName
        return "$type(id=$id)"
    }

    fun getIdOrThrow(): ID = id ?: throw storageServerError(PERSISTENT_ENTITY_HAS_NO_ID)
}
