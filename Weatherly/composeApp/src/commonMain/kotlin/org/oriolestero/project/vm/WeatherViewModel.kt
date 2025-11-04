package org.oriolestero.project.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.oriolestero.project.data.repository.WeatherDataSource
import org.oriolestero.project.data.repository.WeatherRepository
import kotlinx.coroutines.*

data class WeatherUiState(
    val isLoading: Boolean = false,
    val cityLabel: String? = null,
    val temperatureC: Double? = null,
    val windMs: Double? = null,
    val error: String? = null
)
private const val TAG = "WeatherVM"
class WeatherViewModel(
    private val repo: WeatherDataSource = WeatherRepository(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    var uiState by mutableStateOf(WeatherUiState())


        private set

    fun search(city: String) {
        if (city.isBlank()) {
            println("$TAG empty query")
            uiState = uiState.copy(error = "Introduce una ciudad")
            return
        }
        uiState = uiState.copy(isLoading = true, error = null)
        scope.launch {
            runCatching {
                val geo = repo.geocode(city) ?: error("No se encontró la ciudad")
                val res = repo.currentWeather(geo.latitude!!, geo.latitude)
                WeatherUiState(
                    isLoading = false,
                    cityLabel = buildString {
                        append(geo.name)
                        geo.country?.let { append(", ").append(it) }
                    },
                    temperatureC = res.current.temperature,
                    windMs = res.current.windSpeed
                )
            }.onSuccess { uiState = it }
                .onFailure { uiState = uiState.copy(isLoading = false, error = it.message ?: "Error") }
        }
    }

    fun clear() { uiState = WeatherUiState() }
    fun onCleared() { scope.cancel() } // llámalo desde plataformas si quieres
}