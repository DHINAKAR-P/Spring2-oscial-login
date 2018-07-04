package com.zimidy.api

/**
 * It happens if a client does something wrong with the server.
 * If it happens, than client's code should either handle the error or be fixed.
 */
class ClientError(description: String) : RuntimeException(description) {

    constructor(type: Type) : this(type.description)

    /**
     * @property description helps client's developers to understand what happened
     */
    enum class Type(val description: String) {
        NO_ENTITY_BY_ID("No entity found by id");

        override fun toString(): String {
            return "Error $name: $description."
        }
    }
}

/**
 * It happens if a part of server's code does something wrong with another part.
 * It it happens, than a part of server's code should be fixed.
 */
open class ServerError protected constructor(type: ErrorType? = null) : RuntimeException(type?.description) {

    companion object {
        fun serverError(type: Type) = ServerError(type)
    }

    interface ErrorType {
        val description: String
    }

    enum class Type(override val description: String) : ErrorType {
        LOGIC_NOT_IMPLEMENTED("You cannot call this method, because its logic is not implemented yet")
    }
}

/**
 * Server exceptions represent a hierarchy of expected exceptions, which don't indicate programmers' mistakes.
 */
abstract class ServerException(description: String?) : Exception(description)
