package com.zimidy.api.layers.storage.entities.node.nodes.spatial

import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.node.nodes.Location
import com.zimidy.api.layers.storage.entities.node.nodes.review.GeoPoint
import lombok.Getter
import lombok.Setter
import lombok.ToString
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

@NodeEntity(label = "Spatial")
@Getter
@Setter
@ToString(callSuper = true, exclude = arrayOf("location"))
abstract class SpatialNode : Node(), GeoPoint {

    @Relationship(type = RELATIONSHIP_LOCATION, direction = Relationship.UNDIRECTED)
    private val location: Location? = null

    override val latitude: Double?
        get() = if (location == null) {
            null
        } else location!!.latitude

    override val longitude: Double?
        get() {
            return if (location == null) {
                null
            } else location!!.longitude
        }

    companion object {

        const val RELATIONSHIP_LOCATION = "LOCATION"
    }
}
