package com.example.elderhelpprototypev01.model

enum class BillType(
    val title: String,
    val iconEmoji: String,
    val fieldLabel: String,
    val defaultProvider: String,
    val sampleIdentifier: String,
    val defaultAmount: String
) {
    ELECTRICITY(
        title = "Electricity Bill",
        iconEmoji = "⚡",
        fieldLabel = "Consumer / Account ID",
        defaultProvider = "Adani Electricity",
        sampleIdentifier = "102938475",
        defaultAmount = "1450"
    ),
    WATER(
        title = "Water Bill",
        iconEmoji = "💧",
        fieldLabel = "Consumer / Connection No.",
        defaultProvider = "Municipal Corporation Water Board",
        sampleIdentifier = "WB-8839201",
        defaultAmount = "480"
    ),
    MOBILE(
        title = "Mobile Recharge",
        iconEmoji = "📱",
        fieldLabel = "10-Digit Mobile Number",
        defaultProvider = "Jio Prepaid",
        sampleIdentifier = "9876512345",
        defaultAmount = "299"
    )
}

data class BillPayment(
    val id: String,
    val type: BillType,
    val identifier: String,
    val provider: String,
    val amount: String,
    val status: String = "Paid Successfully",
    val timestamp: String
)
