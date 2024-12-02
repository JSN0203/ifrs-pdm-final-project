package com.example.finalproject.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.finalproject.models.Specie

@Dao
interface SpecieDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecie(specie: Specie)

    @Delete
    suspend fun deleteSpecie(specie: Specie)

    @Update
    suspend fun updateSpecie(specie: Specie) : Int

    @Query("""SELECT * FROM species""")
    suspend fun getAllSpecies() : List<Specie>

    @Query("""SELECT * FROM species
        WHERE id = :specieId
    """)
    suspend fun getSpecieById(specieId: Int) : Specie
}