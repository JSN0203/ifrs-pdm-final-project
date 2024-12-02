package com.example.finalproject.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "species")
data class Specie(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String?,
    val waterFrequency: Int,
    val careInstructions: String?
) {
    override fun toString(): String {
        return name
    }
}