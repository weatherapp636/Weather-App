package com.student.weatherapp.data.model

/**
 * Clean UI-facing model. Keeping this separate from WeatherResponse means
 * if the API shape ever changes, only the mapper function has to change -
 * not every screen that uses weather data.
 */
data class WeatherModel(
    val cityName: String,
    val country: String,
    val localDateTime: String,
    val temperatureC: Double,
    val conditionText: String,
    val conditionIconUrl: String,
    val humidity: Int,
    val windKph: Double,
    val uvIndex: Double,
    val precipitationMm: Double
)

fun WeatherResponse.toWeatherModel(): WeatherModel {
    // WeatherAPI gives icon urls without a scheme ("//cdn.weatherapi.com/..."),
    // Coil needs a full url or it fails to load.
    val fixedIconUrl = if (current.condition.icon.startsWith("//")) {
        "https:${current.condition.icon}"
    } else {
        current.condition.icon
    }

    return WeatherModel(
        cityName = location.name,
        country = location.country,
        localDateTime = location.localtime,
        temperatureC = current.tempC,
        conditionText = current.condition.text,
        conditionIconUrl = fixedIconUrl,
        humidity = current.humidity,
        windKph = current.windKph,
        uvIndex = current.uv,
        precipitationMm = current.precipMm
    )
}
