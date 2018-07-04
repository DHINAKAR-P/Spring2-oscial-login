package com.zimidy.api

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("permitAll()")
annotation class AccessibleByAnyone

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('USER')")
annotation class AccessibleByUsers

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('USER') AND principal == '00000000-0000-0000-0000-000000000000'")
annotation class AccessibleBySuperAdmin

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("denyAll()")
annotation class Inaccessible

// @Repeatable(UniquePropertyPartContainer::class.java)
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
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
