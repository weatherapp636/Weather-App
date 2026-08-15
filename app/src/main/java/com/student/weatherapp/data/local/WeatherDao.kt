package com.student.weatherapp.data.local

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WeatherEntity)

    // id is always 0 - we only ever cache the most recent search
    @Query("SELECT * FROM cached_weather WHERE id = 0 LIMIT 1")
    suspend fun getCached(): WeatherEntity?
}
