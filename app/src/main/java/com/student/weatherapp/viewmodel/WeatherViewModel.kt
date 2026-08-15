package com.student.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.weatherapp.data.model.WeatherUiState
import com.student.weatherapp.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Bonus requirement: StateFlow + Unidirectional Data Flow instead of LiveData.
 * The UI observes `uiState` with collectAsStateWithLifecycle() and sends
 * events back in one direction only (onSearch -> viewModel -> new state).
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        // On first launch, try to show whatever we cached last time so the
        // screen isn't just blank while the user hasn't searched anything yet.
        loadCachedWeatherIfAvailable()
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onSearchTriggered() {
        val city = _searchQuery.value.trim()

        if (city.isEmpty()) {
            _uiState.value = WeatherUiState.Error("Please enter a city name to search.")
            return
        }

        searchWeather(city)
    }

    private fun searchWeather(city: String) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            val result = repository.getCurrentWeather(city)

            result.fold(
                onSuccess = { weather ->
                    _uiState.value = WeatherUiState.Success(weather, isFromCache = false)
                },
                onFailure = { error ->
                    // Network failed - fall back to cache if we have one, rather
                    // than just showing an error screen with nothing useful.
                    val cached = repository.getCachedWeather()
                    if (cached != null) {
                        _uiState.value = WeatherUiState.Success(cached, isFromCache = true)
                    } else {
                        _uiState.value = WeatherUiState.Error(
                            error.message ?: "Something went wrong. Please try again."
                        )
                    }
                }
            )
        }
    }

    private fun loadCachedWeatherIfAvailable() {
        viewModelScope.launch {
            val cached = repository.getCachedWeather()
            if (cached != null) {
                _uiState.value = WeatherUiState.Success(cached, isFromCache = true)
                _searchQuery.value = cached.cityName
            }
        }
    }
}
