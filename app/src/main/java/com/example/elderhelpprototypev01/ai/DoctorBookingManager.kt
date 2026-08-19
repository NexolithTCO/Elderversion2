package com.example.elderhelpprototypev01.ai

import com.example.elderhelpprototypev01.model.AssistantResponse
import com.example.elderhelpprototypev01.model.ConversationMessage
import com.example.elderhelpprototypev01.model.MessageRole
import java.util.Locale

/**
 * DoctorBookingManager
 *
 * Dedicated manager for the Doctor Booking conversation flow.
 * Handles slot extraction and provides deterministic, voice-friendly
 * step-by-step guidance following the exact 5-step flow:
 *
 * 1. Location Prompt (City or Locality)
 * 2. Specialty Selection (General Physician, Cardiologist, Orthopedic, Ophthalmologist, Neurologist)
 * 3. Interactive Doctor Selection Cards (3 specialists per category with degree)
 * 4. Date & Time Selection
 * 5. Booking Confirmation & Session Continuity
 */
object DoctorBookingManager {

    data class DoctorEntry(
        val nameEn: String,
        val qualificationsEn: String,
        val nameHi: String,
        val qualificationsHi: String
    )

    data class SpecialtyEntry(
        val key: String,
        val nameEn: String,
        val nameHi: String,
        val doctors: List<DoctorEntry>
    )

    val DOCTOR_DATABASE = listOf(
        SpecialtyEntry(
            key = "General Physician",
            nameEn = "General Physician",
            nameHi = "सामान्य डॉक्टर",
            doctors = listOf(
                DoctorEntry(
                    "Dr. Rajesh Sharma", "MBBS, MD (Internal Medicine)",
                    "डॉ. राजेश शर्मा", "एमबीबीएस, एमडी (इंटरनल मेडिसिन)"
                ),
                DoctorEntry(
                    "Dr. Sunita Patil", "MBBS (Family Physician)",
                    "डॉ. सुनिता पाटिल", "एमबीबीएस (फैमिली फिजिशियन)"
                ),
                DoctorEntry(
                    "Dr. Anil Verma", "MBBS (Senior Consultant)",
                    "डॉ. अनिल वर्मा", "एमबीबीएस (सीनियर कंसल्टेंट)"
                )
            )
        ),
        SpecialtyEntry(
            key = "Cardiologist",
            nameEn = "Cardiologist",
            nameHi = "हृदय रोग विशेषज्ञ",
            doctors = listOf(
                DoctorEntry(
                    "Dr. Priya Mehta", "MD, DM (Cardiology)",
                    "डॉ. प्रिया मेहता", "एमडी, डीएम (कार्डियोलॉजी)"
                ),
                DoctorEntry(
                    "Dr. Suresh Nair", "MBBS, MCh (Cardio)",
                    "डॉ. सुरेश नायर", "एमबीबीएस, एम्ची (कार्डियो)"
                ),
                DoctorEntry(
                    "Dr. Ananya Das", "MD, DNB (Cardiology)",
                    "डॉ. अनन्या दास", "एमडी, डीएनबी (कार्डियोलॉजी)"
                )
            )
        ),
        SpecialtyEntry(
            key = "Orthopedic",
            nameEn = "Orthopedic",
            nameHi = "हड्डी रोग विशेषज्ञ",
            doctors = listOf(
                DoctorEntry(
                    "Dr. Vikram Joshi", "MS (Orthopedics)",
                    "डॉ. विक्रम जोशी", "एमएस (ऑर्थोपेडिक्स)"
                ),
                DoctorEntry(
                    "Dr. Ramesh Kulkarni", "MBBS, D.Ortho",
                    "डॉ. रमेश कुलकर्णी", "एमबीबीएस, डी.ऑर्थो"
                ),
                DoctorEntry(
                    "Dr. Neha Gupta", "MS (Joint Replacement Specialist)",
                    "डॉ. नेहा गुप्ता", "एमएस (जोइंट रिप्लेसमेंट स्पेशलिस्ट)"
                )
            )
        ),
        SpecialtyEntry(
            key = "Ophthalmologist",
            nameEn = "Ophthalmologist",
            nameHi = "नेत्र रोग विशेषज्ञ",
            doctors = listOf(
                DoctorEntry(
                    "Dr. Sanjay Rao", "MS (Ophthalmology)",
                    "डॉ. संजय राव", "एमएस (ऑप्थैल्मोलॉजी)"
                ),
                DoctorEntry(
                    "Dr. Meera Deshmukh", "DOMS (Eye Specialist)",
                    "डॉ. मीरा देशमुख", "डीओएमएस (आई स्पेशलिस्ट)"
                ),
                DoctorEntry(
                    "Dr. Amit Shah", "MD (Ophthalmic Surgeon)",
                    "डॉ. अमित शाह", "एमडी (ऑप्थैल्मिक सर्जन)"
                )
            )
        ),
        SpecialtyEntry(
            key = "Neurologist",
            nameEn = "Neurologist",
            nameHi = "न्यूरोलॉजिस्ट",
            doctors = listOf(
                DoctorEntry(
                    "Dr. Rohan Kapoor", "DM (Neurology)",
                    "डॉ. रोहन कपूर", "डीएम (न्यूरोलॉजी)"
                ),
                DoctorEntry(
                    "Dr. Kavita Iyer", "MD, DNB (Neurology)",
                    "डॉ. कविता अय्यर", "एमडी, डीएनबी (न्यूरोलॉजी)"
                ),
                DoctorEntry(
                    "Dr. Alok Pandey", "MBBS, DM (Neuro)",
                    "डॉ. आलोक पांडे", "एमबीबीएस, डीएम (न्यूरो)"
                )
            )
        )
    )

