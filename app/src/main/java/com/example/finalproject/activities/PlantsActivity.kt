package com.example.finalproject.activities


import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.finalproject.activities.adapters.PlantAdapter
import com.example.finalproject.database.AppDatabase
import com.example.finalproject.databinding.ActivityPlantsBinding
import com.example.finalproject.databinding.DialogAddPlantBinding
import com.example.finalproject.models.Plant
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PlantsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlantsBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: PlantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityPlantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-db"
        )
            .fallbackToDestructiveMigration()
            .build()

        adapter = PlantAdapter(onClick = { plant ->
            val intent = Intent(this, PlantDetailActivity::class.java)
            intent.putExtra("plantId", plant.id)
            startActivity(intent)
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.addPlant.setOnClickListener {
            showAddPlantDialog()
        }
    }

    private fun showAddPlantDialog() {
        val dialogBinding = DialogAddPlantBinding.inflate(layoutInflater)

        // Carregar species e places do banco de dados
        lifecycleScope.launch {
            val speciesList = db.specieDao().getAllSpecies() // Obter todas as especies
            val placesList = db.placeDao().getAllPlaces() // Obter todos os lugares

            // Criar adaptadores para o Spinner
            val specieAdapter = ArrayAdapter(
                this@PlantsActivity,
                android.R.layout.simple_spinner_item,
                speciesList.map { it.name }
            )
            specieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            val placeAdapter = ArrayAdapter(
                this@PlantsActivity,
                android.R.layout.simple_spinner_item,
                placesList.map { it.name }
            )
            placeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Atribuir adaptadores aos spinners
            dialogBinding.specieSpinner.adapter = specieAdapter
            dialogBinding.placeSpinner.adapter = placeAdapter

            // Exibir o diálogo
            AlertDialog.Builder(this@PlantsActivity)
                .setTitle("Add Plant")
                .setView(dialogBinding.root)
                .setPositiveButton("Save") { _, _ ->
                    val nickname = dialogBinding.nickname.text.toString()
                    val specieName = dialogBinding.specieSpinner.selectedItem.toString()
                    val placeName = dialogBinding.placeSpinner.selectedItem.toString()
                    val notes = dialogBinding.notes.text.toString()

                    // Validar entrada
                    if (nickname.isBlank() || specieName.isBlank() || placeName.isBlank()) {
                        Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    // Encontrar o id da specie e place selecionados
                    val specieId = speciesList.first { it.name == specieName }.id
                    val placeId = placesList.first { it.name == placeName }.id

                    // Criar a nova planta
                    val newPlant = Plant(
                        nickname = nickname,
                        specieId = specieId,
                        placeId = placeId,
                        lastWatering = null,
                        notes = notes
                    )

                    // Salvar no banco de dados
                    lifecycleScope.launch {
                        db.plantDao().insertPlant(newPlant)
                        Snackbar.make(binding.root, "Plant added successfully!", Snackbar.LENGTH_SHORT).show()
                        refreshPlantList() // Atualiza a lista de plantas
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun refreshPlantList() {
        lifecycleScope.launch {
            val plants = db.plantDao().getAllPlants()
            adapter.submitList(plants)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshPlantList()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
