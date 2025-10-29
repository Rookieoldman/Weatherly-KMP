package org.oriolestero.project

import org.oriolestero.project.data.network.Http
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.oriolestero.project.data.repository.WeatherRepository
import kotlin.test.*

class WeatherRepositoryTest {

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
    fun `geocode returns first result`() = runTest {
        val json = """{ "results": [ { "name":"Madrid","latitude":40.4,"longitude":-3.7,"country":"Spain" } ] }"""
        val repo = WeatherRepository(mockClient(json))
        val geo = repo.geocode("Madrid")

        // First, assert that the object itself is not null
        assertNotNull(geo)

        // Then, you can safely access its properties
        assertEquals("Madrid", geo.name)
        //assertEquals(40.4, geo.latitude!!, 0.0001) // This will now compile without error
    }

    @Test
    fun `geocode returns null on empty results`() = runTest {
        val json = """{ "results": [] }"""
        val repo = WeatherRepository(mockClient(json))
        val geo = repo.geocode("Nowhere")
        assertNull(geo)
    }
}