    data class BookingState(
        var specialty: String? = null,
        var location: String? = null,
        var doctorName: String? = null,
        var dateTime: String? = null,
        var mode: String? = "In-person clinic visit",
        var isConfirmed: Boolean = false,
        var isCancelled: Boolean = false
    )

    private val DOCTOR_INTENT_KEYWORDS = listOf(
        // English keywords
        "doctor", "appointment", "book a doctor", "see a doctor",
        "consult a doctor", "medical appointment", "physician",
        "general physician", "cardiologist", "orthopedic", "ophthalmologist",
        "neurologist", "eye specialist", "heart doctor", "bone doctor",
        "dr", "dr.",
        // Hindi keywords
        "डॉक्टर", "अपॉइंटमेंट", "डॉक्टर बुक", "डॉक्टर से मिलना",
        "सामान्य डॉक्टर", "हृदय रोग विशेषज्ञ", "हड्डी रोग विशेषज्ञ",
        "नेत्र रोग विशेषज्ञ", "न्यूरोलॉजिस्ट", "मस्तिष्क विशेषज्ञ",
        "इलाज", "परामर्श"
    )

    private val CANCELLATION_TRIGGERS = listOf(
        "cancel", "stop", "abort", "don't book", "dont book", "go back", "nevermind", "never mind", "stop payment",
        "रद्द करो", "रद्द करें", "बंद करो", "पेमेंट रोकें", "बुक मत करो", "वापस जाओ", "रहने दो"
    )

    fun isCancellationIntent(transcript: String): Boolean {
        val lower = transcript.lowercase(Locale.ROOT).trim()
        return CANCELLATION_TRIGGERS.any { lower.contains(it) }
    }

    /**
     * Checks if the transcript or history indicates a doctor booking intent.
     */
    fun isDoctorBookingIntent(transcript: String, conversation: List<ConversationMessage>): Boolean {
        val lowerTranscript = transcript.lowercase(Locale.ROOT)
        if (DOCTOR_INTENT_KEYWORDS.any { lowerTranscript.contains(it) }) {
            return true
        }
        val recentMessages = conversation.takeLast(10)
        for (msg in recentMessages) {
            val textLower = msg.text.lowercase(Locale.ROOT)
            if (DOCTOR_INTENT_KEYWORDS.any { textLower.contains(it) }) {
                return true
            }
            if (textLower.contains("which city or locality are you looking to book") ||
                textLower.contains("which type of doctor do you need") ||
                textLower.contains("available general physicians") ||
                textLower.contains("available cardiologists") ||
                textLower.contains("available orthopedic specialists") ||
                textLower.contains("available eye specialists") ||
                textLower.contains("available neurologists") ||
                textLower.contains("which doctor would you prefer") ||
                textLower.contains("date and time work best") ||
                textLower.contains("booking is confirmed") ||
                textLower.contains("has been confirmed") ||
                textLower.contains("किस शहर या इलाके में अपॉइंटमेंट") ||
                textLower.contains("किस प्रकार के डॉक्टर की आवश्यकता") ||
                textLower.contains("सामान्य डॉक्टर उपलब्ध हैं") ||
                textLower.contains("हृदय रोग विशेषज्ञ उपलब्ध हैं") ||
                textLower.contains("हड्डी रोग विशेषज्ञ उपलब्ध हैं") ||
                textLower.contains("नेत्र रोग विशेषज्ञ उपलब्ध हैं") ||
                textLower.contains("न्यूरोलॉजिस्ट उपलब्ध हैं") ||
                textLower.contains("किसे चुनना चाहेंगे") ||
                textLower.contains("तारीख और समय सही रहेगा") ||
                textLower.contains("अपॉइंटमेंट कन्फर्म हो गया है") ||
                textLower.contains("सफलतापूर्वक कन्फर्म")
            ) {
                return true
            }
        }
        return false
    }

