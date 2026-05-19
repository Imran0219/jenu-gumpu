package com.example.genunuguptua.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "harvest_history")
data class HarvestEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val location: String,
    val quantity: Double, // in kg
    val floralSource: String,
    val grade: String,
    val moistureContent: Double,
    val color: String
)
