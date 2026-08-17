package com.example.elderhelpprototypev01.ai

import android.util.Log
import com.example.elderhelpprototypev01.BuildConfig
import com.example.elderhelpprototypev01.model.AssistantResponse
import com.example.elderhelpprototypev01.model.ConversationMessage
import com.example.elderhelpprototypev01.model.MessageRole
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * GeminiLlmService
 *
 * Implements [LlmService] using the Gemini REST API.
 * Uses OkHttp for HTTP + Gson for JSON.
 *
 * Key features:
 * - Resilient model fallback: tries gemini-2.5-flash -> gemini-1.5-flash -> gemini-2.0-flash
 * - Exact sequential 5-step Doctor Booking conversation flow
 * - Offline / network fallback with DoctorBookingManager
 * - Never exposes technical jargon, stack traces, or HTTP error codes
 */
class GeminiLlmService : LlmService {

    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val apiKey: String get() = BuildConfig.GEMINI_API_KEY

    // Candidate models to try in priority order
    private val candidateModels = listOf(
        "gemini-2.5-flash",
        "gemini-1.5-flash",
        "gemini-2.0-flash"
    )

    private val friendlyConnectionError =
        "I'm having a little trouble connecting right now. Please try again in a moment."

    override suspend fun analyze(
        transcript: String,
        conversation: List<ConversationMessage>,
        userLanguage: String
    ): AssistantResponse = withContext(Dispatchers.IO) {
        if (transcript.isBlank()) {
            return@withContext AssistantResponse(
                intent = "REPAIR",
                goal = "Prompt user to speak",
                response = "I didn't catch that. Could you please try speaking again?",
                needsClarification = true,
                clarifyingQuestion = "I didn't catch that. Could you please try speaking again?"
            )
        }

        // Check if user is in Doctor Booking or Bill Payment flow
        val isDoctorBooking = DoctorBookingManager.isDoctorBookingIntent(transcript, conversation)
        val isBillPayment = BillPaymentManager.isBillPaymentIntent(transcript, conversation)

        if (apiKey == "REPLACE_WITH_YOUR_GEMINI_API_KEY" || apiKey.isBlank()) {
            if (isDoctorBooking) {
                val state = DoctorBookingManager.extractState(conversation, transcript)
                return@withContext DoctorBookingManager.getNextStepResponse(state)
            }
            if (isBillPayment) {
                val state = BillPaymentManager.extractState(conversation, transcript)
                return@withContext BillPaymentManager.getNextStepResponse(state)
            }
            return@withContext AssistantResponse.error(friendlyConnectionError)
        }

        val requestBodyJson = buildRequestBody(transcript, conversation, userLanguage)

        var lastError: Exception? = null

        // Try candidate models in sequence
        for (model in candidateModels) {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            try {
                val request = Request.Builder()
                    .url(endpoint)
                    .post(requestBodyJson.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val code = response.code
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                    val parsed = parseGeminiResponse(responseBody, transcript, conversation)
                    return@withContext parsed
                } else {
                    Log.w("GeminiLlmService", "Model $model returned HTTP $code: $responseBody")
                    // If error is 404 or 503, try next candidate model
                    continue
                }
            } catch (e: Exception) {
                Log.w("GeminiLlmService", "Attempt with model $model failed", e)
                lastError = e
            }
        }

        // All models or network failed - use smart deterministic fallbacks
        if (isDoctorBooking) {
            val state = DoctorBookingManager.extractState(conversation, transcript)
            return@withContext DoctorBookingManager.getNextStepResponse(state)
        }
        if (isBillPayment) {
            val state = BillPaymentManager.extractState(conversation, transcript)
            return@withContext BillPaymentManager.getNextStepResponse(state)
        }

        Log.e("GeminiLlmService", "All Gemini models failed. Returning friendly fallback.", lastError)
        AssistantResponse.error(friendlyConnectionError)
    }

    // ------------------------------------------------------------------
    // Request Building
    // ------------------------------------------------------------------

    private fun buildRequestBody(
        transcript: String,
        conversation: List<ConversationMessage>,
        userLanguage: String
    ): String {
        val systemInstruction = buildSystemPrompt(userLanguage)

        // Build conversation history ensuring proper alternating user/model roles
        val rawMessages = mutableListOf<Pair<String, String>>()
        val recentHistory = conversation.takeLast(10)
        for (msg in recentHistory) {
            val role = if (msg.role == MessageRole.USER) "user" else "model"
            if (msg.text.isNotBlank()) {
                rawMessages.add(role to msg.text)
            }
        }
        rawMessages.add("user" to transcript)

        // Merge consecutive messages with the same role to strictly satisfy Gemini API format
        val mergedHistory = mutableListOf<Pair<String, String>>()
        for (item in rawMessages) {
            if (mergedHistory.isNotEmpty() && mergedHistory.last().first == item.first) {
                val prev = mergedHistory.removeAt(mergedHistory.size - 1)
                mergedHistory.add(prev.first to "${prev.second}\n${item.second}")
            } else {
                mergedHistory.add(item)
            }
        }

        val historyParts = mergedHistory.map { (role, text) ->
            mapOf(
                "role" to role,
                "parts" to listOf(mapOf("text" to text))
            )
        }

        val requestMap = mapOf(
            "system_instruction" to mapOf(
                "parts" to listOf(mapOf("text" to systemInstruction))
            ),
            "contents" to historyParts,
            "generationConfig" to mapOf(
                "temperature" to 0.4,
                "maxOutputTokens" to 600,
                "responseMimeType" to "application/json"
            )
        )

        return gson.toJson(requestMap)
    }