    /**
     * Reconstructs the current booking state by analyzing the full conversation + new transcript.
     */
    fun extractState(conversation: List<ConversationMessage>, currentTranscript: String = ""): BookingState {
        val state = BookingState()

        var lastQuestion = ""
        for (msg in conversation) {
            if (msg.role == MessageRole.USER) {
                if (isCancellationIntent(msg.text)) {
                    state.isCancelled = true
                } else {
                    parseUserUtterance(msg.text, lastQuestion, state)
                }
            } else {
                val assistantText = msg.text.lowercase(Locale.ROOT)
                lastQuestion = assistantText
                if (assistantText.contains("booking is confirmed") ||
                    assistantText.contains("कन्फर्म हो गया है") ||
                    assistantText.contains("has been confirmed") ||
                    assistantText.contains("सफलतापूर्वक कन्फर्म")
                ) {
                    state.isConfirmed = true
                }
                parseAssistantText(msg.text, state)
            }
        }

        if (currentTranscript.isNotBlank() && conversation.lastOrNull()?.text != currentTranscript) {
            if (isCancellationIntent(currentTranscript)) {
                state.isCancelled = true
            } else {
                parseUserUtterance(currentTranscript, lastQuestion, state)
            }
        }

        return state
    }

    private fun parseAssistantText(text: String, state: BookingState) {
        val lower = text.lowercase(Locale.ROOT)
        // Extract specialty if mentioned
        if (state.specialty == null) {
            when {
                lower.contains("general physician") || lower.contains("सामान्य डॉक्टर") -> state.specialty = "General Physician"
                lower.contains("cardiologist") || lower.contains("हृदय रोग") -> state.specialty = "Cardiologist"
                lower.contains("orthopedic") || lower.contains("हड्डी रोग") -> state.specialty = "Orthopedic"
                lower.contains("ophthalmologist") || lower.contains("eye specialist") || lower.contains("नेत्र रोग") -> state.specialty = "Ophthalmologist"
                lower.contains("neurologist") || lower.contains("न्यूरोलॉजिस्ट") || lower.contains("मस्तिष्क") -> state.specialty = "Neurologist"
            }
        }
        // Extract doctor name if mentioned
        if (state.doctorName == null) {
            for (specialty in DOCTOR_DATABASE) {
                for (doc in specialty.doctors) {
                    if (lower.contains(doc.nameEn.lowercase(Locale.ROOT)) || lower.contains(doc.nameHi.lowercase(Locale.ROOT))) {
                        state.doctorName = doc.nameEn
                        if (state.specialty == null) state.specialty = specialty.key
                        break
                    }
                }
            }
        }
    }

