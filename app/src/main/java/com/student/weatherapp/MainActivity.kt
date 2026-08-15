package com.student.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import com.student.weatherapp.ui.screens.WeatherScreen
import com.student.weatherapp.ui.theme.WeatherAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // edge-to-edge support, as required
        setContent {
            WeatherAppRoot()
        }
    }
}

@Composable
fun WeatherAppRoot() {
    WeatherAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            WeatherScreen()
        }
    }
}
