package com.example.finalproject.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.R
import com.example.finalproject.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.btnPlaces.setOnClickListener {
            startActivity(Intent(this, PlacesActivity::class.java))
        }

        binding.btnPlants.setOnClickListener {
            startActivity(Intent(this, PlantsActivity::class.java))
        }

        binding.btnSpecies.setOnClickListener {
            startActivity(Intent(this, SpeciesActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Define the channel ID, name, and importance
            val channelId = "watering_channel"
            val channelName = "Watering Reminders"
            val channelDescription = "Notifications to remind you to water your plants"
            val importance = NotificationManager.IMPORTANCE_HIGH

            // Create the channel
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            // Register the channel with the system
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