    private fun parseUserUtterance(text: String, lastQuestion: String, state: BookingState) {
        val lower = text.lowercase(Locale.ROOT).trim()

        // 1. Location Extraction (Step 1)
        if (state.location == null && (lastQuestion.contains("city or locality") || lastQuestion.contains("शहर या इलाके"))) {
            val cleaned = text.replace(Regex("(?i)^(in|at|near|around|i am in|i live in|book in|mein|me|में)\\s+"), "").trim()
            if (cleaned.isNotBlank() && !cleaned.contains("sharma", ignoreCase = true) && !cleaned.contains("patil", ignoreCase = true)) {
                state.location = cleaned.capitalizeWords()
            }
        } else if (state.location == null) {
            val locMatch = Regex("(?i)\\b(in|at|near|around)\\s+([A-Za-z0-9\\s]{3,30}?)(?=\\s+(for|at|on|tomorrow|today|with|dr)|$)").find(text)
            if (locMatch != null) {
                val candidate = locMatch.groupValues[2].trim()
                if (!candidate.contains("dr", ignoreCase = true) && !candidate.contains("doctor", ignoreCase = true) && candidate.length > 2) {
                    state.location = candidate.capitalizeWords()
                }
            }
        }

        // 2. Specialty Extraction (Step 2)
        if (state.specialty == null) {
            when {
                lower.contains("general physician") || lower.contains("physician") || lower.contains("general doctor") || lower.contains("normal doctor") || lower.contains("सामान्य डॉक्टर") || lower.contains("family doctor") ->
                    state.specialty = "General Physician"
                lower.contains("cardiologist") || lower.contains("cardio") || lower.contains("heart") || lower.contains("हृदय") || lower.contains("दिल") ->
                    state.specialty = "Cardiologist"
                lower.contains("orthopedic") || lower.contains("ortho") || lower.contains("bone") || lower.contains("joint") || lower.contains("हड्डी") ->
                    state.specialty = "Orthopedic"
                lower.contains("ophthalmologist") || lower.contains("eye") || lower.contains("vision") || lower.contains("नेत्र") || lower.contains("आंख") ->
                    state.specialty = "Ophthalmologist"
                lower.contains("neurologist") || lower.contains("neuro") || lower.contains("brain") || lower.contains("nerve") || lower.contains("न्यूरो") || lower.contains("मस्तिष्क") ->
                    state.specialty = "Neurologist"
                lastQuestion.contains("type of doctor") || lastQuestion.contains("किस प्रकार के डॉक्टर") -> {
                    when {
                        lower.contains("1") || lower.contains("first") || lower.contains("पहला") -> state.specialty = "General Physician"
                        lower.contains("2") || lower.contains("second") || lower.contains("दूसरा") -> state.specialty = "Cardiologist"
                        lower.contains("3") || lower.contains("third") || lower.contains("तीसरा") -> state.specialty = "Orthopedic"
                        lower.contains("4") || lower.contains("fourth") || lower.contains("चौथा") -> state.specialty = "Ophthalmologist"
                        lower.contains("5") || lower.contains("fifth") || lower.contains("पांचवा") -> state.specialty = "Neurologist"
                    }
                }
            }
        }

        // 3. Doctor Selection (Step 3)
        if (state.doctorName == null && state.specialty != null) {
            val specialtyObj = DOCTOR_DATABASE.find { it.key.equals(state.specialty, ignoreCase = true) }
            val docList = specialtyObj?.doctors ?: emptyList()

            // Check doctor names directly
            for (doc in docList) {
                val lastName = doc.nameEn.substringAfterLast(" ").lowercase(Locale.ROOT)
                val fullName = doc.nameEn.lowercase(Locale.ROOT)
                val hindiName = doc.nameHi
                if (lower.contains(fullName) || lower.contains(lastName) || lower.contains(hindiName)) {
                    state.doctorName = doc.nameEn
                    break
                }
            }

            // Check ordinal / number choices (1 / "first one", 2 / "second one", 3 / "third one")
            if (state.doctorName == null && (lastQuestion.contains("which doctor would you prefer") || lastQuestion.contains("किसे चुनना चाहेंगे") || lastQuestion.contains("available"))) {
                when {
                    lower.contains("first") || lower.contains("1") || lower.contains("one") || lower.contains("पहला") || lower.contains("एक") -> {
                        if (docList.isNotEmpty()) state.doctorName = docList[0].nameEn
                    }
                    lower.contains("second") || lower.contains("2") || lower.contains("two") || lower.contains("दूसरा") || lower.contains("दो") -> {
                        if (docList.size > 1) state.doctorName = docList[1].nameEn
                    }
                    lower.contains("third") || lower.contains("3") || lower.contains("three") || lower.contains("तीसरा") || lower.contains("तीन") -> {
                        if (docList.size > 2) state.doctorName = docList[2].nameEn
                    }
                }
            }
        }

        // 4. Date & Time Selection (Step 4)
        val timePatterns = listOf(
            Regex("(?i)\\b(tomorrow|today|tonight|next\\s+\\w+|monday|tuesday|wednesday|thursday|friday|saturday|sunday)(\\s+at\\s+\\d{1,2}(?::\\d{2})?\\s*(?:am|pm)?)?"),
            Regex("(?i)\\b\\d{1,2}(?::\\d{2})?\\s*(?:am|pm)\\b"),
            Regex("(?i)\\b\\d{1,2}(?:st|nd|rd|th)?\\s+(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*(\\s+at\\s+\\d{1,2}(?::\\d{2})?\\s*(?:am|pm)?)?"),
            Regex("(?i)\\b(morning|afternoon|evening|night|कल|आज|सुबह|शाम|दोपहर)\\b")
        )

        for (pattern in timePatterns) {
            val match = pattern.find(text)
            if (match != null) {
                state.dateTime = match.value.trim()
                break
            }
        }
        if (state.dateTime == null && (lastQuestion.contains("date and time") || lastQuestion.contains("तारीख और समय"))) {
            state.dateTime = text.trim()
        }
    }