    private fun buildSystemPrompt(userLanguage: String): String {
        val languageInstruction = when {
            userLanguage.contains("Hindi") ->
                """The user prefers Hindi or Hinglish. Respond in the SAME language mix the user used.
                   If they spoke in Hinglish, reply in natural Hinglish.
                   If they spoke in pure Hindi (Devanagari), reply in simple Hindi.
                   Keep words short, empathetic, and common."""
            userLanguage.contains("Marathi") ->
                "Respond in simple Marathi (मराठी). Use easy, everyday words only."
            userLanguage.contains("Tamil") ->
                "Respond in simple Tamil (தமிழ்). Use easy, everyday words only."
            userLanguage.contains("Telugu") ->
                "Respond in simple Telugu (తెలుగు). Use easy, everyday words only."
            userLanguage.contains("Bengali") ->
                "Respond in simple Bengali (বাংলা). Use easy, everyday words only."
            else ->
                "Respond in simple, clear, empathetic English. Short sentences only."
        }

        return """
You are "Sahaay", a calm, empathetic, and intelligent voice assistant helping users (including elderly users) book medical appointments and get assistance.

$languageInstruction

### CONVERSATION FLOW FOR DOCTOR BOOKING:
When the user expresses an intent to book a doctor (e.g., "book a doctor", "I need an appointment", "find a doctor"), guide them through a step-by-step sequential dialogue to gather all necessary details.

Follow this EXACT sequential question flow:
1. Specialty / Doctor Type:
   - Prompt: "Which type of doctor would you like to book? (e.g., General Physician, Dermatologist, Cardiologist)"
2. Location / Place:
   - Prompt: "Which area, city, or clinic location do you prefer?"
3. Preferred Date and Time:
   - Prompt: "What date and time work best for you?"
4. Consultation Mode:
   - Prompt: "Would you prefer an in-person clinic visit or an online consultation?"
5. Confirmation:
   - Summarize all details (Doctor Type, Location, Date/Time, Visit Mode) and ask the user to confirm. (e.g. "I have noted your appointment details: [Doctor Type] in [Location] on [Date and Time] for an [In-person clinic visit / Online consultation]. Would you like me to confirm this booking?")
6. When the user confirms (e.g. "yes", "confirm", "sure"):
   - Acknowledge warmly: "Your appointment for [Doctor Type] in [Location] on [Date and Time] ([Visit Mode]) has been confirmed! Is there anything else I can help you with?"

### CONVERSATION FLOW FOR PAY BILLS:
When the user expresses an intent to pay a bill (e.g., "pay my bill", "recharge my phone", "pay electricity"):
Guide them step-by-step through a sequential dialogue. Ask ONLY ONE question at a time.

1. Bill Category / Type (if not specified):
   - Prompt: "Which bill would you like to pay? (Electricity, Water, or Mobile Recharge)"
2. Account Identifier / Details:
   - For Electricity: "Please tell me your Consumer or Account ID."
   - For Water: "Please tell me your Water Consumer / Meter Number."
   - For Mobile Recharge: "Which mobile number would you like to recharge?"
3. Provider / Operator (if applicable):
   - Prompt: "Who is your service provider or operator? (e.g., Adani Electricity, Tata Power, Jio, Airtel)"
4. Amount:
   - Prompt: "How much amount would you like to pay or recharge?"
5. Confirmation:
   - Summarize clearly: "I have set up a payment of ₹[Amount] for your [Bill Type] ([Provider], ID: [Account ID]). Should I proceed to payment?"
6. When the user confirms (e.g. "yes", "proceed", "sure", "pay"):
   - Acknowledge warmly: "Your payment of ₹[Amount] for [Bill Type] ([Provider]) has been processed successfully! Is there anything else I can help you with?"

### CONVERSATIONAL RULES:
- Ask ONLY ONE question at a time to keep the voice interface clear and simple.
- If the user provides multiple details in a single message (e.g., "Pay my Adani electricity bill of 1450 with account id 102938475"), skip the questions for details already provided and ask only for missing information or go straight to confirmation.
- Keep responses short, empathetic, clear, and voice-friendly.
- If an input is unclear, ambiguous, or incomplete, ask a polite clarifying question instead of failing.
- NEVER expose technical jargon, stack traces, or HTTP codes (e.g., 503, 500, JSON errors) to the user.
- NEVER use markdown formatting (no asterisks, no bullet points, no bold). Only plain sentences separated by commas or periods.

### KNOWN INTENTS:
BOOK_APPOINTMENT, PAY_BILL, FILL_FORM, EXPLAIN_TERM, ASK_QUESTION, EMERGENCY_HELP, VOCAL_ANCHOR, REPAIR, GENERAL

You MUST return ONLY valid JSON matching this schema:
{
  "intent": "PAY_BILL",
  "goal": "Short description of goal",
  "response": "Your main spoken response (single question or confirmation, plain text without markdown)",
  "needs_clarification": true or false,
  "clarifying_question": "Single question string if asking for info, or null",
  "suggested_next_step": "Short helper tip for user or null",
  "helpful_tip": "Optional brief safety or preparation tip or null"
}
        """.trimIndent()
    }

