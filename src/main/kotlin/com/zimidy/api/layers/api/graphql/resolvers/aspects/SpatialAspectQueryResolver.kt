package com.zimidy.api.layers.api.graphql.resolvers.aspects

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.zimidy.api.DistanceUnit
import com.zimidy.api.layers.storage.entities.node.nodes.Location
import com.zimidy.api.layers.storage.storages.SpatialStorage
import org.springframework.stereotype.Component

@Component
class SpatialAspectQueryResolver(private val spatialStorage: SpatialStorage) : GraphQLQueryResolver {

    fun calculateDistance(a: Location, b: Location, distanceUnit: DistanceUnit): Double = spatialStorage.calculateDistanceInUnits(a, b, distanceUnit)
}
