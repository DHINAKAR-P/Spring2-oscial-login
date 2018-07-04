package com.zimidy.api.layers.storage

import com.zimidy.api.ServerError

open class StorageServerError protected constructor(type: ErrorType? = null) : ServerError(type) {
    companion object {
        fun storageServerError(type: Type) = StorageServerError(type)
    }

    enum class Type(override val description: String) : ErrorType {
        CANNOT_CREATE_PERSISTENT_ENTITY("Cannot create persistent entity"),
        CANNOT_UPDATE_TRANSIENT_ENTITY("Cannot update transient entity"),
        CANNOT_GENERATE_HASH_CODE_FOR_ENTITY_WITHOUT_ID("Cannot generate hash code for entity without id"),
        PERSISTENT_ENTITY_HAS_NO_ID("Persistent entity has no id"),
    }
}

class NoEntityByKeyStorageServerError(private val key: Map<String, Any>) : StorageServerError() {
    constructor(property: String, value: Any) : this(mapOf(Pair(property, value)))
    constructor(id: Any) : this("id", id)

    override val message: String? get() = "No entity found by the following key: $key"
}
