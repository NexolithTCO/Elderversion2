package com.example.elderhelpprototypev01.ai

import com.example.elderhelpprototypev01.model.AssistantResponse
import com.example.elderhelpprototypev01.model.ConversationMessage
import java.util.Locale

/**
 * DoctorBookingManager
 *
 * Dedicated manager for the Doctor Booking conversation flow.
 * Handles slot extraction and provides deterministic, voice-friendly
 * step-by-step guidance following the exact 5-step flow:
 *
 * 1. Specialty / Doctor Type
 * 2. Location / Place
 * 3. Preferred Date and Time
 * 4. Consultation Mode
 * 5. Confirmation
 *
 * Used both as a standalone fallback engine and to ensure
 * conversational continuity even if the Gemini API has network hiccups.
 */
object DoctorBookingManager {

    data class BookingState(
        var specialty: String? = null,
        var location: String? = null,
        var dateTime: String? = null,
        var mode: String? = null,
        var isConfirmed: Boolean = false
    )

    private val DOCTOR_INTENT_KEYWORDS = listOf(
        "doctor", "appointment", "book a doctor", "see a doctor",
        "consult a doctor", "medical appointment", "physician",
        "dermatologist", "cardiologist", "pediatrician", "dentist",
        "orthopedic", "neurologist", "gynecologist", "ent specialist",
        "eye specialist", "clinic", "hospital", "dr", "dr."
    )

    private val KNOWN_SPECIALTIES = listOf(
        "General Physician", "Dermatologist", "Cardiologist",
        "Pediatrician", "Dentist", "Orthopedic", "Neurologist",
        "Gynecologist", "ENT Specialist", "Eye Specialist",
        "Psychiatrist", "Oncologist", "Urologist", "Gastroenterologist",
        "Physiotherapist", "Diabetologist", "Surgeon"
    )

    /**
     * Checks if the transcript or history indicates a doctor booking intent.
     */
    fun isDoctorBookingIntent(transcript: String, conversation: List<ConversationMessage>): Boolean {
        val lowerTranscript = transcript.lowercase(Locale.ROOT)
        if (DOCTOR_INTENT_KEYWORDS.any { lowerTranscript.contains(it) }) {
            return true
        }
        // Check if previous assistant message was asking one of our booking questions
        val lastAssistantMsg = conversation.lastOrNull { it.role == com.example.elderhelpprototypev01.model.MessageRole.ASSISTANT }?.text?.lowercase(Locale.ROOT)
        if (lastAssistantMsg != null) {
            if (lastAssistantMsg.contains("type of doctor") ||
                lastAssistantMsg.contains("area, city, or clinic") ||
                lastAssistantMsg.contains("date and time") ||
                lastAssistantMsg.contains("in-person clinic visit or an online") ||
                lastAssistantMsg.contains("confirm this booking") ||
                lastAssistantMsg.contains("appointment details")
            ) {
                return true
            }
        }
        return false
    }

    /**
     * Reconstructs the current booking state by analyzing the full conversation + new transcript.
     */
    fun extractState(conversation: List<ConversationMessage>, currentTranscript: String): BookingState {
        val state = BookingState()

        // Combine all user utterances and assistant questions in chronological order
        val allUserTexts = mutableListOf<String>()
        var lastQuestion = ""

        for (msg in conversation) {
            if (msg.role == com.example.elderhelpprototypev01.model.MessageRole.USER) {
                allUserTexts.add(msg.text)
                parseUserUtterance(msg.text, lastQuestion, state)
            } else {
                lastQuestion = msg.text.lowercase(Locale.ROOT)
            }
        }

        // Now parse the current transcript with the latest assistant question context
        parseUserUtterance(currentTranscript, lastQuestion, state)

        return state
    }

