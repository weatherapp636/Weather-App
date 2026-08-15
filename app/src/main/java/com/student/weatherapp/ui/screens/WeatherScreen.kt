package com.student.weatherapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.student.weatherapp.data.model.WeatherUiState
import com.student.weatherapp.ui.components.CitySearchBar
import com.student.weatherapp.ui.components.MetricCard
import com.student.weatherapp.ui.components.WeatherDisplayCard
import com.student.weatherapp.viewmodel.WeatherViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        WeatherScreenContent(
            uiState = uiState,
            query = query,
            onQueryChange = viewModel::onSearchQueryChanged,
            onSearch = viewModel::onSearchTriggered,
            paddingValues = innerPadding
        )
    }
}

@Composable
private fun WeatherScreenContent(
    uiState: WeatherUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {
        CitySearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        // Smooth crossfade between Idle / Loading / Success / Error states
        AnimatedContent(
            targetState = uiState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "weather-state-transition"
        ) { state ->
            when (state) {
                is WeatherUiState.Idle -> IdleState()
                is WeatherUiState.Loading -> LoadingState()
                is WeatherUiState.Error -> ErrorState(state.message)
                is WeatherUiState.Success -> SuccessState(state)
            }
        }
    }
}

@Composable
private fun IdleState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Search a city to see the current weather",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.size(40.dp))
        Text(
            text = "Fetching weather...",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun ErrorState(message: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun SuccessState(state: WeatherUiState.Success) {
    Column(modifier = Modifier.fillMaxWidth()) {
        WeatherDisplayCard(
            weather = state.weather,
            isFromCache = state.isFromCache
        )

        Text(
            text = "Details",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
        )

        val weather = state.weather
        val metrics = listOf(
            Triple("Humidity", "${weather.humidity}%", Icons.Default.WaterDrop),
            Triple("Wind Speed", "${weather.windKph} km/h", Icons.Default.Air),
            Triple("UV Index", "${weather.uvIndex}", Icons.Default.WbSunny),
            Triple("Precipitation", "${weather.precipitationMm} mm", Icons.Default.Umbrella)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(metrics) { (label, value, icon) ->
                MetricCard(label = label, value = value, icon = icon)
            }
        }
    }
}
