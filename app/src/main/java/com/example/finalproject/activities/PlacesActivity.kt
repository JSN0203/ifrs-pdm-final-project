package com.example.finalproject.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.finalproject.activities.adapters.PlacesAdapter
import com.example.finalproject.dao.PlaceDAO
import com.example.finalproject.database.AppDatabase
import com.example.finalproject.databinding.ActivityPlacesBinding
import com.example.finalproject.databinding.DialogAddPlaceBinding
import com.example.finalproject.models.Place
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PlacesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlacesBinding
    private lateinit var placeDao: PlaceDAO
    private lateinit var db: AppDatabase
    private lateinit var adapter: PlacesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-db"
        )
            .fallbackToDestructiveMigration()
            .build()


        placeDao = db.placeDao()

        adapter = PlacesAdapter(
            onEdit = { place ->
                showEditDialog(place)
            },
            onDelete = { place ->
                showDeletePlaceDialog(place)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        refreshPlacesList()

        binding.add.setOnClickListener {
            showAddPlaceDialog()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun refreshPlacesList() {
        lifecycleScope.launch {
            val placesList = db.placeDao().getAllPlaces()
            adapter.submitList(placesList)
        }
    }

    private fun showEditDialog(place: Place) {
        val dialogBinding = DialogAddPlaceBinding.inflate(layoutInflater)

        dialogBinding.placeName.setText(place.name)
        dialogBinding.placeDescription.setText(place.description)

        AlertDialog.Builder(this)
            .setTitle("Edit Place")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val updatedPlace = place.copy(
                    name = dialogBinding.placeName.text.toString(),
                    description = dialogBinding.placeDescription.text.toString()
                )
                lifecycleScope.launch {
                    db.placeDao().updatePlace(updatedPlace)
                    refreshPlacesList()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeletePlaceDialog(place: Place) {
        AlertDialog.Builder(this)
            .setTitle("Delete Place")
            .setMessage("Are you sure you want to delete ${place.name}?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    placeDao.deletePlace(place)
                    refreshPlacesList()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddPlaceDialog() {
        val dialogBinding = DialogAddPlaceBinding.inflate(layoutInflater)

        AlertDialog.Builder(this)
            .setTitle("Add Place")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.placeName.text.toString().trim()
                val description = dialogBinding.placeDescription.text.toString()

                if (name.isEmpty()) {
                    Snackbar.make(dialogBinding.root, "Name cannot be empty", Snackbar.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    try {
                        val newPlace = Place(name = name, description = description)
                        placeDao.insertPlace(newPlace)
                        refreshPlacesList()
                    } catch (e: Exception) {
                        Snackbar.make(dialogBinding.root, "Error adding place: ${e.message}", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
