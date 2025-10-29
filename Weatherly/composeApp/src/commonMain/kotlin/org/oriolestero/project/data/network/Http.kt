package org.oriolestero.project.data.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object Http {
    val client: HttpClient by lazy {
        HttpClient {
            install(Logging) { level = LogLevel.INFO }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true   // tolera campos extra del JSON
                })
            }
        }
    }
}
