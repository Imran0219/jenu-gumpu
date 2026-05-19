package com.example.genunuguptua.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.genunuguptua.data.AppDatabase
import com.example.genunuguptua.data.HarvestEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HoneyViewModel(application: Application) : AndroidViewModel(application) {
    private val harvestDao = AppDatabase.getDatabase(application).harvestDao()
    
    val allHarvests: Flow<List<HarvestEntry>> = harvestDao.getAllHarvests()
    val totalStock: Flow<Double?> = harvestDao.getTotalCollectiveStock()

    fun addHarvest(
        date: String,
        location: String,
        quantity: Double,
        floralSource: String,
        grade: String,
        moistureContent: Double,
        color: String
    ) {
        viewModelScope.launch {
            harvestDao.insertHarvest(
                HarvestEntry(
                    date = date,
                    location = location,
                    quantity = quantity,
                    floralSource = floralSource,
                    grade = grade,
                    moistureContent = moistureContent,
                    color = color
                )
            )
        }
    }
    
    fun calculateProfit(quantity: Double, retailPrice: Double, cost: Double): Double {
        return (quantity * retailPrice) - cost
    }
}
