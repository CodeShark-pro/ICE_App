package com.example.iceapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find our UI elements by their IDs
        val etContactNumber = findViewById<EditText>(R.id.etContactNumber)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Initialize SharedPreferences to save the data
        val sharedPreferences = getSharedPreferences("ICE_PREFS", Context.MODE_PRIVATE)

        // Load the saved number when the app opens (if one exists)
        val savedNumber = sharedPreferences.getString("CONTACT_NUMBER", "")
        etContactNumber.setText(savedNumber)

        // What happens when the user clicks "Save"
        btnSave.setOnClickListener {
            val number = etContactNumber.text.toString().trim()

            // Checks if the string is exactly 10 digits long (standard mobile format)
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