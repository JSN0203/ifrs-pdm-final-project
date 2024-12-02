package com.example.finalproject.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.finalproject.models.Place

@Dao
interface PlaceDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: Place)

    @Delete
    suspend fun deletePlace(place: Place)

    @Update
    suspend fun updatePlace(place: Place) : Int

    @Query("""SELECT * FROM places
        WHERE id = :placeId
    """)
    suspend fun getPlaceById(placeId: Int) : Place

    @Query("""SELECT * FROM places""")
    suspend fun getAllPlaces() : List<Place>
}