    private fun parseUserUtterance(text: String, lastQuestion: String, state: BookingState) {
        val lower = text.lowercase(Locale.ROOT).trim()

        // 1. Check if user is confirming or answering the confirmation step
        if (lastQuestion.contains("confirm this booking") || lastQuestion.contains("like me to confirm")) {
            if (lower.contains("yes") || lower.contains("confirm") || lower.contains("sure") ||
                lower.contains("ha") || lower.contains("haa") || lower.contains("yep") ||
                lower.contains("proceed") || lower.contains("ok") || lower.contains("okay") ||
                lower.contains("correct")
            ) {
                state.isConfirmed = true
                return
            }
        }

        // 2. Specialty Extraction
        for (spec in KNOWN_SPECIALTIES) {
            if (lower.contains(spec.lowercase(Locale.ROOT))) {
                state.specialty = spec
                break
            }
        }
        if (state.specialty == null) {
            when {
                lower.contains("general physician") || lower.contains("physician") || lower.contains("general doctor") || lower.contains("normal doctor") || lower.contains("family doctor") ->
                    state.specialty = "General Physician"
                lower.contains("skin") || lower.contains("derma") ->
                    state.specialty = "Dermatologist"
                lower.contains("heart") || lower.contains("cardio") ->
                    state.specialty = "Cardiologist"
                lower.contains("child") || lower.contains("kid") || lower.contains("pedia") ->
                    state.specialty = "Pediatrician"
                lower.contains("teeth") || lower.contains("tooth") || lower.contains("dental") ->
                    state.specialty = "Dentist"
                lower.contains("bone") || lower.contains("joint") || lower.contains("ortho") ->
                    state.specialty = "Orthopedic"
                lower.contains("eye") || lower.contains("vision") || lower.contains("opt") ->
                    state.specialty = "Eye Specialist"
                lower.contains("ear") || lower.contains("nose") || lower.contains("throat") ->
                    state.specialty = "ENT Specialist"
                lastQuestion.contains("type of doctor") -> {
                    // User directly answered the specialty question
                    val cleaned = text.replace(Regex("(?i)^(i want|i need|book a|book an|please book)\\s+"), "").trim()
                    if (cleaned.isNotBlank() && !cleaned.contains("in-person") && !cleaned.contains("online")) {
                        state.specialty = cleaned.capitalizeWords()
                    }
                }
            }
        }

        // 3. Consultation Mode Extraction
        if (lower.contains("in-person") || lower.contains("in person") || lower.contains("clinic visit") || lower.contains("visit") || lower.contains("offline") || lower.contains("hospital visit")) {
            state.mode = "in-person clinic visit"
        } else if (lower.contains("online") || lower.contains("video") || lower.contains("teleconsult") || lower.contains("virtual") || lower.contains("phone call")) {
            state.mode = "online consultation"
        } else if (lastQuestion.contains("in-person clinic visit or an online")) {
            if (lower.contains("first") || lower.contains("clinic") || lower.contains("hospital")) {
                state.mode = "in-person clinic visit"
            } else if (lower.contains("second") || lower.contains("call") || lower.contains("video")) {
                state.mode = "online consultation"
            }
        }

        // 4. Location Extraction
        if (state.location == null) {
            val locMatch = Regex("(?i)\\b(in|at|near|around)\\s+([A-Za-z0-9\\s]{3,30}?)(?=\\s+(for|at|on|tomorrow|today|next|in-person|online|visit)|$)").find(text)
            if (locMatch != null) {
                val candidate = locMatch.groupValues[2].trim()
                val isTimeLike = candidate.matches(Regex("(?i).*\\b(\\d{1,2}(?::\\d{2})?\\s*(?:am|pm)|tomorrow|today|morning|evening|night|monday|tuesday|wednesday|thursday|friday|saturday|sunday)\\b.*"))
                if (!isTimeLike && !candidate.equals("person", ignoreCase = true) && !candidate.equals("line", ignoreCase = true) && candidate.length > 2) {
                    state.location = candidate.capitalizeWords()
                }
            } else if (lastQuestion.contains("area, city, or clinic") || lastQuestion.contains("location do you prefer")) {
                val cleaned = text.replace(Regex("(?i)^(in|at|near|i prefer|i want|prefer)\\s+"), "").trim()
                if (cleaned.isNotBlank() && !cleaned.contains("tomorrow", ignoreCase = true) && !cleaned.contains("today", ignoreCase = true) && !cleaned.contains("pm", ignoreCase = true) && !cleaned.contains("am", ignoreCase = true)) {
                    state.location = cleaned.capitalizeWords()
                }
            }
        }

        // 5. Preferred Date and Time Extraction
        val timePatterns = listOf(
            Regex("(?i)\\b(tomorrow|today|tonight|next\\s+\\w+|monday|tuesday|wednesday|thursday|friday|saturday|sunday)(\\s+at\\s+\\d{1,2}(?::\\d{2})?\\s*(?:am|pm)?)?"),
            Regex("(?i)\\b\\d{1,2}(?::\\d{2})?\\s*(?:am|pm)\\b"),
            Regex("(?i)\\b\\d{1,2}(?:st|nd|rd|th)?\\s+(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*(\\s+at\\s+\\d{1,2}(?::\\d{2})?\\s*(?:am|pm)?)?"),
            Regex("(?i)\\b(morning|afternoon|evening|night)\\b")
        )

        for (pattern in timePatterns) {
            val match = pattern.find(text)
            if (match != null) {
                state.dateTime = match.value.trim()
                break
            }
        }
        if (state.dateTime == null && (lastQuestion.contains("date and time") || lastQuestion.contains("work best for you"))) {
            state.dateTime = text.trim()
        }
    }

