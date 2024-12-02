package com.example.finalproject.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.finalproject.activities.adapters.SpeciesAdapter
import com.example.finalproject.dao.SpecieDAO
import com.example.finalproject.database.AppDatabase
import com.example.finalproject.databinding.ActivitySpeciesBinding
import com.example.finalproject.databinding.DialogAddSpeciesBinding
import com.example.finalproject.models.Specie
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SpeciesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpeciesBinding
    private lateinit var specieDao: SpecieDAO
    private lateinit var db : AppDatabase
    private lateinit var adapter: SpeciesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivitySpeciesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-db"
        )
            .fallbackToDestructiveMigration()
            .build()

        specieDao = db.specieDao()

        adapter = SpeciesAdapter(
            onEdit = {specie ->
                showEditDialog(specie)
            },
            onDelete = {specie ->
                showDeleteSpecieDialog(specie)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


       refreshSpeciesList()

        binding.add.setOnClickListener {
            showAddSpecieDialog()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun refreshSpeciesList() {
        lifecycleScope.launch {
            val speciesList = db.specieDao().getAllSpecies()
            adapter.submitList(speciesList)
        }
    }

    private fun showEditDialog(specie: Specie) {
        val dialogBinding = DialogAddSpeciesBinding.inflate(layoutInflater)

        dialogBinding.specieName.setText(specie.name)
        dialogBinding.specieDescription.setText(specie.description)
        dialogBinding.specieFrequency.setText(specie.waterFrequency.toString())
        dialogBinding.specieInstructions.setText(specie.careInstructions)

        AlertDialog.Builder(this)
            .setTitle("Edit Specie")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val updatedSpecie = specie.copy(
                    name = dialogBinding.specieName.text.toString(),
                    description = dialogBinding.specieDescription.text.toString(),
                    waterFrequency = dialogBinding.specieFrequency.text.toString().toInt(),
                    careInstructions = dialogBinding.specieInstructions.text.toString()
                )
                lifecycleScope.launch {
                    db.specieDao().updateSpecie(updatedSpecie)
                    refreshSpeciesList()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()

    }

    private fun showDeleteSpecieDialog(specie: Specie) {
        AlertDialog.Builder(this)
            .setTitle("Delete Specie")
            .setMessage("Are you sure you want to delete ${specie.name}?")
            .setPositiveButton("Delete") { _,_ ->
                lifecycleScope.launch {
                    specieDao.deleteSpecie(specie)
                }
                refreshSpeciesList()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun showAddSpecieDialog() {
        val dialogBinding = DialogAddSpeciesBinding.inflate(layoutInflater)

        AlertDialog.Builder(this)
            .setTitle("Add Specie")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.specieName.text.toString().trim()
                val description = dialogBinding.specieDescription.text.toString()
                val waterFrequencyStr = dialogBinding.specieFrequency.text.toString()
                val careInstructions = dialogBinding.specieInstructions.text.toString()

                if (name.isEmpty()) {
                    Snackbar.make(dialogBinding.root, "Name cannot be empty", Snackbar.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val waterFrequency = waterFrequencyStr.toIntOrNull() ?: 0

                lifecycleScope.launch {
                    try {
                        val newSpecie = Specie(name = name, description = description, waterFrequency = waterFrequency, careInstructions = careInstructions)
                        specieDao.insertSpecie(newSpecie)
                        adapter.submitList(specieDao.getAllSpecies())

                    } catch (e: Exception) {
                        Snackbar.make(dialogBinding.root, "Error adding specie: ${e.message}", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

