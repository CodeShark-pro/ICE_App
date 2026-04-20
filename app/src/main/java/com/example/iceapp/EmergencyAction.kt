package com.example.iceapp

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.telephony.SmsManager
import android.widget.Toast
import com.google.android.gms.location.LocationServices

object EmergencyAction {

    @SuppressLint("MissingPermission") // Permissions are requested in Manifest and checked before calling
    fun triggerSOS(context: Context) {
        // 1. Retrieve the saved contact number
        val sharedPreferences = context.getSharedPreferences("ICE_PREFS", Context.MODE_PRIVATE)
        val contactNumber = sharedPreferences.getString("CONTACT_NUMBER", "")

        if (contactNumber.isNullOrEmpty()) {
            Toast.makeText(context, "No emergency contact saved!", Toast.LENGTH_LONG).show()
            return
        }

        // 2. Initialize the Location Client
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // 3. Fetch the last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Format the Google Maps link
                val mapLink = "https://maps.google.com/?q=$${location.latitude},${location.longitude}"
                val message = "EMERGENCY! I need help. My location: $mapLink"

                // 4. Send the SMS
                sendSms(context, contactNumber, message)
            } else {
                // Fallback if GPS is temporarily unavailable
                val fallbackMessage = "EMERGENCY! I need help, but my GPS signal is currently lost."
                sendSms(context, contactNumber, fallbackMessage)
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to get location.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSms(context: Context, phoneNumber: String, message: String) {
        try {
            // Using the recommended SmsManager approach for modern APIs
            val smsManager = context.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(context, "Emergency SMS Sent in Background", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "SMS Failed: Check permissions", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}