package com.example.elderhelpprototypev01.model

/**
 * UserProfile data model representing user identity and emergency contact details.
 */
data class UserProfile(
    val fullName: String = "Ramesh Sharma",
    val age: String = "68",
    val contactNumber: String = "+91 98765 12345",
    val emergencyContactName: String = "Rahul (Son)",
    val emergencyContactPhone: String = "+91 98765 43210",
    val address: String = "Bandra West, Mumbai"
)
