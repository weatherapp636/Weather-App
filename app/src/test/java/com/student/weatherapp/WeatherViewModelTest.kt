package com.student.weatherapp

import app.cash.turbine.test
import com.student.weatherapp.data.model.WeatherModel
import com.student.weatherapp.data.model.WeatherUiState
import com.student.weatherapp.data.repository.WeatherRepository
import com.student.weatherapp.viewmodel.WeatherViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for WeatherViewModel (bonus requirement).
 * The repository is mocked with MockK so these tests never hit the real network -
 * that's what makes them fast and reliable to run in CI.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: WeatherRepository
    private lateinit var viewModel: WeatherViewModel

    private val sampleWeather = WeatherModel(
        cityName = "London",
        country = "United Kingdom",
        localDateTime = "2026-08-12 14:00",
        temperatureC = 21.0,
        conditionText = "Partly cloudy",
        conditionIconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
        humidity = 60,
        windKph = 14.4,
        uvIndex = 4.0,
        precipitationMm = 0.0
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        // No cache by default; individual tests override with coEvery as needed
        coEvery { repository.getCachedWeather() } returns null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle when there is no cached weather`() = runTest {
        viewModel = WeatherViewModel(repository)
        assertEquals(WeatherUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `initial state loads cached weather when available`() = runTest {
        coEvery { repository.getCachedWeather() } returns sampleWeather
        viewModel = WeatherViewModel(repository)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is WeatherUiState.Success)
            assertEquals(sampleWeather, (state as WeatherUiState.Success).weather)
            assertTrue(state.isFromCache)
        }
    }

    @Test
    fun `searching a blank city shows an error and does not call repository`() = runTest {
        viewModel = WeatherViewModel(repository)
        viewModel.onSearchQueryChanged("   ")
        viewModel.onSearchTriggered()

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Error)
    }

    @Test
    fun `successful search emits Loading then Success`() = runTest {
        coEvery { repository.getCurrentWeather("London") } returns Result.success(sampleWeather)
        viewModel = WeatherViewModel(repository)

        viewModel.uiState.test {
            assertEquals(WeatherUiState.Idle, awaitItem()) // initial

            viewModel.onSearchQueryChanged("London")
            viewModel.onSearchTriggered()

            assertEquals(WeatherUiState.Loading, awaitItem())

            val success = awaitItem()
            assertTrue(success is WeatherUiState.Success)
            assertEquals("London", (success as WeatherUiState.Success).weather.cityName)
            assertTrue(!success.isFromCache)
        }
    }

    @Test
    fun `failed search with no cache emits Error`() = runTest {
        coEvery { repository.getCurrentWeather("Nowhereville") } returns
            Result.failure(Exception("City not found."))
        viewModel = WeatherViewModel(repository)

        viewModel.uiState.test {
            awaitItem() // initial Idle

            viewModel.onSearchQueryChanged("Nowhereville")
            viewModel.onSearchTriggered()

            awaitItem() // Loading
            val errorState = awaitItem()
            assertTrue(errorState is WeatherUiState.Error)
            assertEquals("City not found.", (errorState as WeatherUiState.Error).message)
        }
    }

    @Test
    fun `failed search falls back to cached data when available`() = runTest {
        coEvery { repository.getCurrentWeather("London") } returns
            Result.failure(Exception("No internet"))
        coEvery { repository.getCachedWeather() } returns sampleWeather
        viewModel = WeatherViewModel(repository)

        viewModel.uiState.test {
            awaitItem() // initial state (loads cache -> Success isFromCache=true)

            viewModel.onSearchQueryChanged("London")
            viewModel.onSearchTriggered()

            awaitItem() // Loading
            val fallback = awaitItem()
            assertTrue(fallback is WeatherUiState.Success)
            assertTrue((fallback as WeatherUiState.Success).isFromCache)
        }
    }
}
