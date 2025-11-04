package org.oriolestero.project

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.oriolestero.project.data.repository.WeatherRepository
import kotlin.test.*


class WeatherRepositoryCurrentTest {

    private fun mockClient(body: String, status: HttpStatusCode = HttpStatusCode.OK) =
        HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = body,
                        status = status,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

    @Test
    fun `currentWeather returns temperature and wind`() = runTest {
        val json = """
            {
              "current": {
                "temperature_2m": 18.3,
                "wind_speed_10m": 3.2
              }
            }
        """.trimIndent()
        val repo = WeatherRepository(mockClient(json))
        val res = repo.currentWeather(40.4, -3.7)
        assertEquals(18.3, res.current.temperature, 0.0001)
        assertEquals(3.2, res.current.windSpeed!!, 0.0001)
    }

    @Test
    fun `currentWeather handles missing wind`() = runTest {
        val json = """{ "current": { "temperature_2m": 21.0 } }"""
        val repo = WeatherRepository(mockClient(json))
        val res = repo.currentWeather(40.4, -3.7)
        assertEquals(21.0, res.current.temperature, 0.0001)
        assertNull(res.current.windSpeed)
    }

    @Test
    fun `currentWeather propagates http error`() = runTest {
        val repo = WeatherRepository(mockClient("""{"error":"bad"}""", HttpStatusCode.BadRequest))
        // según tu política: aquí comprobamos que lanza excepción
        assertFails { repo.currentWeather(0.0, 0.0) }
    }
}