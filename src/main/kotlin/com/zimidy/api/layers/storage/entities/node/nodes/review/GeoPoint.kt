package com.zimidy.api.layers.storage.entities.node.nodes.review

import org.springframework.lang.Nullable

interface GeoPoint {

    @get:Nullable
    val latitude: Double?

    @get:Nullable
    val longitude: Double?
}
