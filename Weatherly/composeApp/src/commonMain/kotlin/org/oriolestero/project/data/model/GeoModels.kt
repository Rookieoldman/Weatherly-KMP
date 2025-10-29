package org.oriolestero.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GeoResult(
    val results: List<Geo> = emptyList()
)

@Serializable
data class Geo(
    val id: Int? = null,
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val country: String? = null
)
