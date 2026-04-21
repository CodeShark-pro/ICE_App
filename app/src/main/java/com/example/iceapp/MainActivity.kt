package com.example.iceapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    // Our list of contacts and the adapter that feeds them to the UI
    private val contactList = mutableListOf<Contact>()
    private lateinit var adapter: ContactAdapter

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

        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CALL_PHONE
            )
        )

        val etDesignation = findViewById<EditText>(R.id.etDesignation)
        val etContactNumber = findViewById<EditText>(R.id.etContactNumber)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewContacts)

        // 1. Set up the RecyclerView
        adapter = ContactAdapter(contactList) { position ->
            // Delete logic: Remove from list, update UI, save new list
            contactList.removeAt(position)
            adapter.notifyItemRemoved(position)
            saveContactsToMemory()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 2. Load existing contacts when app opens
        loadContactsFromMemory()

        // 3. Save Button Logic
        btnSave.setOnClickListener {
            val designation = etDesignation.text.toString().trim()
            val number = etContactNumber.text.toString().trim()

            if (designation.isNotEmpty() && number.length == 10 && number.all { it.isDigit() }) {
                // Add to list and update UI
                contactList.add(Contact(designation, number))
                adapter.notifyItemInserted(contactList.size - 1)

                // Save to phone memory
                saveContactsToMemory()

                // Clear input fields
                etDesignation.text.clear()
                etContactNumber.text.clear()
                Toast.makeText(this, "Contact Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid designation and 10-digit number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- MEMORY HELPER FUNCTIONS ---

    private fun saveContactsToMemory() {
        val sharedPreferences = getSharedPreferences("ICE_PREFS", Context.MODE_PRIVATE)
        val jsonArray = JSONArray()

        for (contact in contactList) {
            val jsonObject = JSONObject()
            jsonObject.put("designation", contact.designation)
            jsonObject.put("number", contact.number)
            jsonArray.put(jsonObject)
        }

        sharedPreferences.edit().putString("CONTACTS_JSON", jsonArray.toString()).apply()
    }

    private fun loadContactsFromMemory() {
        val sharedPreferences = getSharedPreferences("ICE_PREFS", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("CONTACTS_JSON", "[]")

        contactList.clear()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val designation = jsonObject.getString("designation")
                val number = jsonObject.getString("number")
                contactList.add(Contact(designation, number))
            }
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}