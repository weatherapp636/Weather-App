package com.student.weatherapp.di

import com.student.weatherapp.data.local.WeatherDao
import com.student.weatherapp.data.remote.WeatherApiService
import com.student.weatherapp.data.repository.WeatherRepository
import com.student.weatherapp.data.repository.WeatherRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApiService,
        dao: WeatherDao,
        @Named("weatherApiKey") apiKey: String
    ): WeatherRepository = WeatherRepositoryImpl(api, dao, apiKey)
}
