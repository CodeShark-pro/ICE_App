package com.example.iceapp

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Keep the permissions logic on the main screen
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsGranted = permissions[Manifest.permission.SEND_SMS] ?: false
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val callGranted = permissions[Manifest.permission.CALL_PHONE] ?: false

        if (!smsGranted || !locationGranted || !callGranted) {
            Toast.makeText(this, "Permissions are required for the app to function.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request permissions on startup
        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CALL_PHONE
            )
        )

        val btnMainSOS = findViewById<Button>(R.id.btnMainSOS)
        val btnManageContacts = findViewById<Button>(R.id.btnManageContacts)

        // 1. The in-app SOS button uses the exact same engine as our widget!
        btnMainSOS.setOnClickListener {
            Toast.makeText(this, "Initiating Emergency Protocol...", Toast.LENGTH_SHORT).show()
            EmergencyAction.triggerSOS(this)
        }

        // 2. The Navigation Intent (Moves to the Contacts page)
        btnManageContacts.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }
    }
}