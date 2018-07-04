package com.zimidy.api.layers.storage.entities.node

import java.lang.annotation.Repeatable
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Marks a property of a [node entity][Node] as a part of its unique property. A
 * [node entity][Node] may have any number of unique properties, and each unique property
 * may consist of any number of parts.<br></br>
 * <br></br>
 * **Usage example**<br></br>
 * A book may be unique by ISBN. But at the same time in rare cases a book may have no ISBN. Also, the book may be
 * unique by its, say, full name - a combination of several properties: publisher, author and name, where only name is
 * always present. In this naive case you could write something like this:<br></br>
 * <br></br>
 * <pre>
 * class Book extends Node {
 *
 * &#064;UniquePropertyPart(value = "isbn", required = false)
 * private isbn;
 *
 * &#064;UniquePropertyPart(value = "full-name", required = false)
 * private publisher;
 *
 * &#064;UniquePropertyPart(value = "full-name", required = false)
 * private author;
 *
 * &#064;UniquePropertyPart("full-name")
 * private name;
 *
 * ...
 * }
</pre> *
 */
@Repeatable(UniquePropertyPartContainer::class)
@Target(AnnotationTarget.FIELD)
@Retention(RetentionPolicy.RUNTIME)
annotation class UniquePropertyPart(
    /**
     * The name of an unique property
     */
    val value: String,
    /**
     * By default this property part should never be `null`. But if this attribute is set to `false`,
     * then `null` will be allowed.
     */
    val required: Boolean = true
)
