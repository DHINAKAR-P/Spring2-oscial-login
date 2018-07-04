package com.zimidy.api.layers.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.zimidy.api.layers.storage.entities.node.Node

data class UserPointsDto (
    private val pointBalance: Long? = null,
    private val pointsPending: Long? = null,
    private val firstName: String? = null
) : Node() {
    @JsonProperty("name")
    fun buildFullName(): String? {
        return firstName
    }
}
