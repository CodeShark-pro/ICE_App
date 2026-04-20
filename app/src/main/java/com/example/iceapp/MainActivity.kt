package com.example.iceapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsGranted = permissions[Manifest.permission.SEND_SMS] ?: false
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val callGranted = permissions[Manifest.permission.CALL_PHONE] ?: false

        if (!smsGranted || !locationGranted || !callGranted) {
            Toast.makeText(this, "ICE App requires permissions to function properly.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ask for permissions as soon as the app opens
        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CALL_PHONE
            )
        )

        val etContactNumber = findViewById<EditText>(R.id.etContactNumber)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val sharedPreferences = getSharedPreferences("ICE_PREFS", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getString("CONTACT_NUMBER", "")
        etContactNumber.setText(savedNumber)

        btnSave.setOnClickListener {
            val number = etContactNumber.text.toString().trim()

            if (number.length == 10 && number.all { it.isDigit() }) {
                val editor = sharedPreferences.edit()
                editor.putString("CONTACT_NUMBER", number)
                editor.apply()

                Toast.makeText(this, "Contact Saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid 10-digit number", Toast.LENGTH_SHORT).show()
            }
        }
    }
}