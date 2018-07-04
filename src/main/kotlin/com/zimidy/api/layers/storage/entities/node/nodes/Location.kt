package com.zimidy.api.layers.storage.entities.node.nodes

import com.zimidy.api.UniquePropertyPart
import com.zimidy.api.layers.storage.entities.node.Node
import com.zimidy.api.layers.storage.entities.node.nodes.review.GeoPoint
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.NonNull
import lombok.Setter
import lombok.ToString
import org.neo4j.ogm.annotation.Property

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = arrayOf("owner"))
data class Location(
    override var latitude: Double? = null,
    override var longitude: Double? = null,
    @param:NonNull @field:Property(name = "googleId")
    @field:UniquePropertyPart("google-place-id")
    val googlePlaceId: String? = null,
    var address: String? = null,
    val fuzzyLocation: String? = null,
    val locality: String? = null,
    val name: String? = null,
    var placeType: String? = null,
    var rating: String? = null,
    var website: String? = null,
    val imageURL: String? = null,
    val description: String? = null,
    val reviews: String? = null,
    val owner: User? = null
    /*@Relationship(type = PostalCode.RELATIONSHIP_LOCATION)
     private val postalCode: PostalCode,
     @Relationship(type = "WITHIN_COUNTRY")
     private val country: Country*/
) : Node(), GeoPoint {

    companion object {
        /*fun newDummyInstance(): Location {
               return Location("")
          }*/
    }
}
