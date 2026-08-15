package com.student.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.student.weatherapp.data.model.WeatherModel

/**
 * Room entity used to cache the LAST successfully fetched city so the user
 * has something to look at when they open the app offline.
 * I'm only caching one row (id is always 0, see OnConflictStrategy.REPLACE
 * in the DAO) since the task only asked for "last searched weather".
 */
@Entity(tableName = "cached_weather")
data class WeatherEntity(
    @PrimaryKey val id: Int = 0,
    val cityName: String,
    val country: String,
    val localDateTime: String,
    val temperatureC: Double,
    val conditionText: String,
    val conditionIconUrl: String,
    val humidity: Int,
    val windKph: Double,
    val uvIndex: Double,
    val precipitationMm: Double,
    val cachedAtMillis: Long
)

fun WeatherEntity.toWeatherModel() = WeatherModel(
    cityName = cityName,
    country = country,
    localDateTime = localDateTime,
    temperatureC = temperatureC,
    conditionText = conditionText,
    conditionIconUrl = conditionIconUrl,
    humidity = humidity,
    windKph = windKph,
    uvIndex = uvIndex,
    precipitationMm = precipitationMm
)

fun WeatherModel.toEntity(cachedAtMillis: Long) = WeatherEntity(
    cityName = cityName,
    country = country,
    localDateTime = localDateTime,
    temperatureC = temperatureC,
    conditionText = conditionText,
    conditionIconUrl = conditionIconUrl,
    humidity = humidity,
    windKph = windKph,
    uvIndex = uvIndex,
    precipitationMm = precipitationMm,
    cachedAtMillis = cachedAtMillis
)
