package com.zimidy.api.layers.mapper

import com.zimidy.api.layers.dto.LocationDto
import com.zimidy.api.layers.storage.entities.node.nodes.Location
import java.util.ArrayList

object LocationMapper {

    fun map(location: Location?): LocationDto? {
        if (location == null) {
            return null
        }
        val dto = LocationDto()
        dto.name = location.name
        dto.address = location.address
        dto.fuzzyLocation = location.fuzzyLocation
        dto.locality = location.locality
        dto.lat = location.latitude
        dto.lon = location.longitude
        dto.rating = location.rating
        dto.imageURL = location.imageURL
        dto.description = location.description
        dto.reviews = location.reviews
        dto.owner = location.owner
        return dto
    }

    fun map(locations: List<Location>): List<LocationDto> {
        val dtos = ArrayList<LocationDto>()
        for (location in locations) {
            dtos.add(map(location)!!)
        }
        return dtos
    }
}
