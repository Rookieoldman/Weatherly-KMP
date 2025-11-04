package org.oriolestero.project

import org.oriolestero.project.data.model.CurrentWeather
import org.oriolestero.project.data.model.Geo
import org.oriolestero.project.data.model.WeatherResponse
import org.oriolestero.project.data.repository.WeatherDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.oriolestero.project.vm.WeatherViewModel
import kotlin.test.*

private class FakeRepo(
    private val geo: Geo? = Geo(name = "Madrid", latitude = 40.4, longitude = -3.7, country = "Spain"),
    private val weather: WeatherResponse = WeatherResponse(CurrentWeather(temperature = 20.0, windSpeed = 3.0)),
    private val fail: Boolean = false
) : WeatherDataSource {
    override suspend fun geocode(city: String): Geo? {
        if (fail) error("Fallo geo")
        return geo
    }
    override suspend fun currentWeather(lat: Double, lon: Double): WeatherResponse {
        if (fail) error("Fallo weather")
        return weather
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @Test
    fun `search success updates uiState with data`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val vm = WeatherViewModel(repo = FakeRepo(), dispatcher = dispatcher)

        vm.search("Madrid")
        dispatcher.scheduler.advanceUntilIdle()

        val s = vm.uiState
        assertFalse(s.isLoading)
        assertEquals("Madrid, Spain", s.cityLabel)
        assertEquals(20.0, s.temperatureC)
        assertEquals(3.0, s.windMs)
        assertNull(s.error)
    }

    @Test
    fun `search not found sets error`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val vm = WeatherViewModel(repo = FakeRepo(geo = null), dispatcher = dispatcher)

        vm.search("Nowhere")
        dispatcher.scheduler.advanceUntilIdle()

        assertNotNull(vm.uiState.error)
        assertFalse(vm.uiState.isLoading)
    }
}