package com.zimidy.api.layers.userSupport

import com.zimidy.api.layers.storage.entities.node.Node
import org.neo4j.ogm.annotation.Index

abstract class NaturallyIdentifiedNode : Node() {

    private val naturalId: Any?
        get() {
            val naturalIdField =
                Utils.getAllDeclaredAnnotatedFields(Class.forName(generateUUId()), Index::class.java).stream()
                    .filter { indexedField ->
                        val indexAnnotation = indexedField.getAnnotation(Index::class.java)
                        indexAnnotation.unique && indexAnnotation.primary
                    }
                    .collect(Utils.singletonCollector()) ?: throw RuntimeException("No natural id specified.")
            naturalIdField.setAccessible(true)
            try {
                return naturalIdField.get(this)
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            }
        }

    protected fun generateUUId(): String? {
        val naturalId = naturalId
        return naturalId?.toString()
    }

    companion object {

        fun <T : NaturallyIdentifiedNode> updateCollection(
            collection: MutableCollection<T>,
            elements: Collection<T>
        ) {
            collection.retainAll(elements)
            collection.addAll(elements)
        }
    }
}
