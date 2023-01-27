package com.example.bmr_app.weatherData

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface weatherDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(weatherTable: weatherTable)

    @Query("DELETE FROM weather_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM weather_table ORDER BY id DESC")
    fun getAllWeather(): Flow<List<weatherTable>>

    @Query("SELECT AVG(`temp`) FROM weather_table")
    fun getAvgTemp(): Flow<Double>
}