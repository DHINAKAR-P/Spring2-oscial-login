package com.zimidy.api.layers.storage.storages

import com.zimidy.api.DistanceUnit
import com.zimidy.api.layers.storage.entities.node.nodes.Location
import com.zimidy.api.Spatial
import com.zimidy.api.layers.storage.entities.node.Node
import org.neo4j.ogm.session.Session
import org.springframework.stereotype.Component

@Component
class SpatialStorage(private val session: Session) {

    companion object {
        private const val POINT_LATITUDE = "latitude"
        private const val POINT_LONGITUDE = "longitude"
        private val noParameters = emptyMap<String, String>()
    }

    // It is possible to implement distance calculation in code to avoid one extra request to Neo4j, which could make
    // this method to work faster. But, on the other hand, such an implementation may produce results, different from
    // what is produced by Neo4j's native implementation. I decided to leverage Neo4j's native implementation in order
    // to achieve consistency with other spatial based methods, which also rely on Neo4j's native implementation.
    fun calculateDistanceInUnits(a: Location, b: Location, unit: DistanceUnit): Double {
        val query =
                """
                |WITH
                | point({$POINT_LATITUDE: ${a.latitude}, $POINT_LONGITUDE: ${a.longitude}}) as a,
                | point({$POINT_LATITUDE: ${b.latitude}, $POINT_LONGITUDE: ${b.longitude}}) as b
                |RETURN distance(a, b)
                """.trimMargin()
        val distanceInMeters = session.queryForObject(Double::class.java, query, noParameters)
        return unit.metersToUnits(distanceInMeters)
    }

    fun calculateDistanceInUnits(s1: Spatial, s2: Spatial, distanceUnit: DistanceUnit): Double? {
        val a = s1.location
        val b = s2.location
        return if (a != null && b != null) calculateDistanceInUnits(a, b, distanceUnit) else null
    }

    fun <T : Node> findNodesByLocation(
        nodesType: Class<T>,
        location: Location,
        radiusInUnits: Double,
        unit: DistanceUnit
    ): Iterable<T> {
        val radiusInMeters = unit.unitsToMeters(radiusInUnits)
        val query = """
                |MATCH (n:${nodesType.simpleName})
                |WHERE distance(
                | point(n),
                | point({$POINT_LATITUDE: ${location.latitude}, $POINT_LONGITUDE: ${location.longitude}})
                |) <= $radiusInMeters
                |RETURN n
                """.trimMargin()
        return session.query(nodesType, query, noParameters)
    }
}