    /**
     * Generates the next question or response in the exact 5-step sequence.
     */
    fun getNextStepResponse(state: BookingState): AssistantResponse {
        return when {
            // Step 1: Specialty
            state.specialty == null -> {
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "Ask doctor specialty",
                    response = "Which type of doctor would you like to book? (e.g., General Physician, Dermatologist, Cardiologist)",
                    needsClarification = true,
                    clarifyingQuestion = "Which type of doctor would you like to book? (e.g., General Physician, Dermatologist, Cardiologist)",
                    suggestedNextStep = "Say the type of doctor you need, like General Physician or Dermatologist."
                )
            }

            // Step 2: Location
            state.location == null -> {
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "Ask preferred location",
                    response = "Which area, city, or clinic location do you prefer?",
                    needsClarification = true,
                    clarifyingQuestion = "Which area, city, or clinic location do you prefer?",
                    suggestedNextStep = "Mention your preferred area or city, for example Bandra or Central Clinic."
                )
            }

            // Step 3: Preferred Date and Time
            state.dateTime == null -> {
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "Ask preferred date and time",
                    response = "What date and time work best for you?",
                    needsClarification = true,
                    clarifyingQuestion = "What date and time work best for you?",
                    suggestedNextStep = "Mention your preferred time, like tomorrow at 5 PM or Monday morning."
                )
            }

            // Step 4: Consultation Mode
            state.mode == null -> {
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "Ask consultation mode",
                    response = "Would you prefer an in-person clinic visit or an online consultation?",
                    needsClarification = true,
                    clarifyingQuestion = "Would you prefer an in-person clinic visit or an online consultation?",
                    suggestedNextStep = "Say in-person clinic visit or online consultation."
                )
            }

            // Step 5: Confirmation
            !state.isConfirmed -> {
                val summary = "I have noted your appointment details: ${state.specialty} in ${state.location} on ${state.dateTime} for an ${state.mode}. Would you like me to confirm this booking?"
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "Confirm appointment details",
                    response = summary,
                    needsClarification = true,
                    clarifyingQuestion = summary,
                    suggestedNextStep = "Say yes to confirm, or tell me if you want to change any detail."
                )
            }

            // Completed / Confirmed
            else -> {
                val confirmedMsg = "Your appointment for ${state.specialty} in ${state.location} on ${state.dateTime} (${state.mode}) has been confirmed! Is there anything else I can help you with?"
                AssistantResponse(
                    intent = "BOOK_APPOINTMENT",
                    goal = "Appointment confirmed",
                    response = confirmedMsg,
                    needsClarification = false,
                    clarifyingQuestion = null,
                    suggestedNextStep = "You can ask for another appointment or any health guidance.",
                    helpfulTip = "Please arrive 10 minutes early or ensure your internet connection is ready for online consultation."
                )
            }
        }
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
