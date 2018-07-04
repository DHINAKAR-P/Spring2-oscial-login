package com.zimidy.api.layers.storage.entities.node

import com.zimidy.api.UniquePropertyPart
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(AnnotationTarget.FIELD)
@Retention(RetentionPolicy.RUNTIME)
annotation class UniquePropertyPartContainer(vararg val value: UniquePropertyPart)
