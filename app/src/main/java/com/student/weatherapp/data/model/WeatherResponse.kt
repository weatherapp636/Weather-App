package com.student.weatherapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Top level response from https://api.weatherapi.com/v1/current.json
 * I only mapped the fields the app actually needs (there are a lot more
 * in the real response, but no point modelling ones we never use).
 */
data class WeatherResponse(
    @SerializedName("location") val location: Location,
    @SerializedName("current") val current: Current
)

data class Location(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("localtime") val localtime: String
)

data class Current(
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("condition") val condition: Condition,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("uv") val uv: Double,
    @SerializedName("precip_mm") val precipMm: Double
)

data class Condition(
    @SerializedName("text") val text: String,
    // WeatherAPI returns protocol-relative urls like "//cdn.weatherapi.com/...png"
    @SerializedName("icon") val icon: String
)
