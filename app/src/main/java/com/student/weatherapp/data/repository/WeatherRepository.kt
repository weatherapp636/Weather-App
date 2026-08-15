package com.student.weatherapp.data.repository

import com.student.weatherapp.data.local.WeatherDao
import com.student.weatherapp.data.local.toEntity
import com.student.weatherapp.data.local.toWeatherModel
import com.student.weatherapp.data.model.WeatherModel
import com.student.weatherapp.data.model.toWeatherModel
import com.student.weatherapp.data.remote.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

/**
 * Repository is the single source of truth for weather data. The ViewModel
 * doesn't know or care whether the data came from Retrofit or Room - it
 * just asks the repository for a Result<WeatherModel>.
 */
interface WeatherRepository {
    suspend fun getCurrentWeather(city: String): Result<WeatherModel>
    suspend fun getCachedWeather(): WeatherModel?
}

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService,
    private val dao: WeatherDao,
    private val apiKey: String
) : WeatherRepository {

    override suspend fun getCurrentWeather(city: String): Result<WeatherModel> =
        withContext(Dispatchers.IO) {
            try {
                if (apiKey.isBlank() || apiKey == "PUT_YOUR_WEATHERAPI_KEY_HERE") {
                    return@withContext Result.failure(
                        IllegalStateException(
                            "Missing API key. Add your WeatherAPI.com key to gradle.properties (WEATHER_API_KEY=...)"
                        )
                    )
                }

                val response = api.getCurrentWeather(apiKey = apiKey, city = city)
                val model = response.toWeatherModel()

                // cache the successful result for offline viewing
                dao.upsert(model.toEntity(System.currentTimeMillis()))

                Result.success(model)
            } catch (e: IOException) {
                // no internet / DNS failure / timeout
                Result.failure(IOException("No internet connection. Showing cached data if available.", e))
            } catch (e: retrofit2.HttpException) {
                val message = when (e.code()) {
                    400 -> "City not found. Check the spelling and try again."
                    401, 403 -> "Invalid API key. Check your WeatherAPI.com key in gradle.properties."
                    else -> "Server error (${e.code()}). Please try again later."
                }
                Result.failure(Exception(message, e))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getCachedWeather(): WeatherModel? = withContext(Dispatchers.IO) {
        dao.getCached()?.toWeatherModel()
    }
}
