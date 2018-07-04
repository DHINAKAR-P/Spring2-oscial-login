package com.zimidy.api.layers.dto

import com.zimidy.api.layers.storage.entities.node.nodes.User

class LocationDto {

    var id: Long? = null
    var name: String? = null
    var address: String? = null
    var fuzzyLocation: String? = null
    var locality: String? = null
    var lat: Double? = null
    var lon: Double? = null
    var rating: String? = null
    var imageURL: String? = null
    var description: String? = null
    var reviews: String? = null
    var owner: User? = null
}