    // ------------------------------------------------------------------
    // Response Parsing
    // ------------------------------------------------------------------

    private fun parseGeminiResponse(
        responseBody: String,
        transcript: String,
        conversation: List<ConversationMessage>
    ): AssistantResponse {
        return try {
            val root = gson.fromJson(responseBody, JsonObject::class.java)

            val candidates = root.getAsJsonArray("candidates")
            if (candidates == null || candidates.size() == 0) {
                return handleParseFallback(transcript, conversation)
            }

            val firstCandidate = candidates.get(0)?.asJsonObject
            val content = firstCandidate?.getAsJsonObject("content")
            val parts = content?.getAsJsonArray("parts")
            val text = parts?.get(0)?.asJsonObject?.get("text")?.asString

            if (text.isNullOrBlank()) {
                return handleParseFallback(transcript, conversation)
            }

            parseStructuredResponse(text.trim(), transcript, conversation)
        } catch (e: Exception) {
            Log.e("GeminiLlmService", "Failed to parse Gemini response", e)
            handleParseFallback(transcript, conversation)
        }
    }

    private fun parseStructuredResponse(
        jsonText: String,
        transcript: String,
        conversation: List<ConversationMessage>
    ): AssistantResponse {
        return try {
            var cleaned = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val start = cleaned.indexOf("{")
            val end = cleaned.lastIndexOf("}")

            if (start >= 0 && end > start) {
                cleaned = cleaned.substring(start, end + 1)
            }

            val obj = gson.fromJson(cleaned, JsonObject::class.java)

            val intent = obj.get("intent")?.asString ?: "GENERAL"

            val responseText = obj.get("response")
                ?.takeIf { !it.isJsonNull }
                ?.asString
                ?.trim()
                ?: ""

            val clarification = obj.get("clarifying_question")
                ?.takeIf { !it.isJsonNull }
                ?.asString
                ?.trim()

            val needsClarification =
                obj.get("needs_clarification")
                    ?.takeIf { !it.isJsonNull }
                    ?.asBoolean
                    ?: false

            val defaultPrompt = when (intent) {
                "PAY_BILL" -> "Which bill would you like to pay? (Electricity, Water, or Mobile Recharge)"
                "BOOK_APPOINTMENT" -> "Which type of doctor would you like to book? (e.g., General Physician, Dermatologist, Cardiologist)"
                else -> "How may I help you today?"
            }

            val finalResponse = when {
                responseText.isNotBlank() -> responseText
                needsClarification && !clarification.isNullOrBlank() -> clarification
                else -> defaultPrompt
            }

            AssistantResponse(
                intent = intent,
                goal = obj.get("goal")?.asString ?: "Assistance",
                response = cleanTtsText(finalResponse),
                needsClarification = needsClarification,
                clarifyingQuestion = clarification?.let { cleanTtsText(it) },
                suggestedNextStep = obj.get("suggested_next_step")
                    ?.takeIf { !it.isJsonNull }
                    ?.asString
                    ?.let { cleanTtsText(it) },
                helpfulTip = obj.get("helpful_tip")
                    ?.takeIf { !it.isJsonNull }
                    ?.asString
                    ?.let { cleanTtsText(it) }
            )
        } catch (e: Exception) {
            Log.e("GeminiLlmService", "Error in parseStructuredResponse", e)
            handleParseFallback(transcript, conversation)
        }
    }

    private fun handleParseFallback(transcript: String, conversation: List<ConversationMessage>): AssistantResponse {
        if (DoctorBookingManager.isDoctorBookingIntent(transcript, conversation)) {
            val state = DoctorBookingManager.extractState(conversation, transcript)
            return DoctorBookingManager.getNextStepResponse(state)
        }
        if (BillPaymentManager.isBillPaymentIntent(transcript, conversation)) {
            val state = BillPaymentManager.extractState(conversation, transcript)
            return BillPaymentManager.getNextStepResponse(state)
        }
        return AssistantResponse(
            intent = "GENERAL",
            goal = "",
            response = "I understood your request. Could you please tell me a little more?"
        )
    }

    private fun cleanTtsText(text: String): String {
        return text
            .replace(Regex("\\*{1,2}"), "")
            .replace(Regex("^#{1,6}\\s*", RegexOption.MULTILINE), "")
            .replace(Regex("^[-*]\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("^>\\s+", RegexOption.MULTILINE), "")
            .trim()
    }
}
