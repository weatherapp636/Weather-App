package com.student.weatherapp

import com.student.weatherapp.data.model.Condition
import com.student.weatherapp.data.model.Current
import com.student.weatherapp.data.model.Location
import com.student.weatherapp.data.model.WeatherResponse
import com.student.weatherapp.data.model.toWeatherModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherMapperTest {

    @Test
    fun `mapper fixes protocol-relative icon urls`() {
        val response = WeatherResponse(
            location = Location(name = "Paris", country = "France", localtime = "2026-08-12 15:00"),
            current = Current(
                tempC = 25.0,
                condition = Condition(text = "Sunny", icon = "//cdn.weatherapi.com/weather/64x64/day/113.png"),
                humidity = 40,
                windKph = 10.0,
                uv = 6.0,
                precipMm = 0.0
            )
        )

        val model = response.toWeatherModel()

        assertTrue(model.conditionIconUrl.startsWith("https://"))
        assertEquals("Paris", model.cityName)
        assertEquals("France", model.country)
    }
}