    /**
     * Helper to retrieve qualification degree in English.
     */
    fun getDoctorDegree(enName: String?, specialty: String?): String {
        if (enName != null) {
            for (spec in DOCTOR_DATABASE) {
                for (doc in spec.doctors) {
                    if (doc.nameEn.equals(enName, ignoreCase = true)) {
                        return doc.qualificationsEn
                    }
                }
            }
        }
        return when (specialty) {
            "Cardiologist" -> "MD, DM (Cardiology)"
            "Orthopedic" -> "MS (Orthopedics)"
            "Ophthalmologist" -> "MS (Ophthalmology)"
            "Neurologist" -> "DM (Neurology)"
            else -> "MBBS, MD (Internal Medicine)"
        }
    }

    /**
     * Helper to retrieve qualification degree in Hindi.
     */
    fun getDoctorDegreeHindi(enName: String?, specialty: String?): String {
        if (enName != null) {
            for (spec in DOCTOR_DATABASE) {
                for (doc in spec.doctors) {
                    if (doc.nameEn.equals(enName, ignoreCase = true)) {
                        return doc.qualificationsHi
                    }
                }
            }
        }
        return when (specialty) {
            "Cardiologist" -> "एमडी, डीएम (कार्डियोलॉजी)"
            "Orthopedic" -> "एमएस (ऑर्थोपेडिक्स)"
            "Ophthalmologist" -> "एमएस (ऑप्थैल्मोलॉजी)"
            "Neurologist" -> "डीएम (न्यूरोलॉजी)"
            else -> "एमबीबीएस, एमडी (इंटरनल मेडिसिन)"
        }
    }

    /**
     * Generates the next question or response in the exact 5-step sequence.
     */
    fun getNextStepResponse(state: BookingState, userLanguage: String = "English"): AssistantResponse {
        val isHindi = userLanguage.contains("Hindi") || userLanguage.contains("हिंदी")
        if (state.isCancelled) {
            val cancelMsg = if (isHindi)
                "ठीक है, मैंने प्रक्रिया रद्द कर दी है। यदि आपको कुछ और चाहिए तो मुझे बताएं।"
            else
                "Okay, I have cancelled the process. Let me know if you need anything else."
            return AssistantResponse(
                intent = "BOOK_APPOINTMENT",
                goal = "Booking cancelled",
                response = cancelMsg,
                needsClarification = false,
                clarifyingQuestion = null,
                suggestedNextStep = null
            )
        }
        return if (isHindi) getNextStepResponseHindi(state) else getNextStepResponseEnglish(state)
    }

    private fun getNextStepResponseEnglish(state: BookingState): AssistantResponse {
        return when {
            // Step 1: Location Prompt
            state.location == null -> {
                val prompt = "Which city or locality are you looking to book an appointment in?"
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "Ask booking location",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "Say your city or locality, like Mumbai, Delhi, or Bandra."
                )
            }

            // Step 2: Specialty Selection
            state.specialty == null -> {
                val prompt = "Which type of doctor do you need in ${state.location}? You can choose from: General Physician, Cardiologist, Orthopedic, Ophthalmologist, or Neurologist."
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "Ask doctor specialty",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "Say General Physician, Cardiologist, Orthopedic, Ophthalmologist, or Neurologist."
                )
            }

