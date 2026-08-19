package com.example.elderhelpprototypev01.model

import androidx.compose.ui.graphics.Color

enum class TransactionCategory(
    val title: String,
    val iconEmoji: String,
    val defaultProvider: String,
    val badgeBgColor: Color = Color(0xFFE8F5E9),
    val badgeTextColor: Color = Color(0xFF2E7D32)
) {
    ELECTRICITY(
        title = "Electricity",
        iconEmoji = "⚡",
        defaultProvider = "Adani Electricity"
    ),
    WATER(
        title = "Water",
        iconEmoji = "💧",
        defaultProvider = "Municipal Corporation Water Board"
    ),
    MOBILE(
        title = "Mobile Recharge",
        iconEmoji = "📱",
        defaultProvider = "Jio Prepaid"
    ),
    GAS(
        title = "Gas Bill",
        iconEmoji = "🔥",
        defaultProvider = "Mahanagar Gas",
        badgeBgColor = Color(0xFFFFF3E0),
        badgeTextColor = Color(0xFFE65100)
    ),
    DOCTOR(
        title = "Doctor Booking",
        iconEmoji = "🩺",
        defaultProvider = "Bandra Medical Clinic"
    )
}

data class TransactionRecord(
    val id: String,
    val category: TransactionCategory,
    val provider: String,
    val identifier: String,
    val amount: String,
    val status: String = "Successful",
    val timestamp: String,
    val isVoicePayment: Boolean = false,
    val billType: BillType? = null,
    val doctorSpecialty: String? = null,
    /** True for doctor appointment bookings — hides ₹ amount, shows "Appointment Booked" badge. */
    val isAppointment: Boolean = false,
    /** Doctor's name for appointment cards (e.g. "Dr. Amit Joshi"). */
    val doctorName: String? = null,
    /** Formatted date+time string for appointment cards. */
    val appointmentDateTime: String? = null
) {
    val accountOrPhone: String get() = identifier
}

