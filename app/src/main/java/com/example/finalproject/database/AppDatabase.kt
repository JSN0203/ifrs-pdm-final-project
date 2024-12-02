package com.example.finalproject.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.finalproject.dao.PlaceDAO
import com.example.finalproject.dao.PlantDAO
import com.example.finalproject.dao.SpecieDAO
import com.example.finalproject.models.Place
import com.example.finalproject.models.Plant
import com.example.finalproject.models.Specie

@Database(entities = [Place::class, Plant::class, Specie::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placeDao() : PlaceDAO
    abstract fun specieDao() : SpecieDAO
    abstract fun plantDao() : PlantDAO
}