package com.example.elderhelpprototypev01.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat

/**
 * EmergencyCallHelper
 *
 * Utility to execute direct emergency phone calls using Intent.ACTION_CALL
 * when CALL_PHONE permission is granted, and graceful fallback to Intent.ACTION_DIAL.
 */
object EmergencyCallHelper {

    fun hasCallPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun makeCall(context: Context, phoneNumber: String) {
        val cleanNumber = phoneNumber.replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        if (cleanNumber.isBlank()) {
            Toast.makeText(context, "No phone number specified", Toast.LENGTH_SHORT).show()
            return
        }

        if (hasCallPermission(context)) {
            try {
                val callIntent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:$cleanNumber")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(callIntent)
                Toast.makeText(context, "Connecting emergency call to $cleanNumber...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Fallback to dialer if direct call fails
                dialFallback(context, cleanNumber)
            }
        } else {
            // Fallback to dialer if permission not granted
            dialFallback(context, cleanNumber)
        }
    }

    private fun dialFallback(context: Context, cleanNumber: String) {
        try {
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(dialIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to place call: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
