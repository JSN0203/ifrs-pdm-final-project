package com.example.finalproject.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "plants",
    foreignKeys = [
        ForeignKey(
            entity = Specie::class,
            parentColumns = ["id"],
            childColumns = ["specieId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Place::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Plant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nickname: String,
    val specieId: Int,
    val placeId: Int,
    val lastWatering: Long?,
    val notes: String?
)
