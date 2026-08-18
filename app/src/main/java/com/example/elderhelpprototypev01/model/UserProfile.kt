package com.example.elderhelpprototypev01.model

/**
 * Explicit emergency contact record with separate name, relationship, and phone.
 */
data class EmergencyContact(
    val name: String = "Rahul",
    val relationship: String = "Son",
    val phone: String = "+91 98765 43210"
) {
    /**
     * Checks if this emergency contact matches a query by name or relationship.
     * Supports both English and Hindi relationship terms.
     */
    fun matchesQuery(query: String): Boolean {
        val q = query.lowercase().trim()
        val nameLower = name.lowercase().trim()
        val relLower = relationship.lowercase().trim()

        if (nameLower.isNotBlank() && (q.contains(nameLower) || nameLower.contains(q))) {
            return true
        }

        if (relLower.isNotBlank() && (q.contains(relLower) || relLower.contains(q))) {
            return true
        }

        // Multilingual relationship synonym dictionary (English, Hinglish, Hindi)
        val synonyms = when (relLower) {
            "son" -> listOf("son", "boy", "beta", "बेटा", "पुत्र", "लड़का", "rahul")
            "daughter" -> listOf("daughter", "girl", "beti", "बेटी", "पुत्री", "लड़की")
            "husband" -> listOf("husband", "pati", "पति", "spouse", "partner")
            "wife" -> listOf("wife", "patni", "पत्नी", "biwi", "बीवी", "spouse", "partner")
            "doctor" -> listOf("doctor", "dr", "dr.", "डॉक्टर", "वैद्य", "physician")
            "father" -> listOf("father", "dad", "papa", "पिता", "पापा", "पिताजी", "बाबूजी")
            "mother" -> listOf("mother", "mom", "maa", "माता", "माँ", "माताजी", "मम्मी")
            "brother" -> listOf("brother", "bhai", "भाई", "भैया")
            "sister" -> listOf("sister", "behen", "didi", "दीदी", "बहन")
            "caregiver" -> listOf("caregiver", "nurse", "सहायक", "केयरगिवर", "attendant")
            "friend" -> listOf("friend", "dost", "मित्र", "दोस्त")
            else -> listOf(relLower)
        }

        return synonyms.any { q.contains(it) }
    }
}

/**
 * UserProfile data model representing user identity and emergency contact details.
 *
 * Separates emergency contact records into explicit fields:
 * [emergencyContactName], [emergencyContactRelationship], and [emergencyContactPhone].
 */
data class UserProfile(
    val fullName: String = "Ramesh Sharma",
    val age: String = "68",
    val contactNumber: String = "+91 98765 12345",
    val emergencyContactName: String = "Rahul",
    val emergencyContactRelationship: String = "Son",
    val emergencyContactPhone: String = "+91 98765 43210",
    val address: String = "Bandra West, Mumbai",
    val emergencyContacts: List<EmergencyContact> = listOf(
        EmergencyContact(name = "Rahul", relationship = "Son", phone = "+91 98765 43210")
    )
) {
    /**
     * Primary emergency contact accessor.
     */
    val primaryContact: EmergencyContact
        get() = emergencyContacts.firstOrNull() ?: EmergencyContact(
            name = emergencyContactName,
            relationship = emergencyContactRelationship,
            phone = emergencyContactPhone
        )

    /**
     * Formatted label showing Name and Relationship e.g. "Rahul (Son)".
     */
    val emergencyContactDisplayName: String
        get() = if (emergencyContactRelationship.isNotBlank()) {
            "$emergencyContactName ($emergencyContactRelationship)"
        } else {
            emergencyContactName
        }

    /**
     * Finds an emergency contact matching a spoken name, relationship, or query.
     */
    fun findEmergencyContact(query: String): EmergencyContact? {
        val q = query.lowercase().trim()

        // 1. Direct match in list
        val matchedInList = emergencyContacts.firstOrNull { it.matchesQuery(q) }
        if (matchedInList != null) return matchedInList

        // 2. Direct match on primary
        if (primaryContact.matchesQuery(q)) return primaryContact

        // 3. General emergency keywords match primary contact
        val isEmergencyIntent = q.contains("emergency") || q.contains("sos") ||
                q.contains("urgent") || q.contains("help") ||
                q.contains("आपातकाल") || q.contains("मदद") ||
                q.contains("इमरजेंसी") || q.contains("सहायता") ||
                q.contains("call contact") || q.contains("संपर्क को कॉल")

        if (isEmergencyIntent) {
            return primaryContact
        }

        return null
    }
}
