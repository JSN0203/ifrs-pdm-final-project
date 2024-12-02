package com.example.finalproject.activities

import android.Manifest
import android.R
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.finalproject.database.AppDatabase
import com.example.finalproject.databinding.ActivityPlantDetailBinding
import com.example.finalproject.databinding.DialogAddPlantBinding
import com.example.finalproject.models.Plant
import com.example.finalproject.utils.NotificationReceiver
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlantDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlantDetailBinding
    private lateinit var db: AppDatabase
    private var plantId: Int = 0
    private lateinit var plant: Plant

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityPlantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-db"
        )
            .fallbackToDestructiveMigration()
            .build()

        plantId = intent.getIntExtra("plantId", 0)

        //Teste de log
        /* checkAndRequestNotificationPermission {
            scheduleNotification("Teste", 10000)
            Log.d("notification", "Test started")
        } */


        loadPlantDetails()

        binding.edit.setOnClickListener { showEditDialog() }
        binding.delete.setOnClickListener { showDeleteDialog() }
        binding.water.setOnClickListener {
            checkAndRequestNotificationPermission {
                registerWatering()
            }
        }
    }

    private fun loadPlantDetails() {
        lifecycleScope.launch {
            plant = db.plantDao().getPlantById(plantId)
            val specie = db.specieDao().getSpecieById(plant.specieId)
            val place = db.placeDao().getPlaceById(plant.placeId)

            binding.nickname.text = plant.nickname
            binding.specie.text = specie.name
            binding.place.text = place.name
            binding.notes.text = plant.notes ?: "No notes"

            // Exibir a última rega
            val lastWateringTime = plant.lastWatering
            if (lastWateringTime != null) {
                val lastWateringDate = Date(lastWateringTime)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                binding.lastWatering.text = "Last watered on: ${dateFormat.format(lastWateringDate)}"
            } else {
                binding.lastWatering.text = "Last watered on: Not recorded"
            }

            // Exibir o tempo restante para a próxima rega
            val nextWateringTime = plant.lastWatering?.let { it + specie.waterFrequency * 24 * 60 * 60 * 1000 }

            //Check de tempo
            /*
            Log.d("Watering", "lastWatering: ${plant.lastWatering}")
            Log.d("Watering", "specie.waterFrequency: ${specie.waterFrequency}")
            Log.d("Watering", "nextWateringTime: $nextWateringTime")
             */

            val currentTime = System.currentTimeMillis()
            if (nextWateringTime != null && nextWateringTime > currentTime) {
                val timeRemaining = nextWateringTime - currentTime
                val days = timeRemaining / (24 * 60 * 60 * 1000)
                val hours = (timeRemaining % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
                val minutes = (timeRemaining % (60 * 60 * 1000)) / (60 * 1000)

                binding.timeUntilNext.text =
                    "Next watering in: ${days}d ${hours}h ${minutes}m"
            } else {
                binding.timeUntilNext.text = "Next watering in: Overdue!"
            }
        }
    }


    private fun registerWatering() {
        lifecycleScope.launch {
            val specie = db.specieDao().getSpecieById(plant.specieId)
            val nextWateringTime = System.currentTimeMillis() + specie.waterFrequency * 24 * 60 * 60 * 1000

            val updatedPlant = plant.copy(lastWatering = System.currentTimeMillis())
            db.plantDao().updatePlant(updatedPlant)
            scheduleNotification(updatedPlant.nickname, nextWateringTime)
            Snackbar.make(binding.root, "Watering registered!", Snackbar.LENGTH_SHORT).show()
            loadPlantDetails()
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }


    private fun scheduleNotification(plantName: String, timeInMillis: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                requestExactAlarmPermission()
                return
            }
        }

        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("plantName", plantName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }


    private fun showEditDialog() {
        val dialogBinding = DialogAddPlantBinding.inflate(layoutInflater)

        lifecycleScope.launch {
            val speciesList = db.specieDao().getAllSpecies()
            val placesList = db.placeDao().getAllPlaces()

            val specieAdapter = ArrayAdapter(
                this@PlantDetailActivity,
                R.layout.simple_spinner_item,
                speciesList.map { it.name }
            )
            specieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            val placeAdapter = ArrayAdapter(
                this@PlantDetailActivity,
                android.R.layout.simple_spinner_item,
                placesList.map { it.name }
            )
            placeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            dialogBinding.specieSpinner.adapter = specieAdapter
            dialogBinding.placeSpinner.adapter = placeAdapter

            val currentSpecie = speciesList.firstOrNull { it.id == plant.specieId }
            val currentPlace = placesList.firstOrNull { it.id == plant.placeId }

            dialogBinding.specieSpinner.setSelection(speciesList.indexOf(currentSpecie))
            dialogBinding.placeSpinner.setSelection(placesList.indexOf(currentPlace))

            dialogBinding.nickname.setText(plant.nickname)
            dialogBinding.notes.setText(plant.notes)

            AlertDialog.Builder(this@PlantDetailActivity)
                .setTitle("Edit Plant")
                .setView(dialogBinding.root)
                .setPositiveButton("Save") { _, _ ->
                    val nickname = dialogBinding.nickname.text.toString()
                    val specieName = dialogBinding.specieSpinner.selectedItem.toString()
                    val placeName = dialogBinding.placeSpinner.selectedItem.toString()
                    val notes = dialogBinding.notes.text.toString()

                    if (nickname.isBlank() || specieName.isBlank() || placeName.isBlank()) {
                        Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val specieId = speciesList.first { it.name == specieName }.id
                    val placeId = placesList.first { it.name == placeName }.id

                    val updatedPlant = plant.copy(
                        nickname = nickname,
                        specieId = specieId,
                        placeId = placeId,
                        notes = notes
                    )

                    lifecycleScope.launch {
                        db.plantDao().updatePlant(updatedPlant)
                        Snackbar.make(binding.root, "Plant updated successfully!", Snackbar.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }


    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Plant")
            .setMessage("Are you sure you want to delete ${plant.nickname}?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    db.plantDao().deletePlant(plant)
                    Snackbar.make(binding.root, "Plant deleted!", Snackbar.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkAndRequestNotificationPermission(action: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            } else {
                action()
            }
        } else {
            action()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
