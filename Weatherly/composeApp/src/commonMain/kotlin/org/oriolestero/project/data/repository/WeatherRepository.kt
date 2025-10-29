package org.oriolestero.project.data.repository

import org.oriolestero.project.data.model.Geo
import org.oriolestero.project.data.model.GeoResult
import org.oriolestero.project.data.network.Http
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLBuilder

class WeatherRepository(
    private val client: io.ktor.client.HttpClient = Http.client
) {
    suspend fun geocode(city: String): Geo? {
        val url = URLBuilder("https://geocoding-api.open-meteo.com/v1/search").apply {
            parameters.append("name", city)
            parameters.append("count", "1") // solo queremos el 1º
        }.buildString()

        val res: GeoResult = client.get(url).body()
        return res.results.firstOrNull()
    }
}