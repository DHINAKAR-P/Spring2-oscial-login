package com.zimidy.api

import com.zimidy.api.layers.storage.entities.node.nodes.Location

// class Location(val latitude: Double, val longitude: Double)

interface Spatial {
    var location: Location?
}

enum class DistanceUnit(private val metersInUnit: Double) {
    METER(1.0),
    KILOMETER(1000.0),
    MILE(1609.344);

    fun metersToUnits(distanceInMeters: Double) = distanceInMeters / this.metersInUnit
    fun unitsToMeters(distanceInUnits: Double) = distanceInUnits * this.metersInUnit
}
