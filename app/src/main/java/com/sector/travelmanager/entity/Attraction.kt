package com.sector.travelmanager.entity
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attraction(
    val name: String? = null,
    val description: String? = null,
    val lat: Int? = null,
    val lon: Int? = null,
    val image: String? = null,
    val city: String? = null,
    val area: String? = null,
    val rating: Int? = null,
): Parcelable {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "description" to description,
            "lat" to lat,
            "lon" to lon,
            "image" to image,
            "city" to city,
            "area" to area,
            "rating" to rating
        )
    }
}