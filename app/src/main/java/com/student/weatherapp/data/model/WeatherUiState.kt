package com.student.weatherapp.data.model

/**
 * Explicit UI state modelled as a sealed interface, as required.
 * Idle = nothing searched yet (first app launch, empty search box).
 * Loading = request in flight.
 * Success = we have data to show (from network or offline cache).
 * Error = something went wrong, message is shown to the user.
 */
sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data object Loading : WeatherUiState
    data class Success(val weather: WeatherModel, val isFromCache: Boolean = false) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}
