package com.sector.travelmanager.entity
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attraction(
    val name: String? = null,
    val lat: Int? = null,
    val lon: Int? = null,
    val image: String? = null
): Parcelable {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "lat" to lat,
            "lon" to lon,
            "image" to image
        )
    }
}