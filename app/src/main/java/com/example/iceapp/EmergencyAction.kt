package com.example.iceapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.telephony.SmsManager
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import org.json.JSONArray

object EmergencyAction {

    @SuppressLint("MissingPermission")
    fun triggerSOS(context: Context, onComplete: () -> Unit = {}) {
        val sharedPreferences = context.getSharedPreferences("ICE_PREFS", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("CONTACTS_JSON", "[]")

        val phoneNumbers = mutableListOf<String>()

        // 1. Extract all numbers from the saved JSON array
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                phoneNumbers.add(jsonObject.getString("number"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (phoneNumbers.isEmpty()) {
            Toast.makeText(context, "No emergency contacts saved!", Toast.LENGTH_LONG).show()
            onComplete()
            return
        }

        // 2. Fetch Location and Trigger Actions
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            val message: String
            if (location != null) {
                val mapLink = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
                message = "EMERGENCY! I need help. My location: $mapLink"
            } else {
                message = "EMERGENCY! I need help, but my GPS signal is currently lost."
            }

            // Send SMS to EVERYONE on the list
            for (number in phoneNumbers) {
                sendSms(context, number, message)
            }

            // Call the FIRST contact on the list
            makePhoneCall(context, phoneNumbers[0])

            onComplete()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to get location.", Toast.LENGTH_SHORT).show()

            // Still attempt to call the primary contact even if location fails
            makePhoneCall(context, phoneNumbers[0])
            onComplete()
        }
    }

    private fun sendSms(context: Context, phoneNumber: String, message: String) {
        try {
            val smsManager = context.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun makePhoneCall(context: Context, phoneNumber: String) {
        try {
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(callIntent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}