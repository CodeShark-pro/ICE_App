package com.example.iceapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
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
                // Format a standard Google Maps link
                val mapLink = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
                val message = "EMERGENCY! I need help. My location: $mapLink"

                // 4. Send the SMS and Trigger the Call
                sendSms(context, contactNumber, message)
                makePhoneCall(context, contactNumber)
            } else {
                // Fallback if GPS is temporarily unavailable
                val fallbackMessage = "EMERGENCY! I need help, but my GPS signal is currently lost."
                sendSms(context, contactNumber, fallbackMessage)
                makePhoneCall(context, contactNumber)
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to get location.", Toast.LENGTH_SHORT).show()
            // Still attempt to call even if the location service completely fails
            makePhoneCall(context, contactNumber)
        }
    }

    private fun sendSms(context: Context, phoneNumber: String, message: String) {
        try {
            // Using the recommended SmsManager approach for modern APIs
            val smsManager = context.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(context, "Emergency SMS Sent", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "SMS Failed: Check permissions", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun makePhoneCall(context: Context, phoneNumber: String) {
        try {
            // ACTION_CALL dials immediately.
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
                // FLAG_ACTIVITY_NEW_TASK is required because we are launching this
                // from a background widget (BroadcastReceiver), not a standard Activity UI.
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(callIntent)
        } catch (e: SecurityException) {
            Toast.makeText(context, "Call Permission Missing", Toast.LENGTH_SHORT).show()
        }
    }
}