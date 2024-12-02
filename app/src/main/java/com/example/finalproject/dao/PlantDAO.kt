package com.example.finalproject.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.finalproject.models.Plant

@Dao
interface PlantDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant : Plant)

    @Delete
    suspend fun deletePlant(plant: Plant)

    @Update
    suspend fun updatePlant(plant: Plant) : Int

    @Query("""SELECT * FROM plants""")
    suspend fun getAllPlants() : List<Plant>

    @Query("""SELECT * FROM plants
        WHERE id = :plantId
    """)
    suspend fun getPlantById(plantId: Int) : Plant
}