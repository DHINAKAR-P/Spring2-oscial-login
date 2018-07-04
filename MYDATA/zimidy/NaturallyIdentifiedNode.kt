package com.zimidy.api.layers.storage.entities.node.nodes


import com.zimidy.api.Utils
import com.zimidy.api.layers.storage.entities.node.Node
import org.neo4j.ogm.annotation.Index

import java.lang.reflect.Field

/**
 * Each child class must annotate one and only one field as its natural identifier
 * using the [org.neo4j.ogm.annotation.Index] annotation as shown bellow:
 *
 *
 * `@Index(unique = true, primary = true)`
 */
abstract class NaturallyIdentifiedNode : Node() {

    /*private val naturalId: Any?
      get() {
          val naturalIdField = Utils.getAllDeclaredAnnotatedFields(getClass(), Index::class.java).stream()
                  .filter({ indexedField ->
                      val indexAnnotation = indexedField.getAnnotation(Index::class.java)
                      indexAnnotation.unique() && indexAnnotation.primary()
                  })
                  .collect(Utils.singletonCollector()) ?: throw RuntimeException("No natural id specified.")
          naturalIdField.setAccessible(true)
          try {
              return naturalIdField[this]
          } catch (e: IllegalAccessException) {
              throw RuntimeException(e)
          }

      }*/

    protected fun generateUUId(): String? {
        val naturalId = ""//naturalId
        return naturalId?.toString()
    }

    companion object {

        fun <T : NaturallyIdentifiedNode> updateCollection(
                collection: MutableCollection<T>, elements: Collection<T>
        ) {
            collection.retainAll(elements)
            collection.addAll(elements)
        }
    }
}
