package com.example.genunuguptua.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HarvestDao {
    @Query("SELECT * FROM harvest_history ORDER BY date DESC")
    fun getAllHarvests(): Flow<List<HarvestEntry>>

    @Insert
    suspend fun insertHarvest(entry: HarvestEntry)

    @Query("SELECT SUM(quantity) FROM harvest_history")
    fun getTotalCollectiveStock(): Flow<Double?>
}
