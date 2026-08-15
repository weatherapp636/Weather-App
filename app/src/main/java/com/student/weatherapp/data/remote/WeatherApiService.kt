package com.student.weatherapp.data.remote

import com.student.weatherapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for WeatherAPI.com's current weather endpoint.
 * Docs: https://www.weatherapi.com/docs/
 */
interface WeatherApiService {

    @GET("v1/current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") city: String
    ): WeatherResponse

    companion object {
        const val BASE_URL = "https://api.weatherapi.com/"
    }
}