            // Step 3: Interactive Doctor Selection Cards
            state.doctorName == null -> {
                val location = state.location ?: "your area"
                val prompt = when (state.specialty) {
                    "Cardiologist" ->
                        "Here are 3 available Cardiologists: 1. Dr. Priya Mehta, MD, DM Cardiology, 2. Dr. Suresh Nair, MBBS, MCh Cardio, and 3. Dr. Ananya Das, MD, DNB Cardiology. Which doctor would you prefer?"
                    "Orthopedic" ->
                        "Here are 3 available Orthopedic specialists: 1. Dr. Vikram Joshi, MS Orthopedics, 2. Dr. Ramesh Kulkarni, MBBS, D.Ortho, and 3. Dr. Neha Gupta, MS Joint Replacement Specialist. Which doctor would you prefer?"
                    "Ophthalmologist" ->
                        "Here are 3 available Eye Specialists: 1. Dr. Sanjay Rao, MS Ophthalmology, 2. Dr. Meera Deshmukh, DOMS Eye Specialist, and 3. Dr. Amit Shah, MD Ophthalmic Surgeon. Which doctor would you prefer?"
                    "Neurologist" ->
                        "Here are 3 available Neurologists: 1. Dr. Rohan Kapoor, DM Neurology, 2. Dr. Kavita Iyer, MD, DNB Neurology, and 3. Dr. Alok Pandey, MBBS, DM Neuro. Which doctor would you prefer?"
                    else -> // General Physician
                        "Here are 3 available General Physicians in $location: 1. Dr. Rajesh Sharma, MBBS, MD Internal Medicine, 2. Dr. Sunita Patil, MBBS Family Physician, and 3. Dr. Anil Verma, MBBS Senior Consultant. Which one would you like to select?"
                }
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "Select doctor from list",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "Tap a doctor card or say 'First one', 'Second one', or the doctor's name."
                )
            }

            // Step 4: Date & Time Selection
            state.dateTime == null -> {
                val prompt = "What date and time work best for you?"
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "Ask appointment date and time",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "Mention your preferred date and time, like tomorrow at 10 AM."
                )
            }

            // Step 5: Booking Confirmation & Session Continuity
            else -> {
                val degree = getDoctorDegree(state.doctorName, state.specialty)
                val confirmedMsg = "I have set up an appointment with ${state.doctorName} ($degree) in ${state.location} for ${state.dateTime}. Your booking is confirmed! How else can I assist you today? You can pay a bill or book another appointment."
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "Appointment confirmed with continuity",
                    response = confirmedMsg,
                    needsClarification = false,
                    clarifyingQuestion = null,
                    suggestedNextStep = "You can immediately pay a utility bill (Water, Electricity, Gas, Mobile Recharge) or book another appointment.",
                    helpfulTip = "Your appointment is confirmed. Session remains active for utility payments or other services."
                )
            }
        }
    }

    private fun getNextStepResponseHindi(state: BookingState): AssistantResponse {
        val specialtyNameHi = when (state.specialty) {
            "Cardiologist" -> "हृदय रोग विशेषज्ञ"
            "Orthopedic" -> "हड्डी रोग विशेषज्ञ"
            "Ophthalmologist" -> "नेत्र रोग विशेषज्ञ"
            "Neurologist" -> "न्यूरोलॉजिस्ट"
            else -> "सामान्य डॉक्टर"
        }
        val doctorNameHi = getDoctorHindiName(state.doctorName)

        return when {
            // चरण 1: स्थान पूछना
            state.location == null -> {
                val prompt = "आप किस शहर या इलाके में अपॉइंटमेंट बुक करना चाहते हैं?"
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "स्थान पूछें",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "अपना शहर या इलाका बताएं, जैसे मुंबई, दिल्ली, या बांद्रा।"
                )
            }

            // चरण 2: विशेषज्ञता का चयन
            state.specialty == null -> {
                val prompt = "आपको ${state.location} में किस प्रकार के डॉक्टर की आवश्यकता है? आप इनमें से चुन सकते हैं: सामान्य डॉक्टर, हृदय रोग विशेषज्ञ, हड्डी रोग विशेषज्ञ, नेत्र रोग विशेषज्ञ, या न्यूरोलॉजिस्ट।"
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "विशेषज्ञता पूछें",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "सामान्य डॉक्टर, हृदय रोग विशेषज्ञ, हड्डी रोग विशेषज्ञ, नेत्र रोग विशेषज्ञ, या न्यूरोलॉजिस्ट बोलें।"
                )
            }

            // चरण 3: डॉक्टर का चयन
            state.doctorName == null -> {
                val prompt = when (state.specialty) {
                    "Cardiologist" ->
                        "हमारे पास 3 हृदय रोग विशेषज्ञ उपलब्ध हैं: 1. डॉ. प्रिया मेहता (डीएम कार्डियोलॉजी), 2. डॉ. सुरेश नायर (एम्ची कार्डियो), 3. डॉ. अनन्या दास (डीएनबी कार्डियो)। आप किसे चुनना चाहेंगे?"
                    "Orthopedic" ->
                        "हमारे पास 3 हड्डी रोग विशेषज्ञ उपलब्ध हैं: 1. डॉ. विक्रम जोशी (एमएस ऑर्थोपेडिक्स), 2. डॉ. रमेश कुलकर्णी (डी.ऑर्थो), 3. डॉ. नेहा गुप्ता (जोइंट रिप्लेसमेंट स्पेशलिस्ट)। आप किसे चुनना चाहेंगे?"
                    "Ophthalmologist" ->
                        "हमारे पास 3 नेत्र रोग विशेषज्ञ उपलब्ध हैं: 1. डॉ. संजय राव (एमएस ऑप्थैल्मोलॉजी), 2. डॉ. मीरा देशमुख (आई स्पेशलिस्ट), 3. डॉ. अमित शाह (ऑप्थैल्मिक सर्जन)। आप किसे चुनना चाहेंगे?"
                    "Neurologist" ->
                        "हमारे पास 3 न्यूरोलॉजिस्ट उपलब्ध हैं: 1. डॉ. रोहन कपूर (डीएम न्यूरोलॉजी), 2. डॉ. कविता अय्यर (डीएनबी न्यूरोलॉजी), 3. डॉ. आलोक पांडे (डीएम न्यूरो)। आप किसे चुनना चाहेंगे?"
                    else -> // General Physician
                        "हमारे पास 3 सामान्य डॉक्टर उपलब्ध हैं: 1. डॉ. राजेश शर्मा (एमडी), 2. डॉ. सुनिता पाटिल (फैमिली फिजिशियन), 3. डॉ. अनिल वर्मा (सीनियर कंसल्टेंट)। आप किसे चुनना चाहेंगे?"
                }
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "डॉक्टर चुनें",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "कार्ड पर टैप करें या 'पहला', 'दूसरा' या डॉक्टर का नाम बोलें।"
                )
            }

            // चरण 4: तारीख और समय
            state.dateTime == null -> {
                val prompt = "आपके लिए कौन सी तारीख और समय सही रहेगा?"
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "तारीख और समय पूछें",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "अपनी पसंदीदा तारीख और समय बताएं, जैसे कल सुबह 10 बजे।"
                )
            }

            // चरण 5: पुष्टि व निरंतरता
            else -> {
                val degreeHi = getDoctorDegreeHindi(state.doctorName, state.specialty)
                val confirmedMsg = "मैंने ${state.location} में ${state.dateTime} को $doctorNameHi ($degreeHi) के साथ अपॉइंटमेंट तय किया है। आपका अपॉइंटमेंट कन्फर्म हो गया है! मैं आपकी और क्या सहायता कर सकता हूं? आप बिल भुगतान या अन्य सेवा चुन सकते हैं।"
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "अपॉइंटमेंट कन्फर्म व निरंतरता",
                    response = confirmedMsg,
                    needsClarification = false,
                    clarifyingQuestion = null,
                    suggestedNextStep = "आप तुरंत बिल भुगतान कर सकते हैं या कोई अन्य सेवा चुन सकते हैं।",
                    helpfulTip = "आपका अपॉइंटमेंट सफलतापूर्वक दर्ज हो गया है।"
                )
            }
        }
    }

    private fun getDoctorHindiName(enName: String?): String {
        if (enName == null) return "डॉक्टर"
        for (spec in DOCTOR_DATABASE) {
            for (doc in spec.doctors) {
                if (doc.nameEn.equals(enName, ignoreCase = true)) {
                    return doc.nameHi
                }
            }
        }
        return enName
    }

    private fun String.capitalizeWords(): String {
        return this.split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.lowercase(Locale.ROOT).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                }
            }
    }
}
