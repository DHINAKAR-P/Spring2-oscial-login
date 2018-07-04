package com.zimidy.api.layers.storage.entities

import com.zimidy.api.layers.storage.entities.node.nodes.Location
import org.neo4j.ogm.typeconversion.CompositeAttributeConverter

class LocationConverter : CompositeAttributeConverter<Location> {

    companion object {
        const val PROPERTY_LATITUDE = "latitude"
        const val PROPERTY_LONGITUDE = "longitude"
    }

    override fun toGraphProperties(location: Location?): Map<String, *> {
        val properties = HashMap<String, Double>()
        if (location != null) {
            properties.put(PROPERTY_LATITUDE, location.latitude!!)
            properties.put(PROPERTY_LONGITUDE, location.longitude!!)
        }
        return properties
    }

    override fun toEntityAttribute(map: Map<String, *>): Location? {
        val latitude = map[PROPERTY_LATITUDE] as Double?
        val longitude = map[PROPERTY_LONGITUDE] as Double?
        return if (latitude != null && longitude != null) Location(latitude, longitude) else null
    }
}
