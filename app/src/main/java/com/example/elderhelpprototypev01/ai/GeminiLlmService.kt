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
 * - 5 Specialties x 3 Doctors comprehensive database
 * - Global Voice Cancellation Protocol
 * - Sequential Doctor Booking flow (Steps 1 to 5)
 * - 4 Utility Bill Payment & Mobile Recharge flows
 * - Offline / deterministic fallback with DoctorBookingManager and BillPaymentManager
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
        val isHindi = userLanguage.contains("Hindi") || userLanguage.contains("हिंदी")

        if (transcript.isBlank()) {
            val repairMsg = if (isHindi)
                "मैं सुन नहीं पाया। कृपया दोबारा बोलने की कोशिश करें।"
            else
                "I didn't catch that. Could you please try speaking again?"
            return@withContext AssistantResponse(
                intent = "REPAIR",
                goal = "Prompt user to speak",
                response = repairMsg,
                needsClarification = true,
                clarifyingQuestion = repairMsg
            )
        }

        // Voice Cancellation check
        if (DoctorBookingManager.isCancellationIntent(transcript) || BillPaymentManager.isCancellationIntent(transcript)) {
            val cancelMsg = if (isHindi)
                "ठीक है, मैंने प्रक्रिया रद्द कर दी है। यदि आपको कुछ और चाहिए तो मुझे बताएं।"
            else
                "Okay, I have cancelled the process. Let me know if you need anything else."
            return@withContext AssistantResponse(
                intent = "CANCEL",
                goal = "Cancel transaction",
                response = cancelMsg,
                needsClarification = false,
                clarifyingQuestion = null,
                suggestedNextStep = null
            )
        }

        // Check if user is in Doctor Booking or Bill Payment flow
        val isDoctorBooking = DoctorBookingManager.isDoctorBookingIntent(transcript, conversation)
        val isBillPayment = BillPaymentManager.isBillPaymentIntent(transcript, conversation)

        if (apiKey == "REPLACE_WITH_YOUR_GEMINI_API_KEY" || apiKey.isBlank()) {
            if (isDoctorBooking) {
                val state = DoctorBookingManager.extractState(conversation, transcript)
                return@withContext DoctorBookingManager.getNextStepResponse(state, userLanguage)
            }
            if (isBillPayment) {
                val state = BillPaymentManager.extractState(conversation, transcript)
                return@withContext BillPaymentManager.getNextStepResponse(state, userLanguage)
            }
            val fallbackMsg = if (isHindi)
                "अभी कनेक्शन में थोड़ी दिक्कत है। कृपया थोड़ी देर बाद फिर कोशिश करें।"
            else
                friendlyConnectionError
            return@withContext AssistantResponse.error(fallbackMsg)
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
                    val parsed = parseGeminiResponse(responseBody, transcript, conversation, userLanguage)
                    return@withContext parsed
                } else {
                    Log.w("GeminiLlmService", "Model $model returned HTTP $code: $responseBody")
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
            return@withContext DoctorBookingManager.getNextStepResponse(state, userLanguage)
        }
        if (isBillPayment) {
            val state = BillPaymentManager.extractState(conversation, transcript)
            return@withContext BillPaymentManager.getNextStepResponse(state, userLanguage)
        }

        Log.e("GeminiLlmService", "All Gemini models failed. Returning friendly fallback.", lastError)
        val errorMsg = if (isHindi)
            "अभी कनेक्शन में थोड़ी दिक्कत है। कृपया थोड़ी देर बाद फिर कोशिश करें।"
        else
            friendlyConnectionError
        AssistantResponse.error(errorMsg)
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

        // Merge consecutive messages with the same role
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
                "temperature" to 0.3,
                "maxOutputTokens" to 600,
                "responseMimeType" to "application/json"
            )
        )

        return gson.toJson(requestMap)
    }

    private fun buildSystemPrompt(userLanguage: String): String {
        val isHindi = userLanguage.contains("Hindi") || userLanguage.contains("हिंदी")

        return if (isHindi) buildHindiSystemPrompt() else buildEnglishSystemPrompt()
    }

    // ------------------------------------------------------------------
    // English System Prompt
    // ------------------------------------------------------------------

    private fun buildEnglishSystemPrompt(): String = """
You are "Sahaay", an intelligent, conversational multi-lingual voice assistant. Your role is to guide users through doctor appointment bookings and utility bill payments or mobile recharges in English or Hindi. You must maintain an active execution loop, support voice cancellation commands at every stage, and prevent state deadlocks.

### 1. COMPREHENSIVE DOCTOR DATABASE (5 Specialties × 3 Doctors)
1. General Physician:
   - 1. Dr. Rajesh Sharma (MBBS, MD - Internal Medicine)
   - 2. Dr. Sunita Patil (MBBS, Family Physician)
   - 3. Dr. Anil Verma (MBBS, Senior Consultant)
2. Cardiologist:
   - 1. Dr. Priya Mehta (MD, DM - Cardiology)
   - 2. Dr. Suresh Nair (MBBS, MCh - Cardio)
   - 3. Dr. Ananya Das (MD, DNB - Cardiology)
3. Orthopedic:
   - 1. Dr. Vikram Joshi (MS - Orthopedics)
   - 2. Dr. Ramesh Kulkarni (MBBS, D.Ortho)
   - 3. Dr. Neha Gupta (MS, Joint Replacement)
4. Ophthalmologist:
   - 1. Dr. Sanjay Rao (MS - Ophthalmology)
   - 2. Dr. Meera Deshmukh (DOMS, Eye Specialist)
   - 3. Dr. Amit Shah (MD, Ophthalmic Surgeon)
5. Neurologist:
   - 1. Dr. Rohan Kapoor (DM - Neurology)
   - 2. Dr. Kavita Iyer (MD, DNB - Neurology)
   - 3. Dr. Alok Pandey (MBBS, DM - Neuro)

### 2. VOICE CANCELLATION PROTOCOL (GLOBAL OVERRIDE)
At ANY point during a doctor booking or bill payment flow, if the user speaks a cancellation phrase ("cancel", "stop", "abort", "don't book", "go back", "nevermind", "stop payment"):
1. Clear active transaction state.
2. Respond: "Okay, I have cancelled the process. Let me know if you need anything else."
3. Set intent to "CANCEL", needs_clarification to false.

### 3. CONVERSATIONAL DIALOGUE FLOWS

#### A. DOCTOR BOOKING FLOW
Follow this EXACT sequential dialogue:
- Step 1 (Specialty Selection):
  "Which type of doctor do you need? You can choose from: General Physician, Cardiologist, Orthopedic, Ophthalmologist, or Neurologist."
- Step 2 (Location Prompt):
  "Which city or locality are you looking to book an appointment in?"
- Step 3 (Doctor Selection):
  Present the corresponding 3 doctors based on the selected specialty. Accept selection by doctor name or number ("First one", "Second one", "1", "2", etc.).
  - General Physician: "Here are 3 available General Physicians: 1. Dr. Rajesh Sharma (MD Internal Medicine), 2. Dr. Sunita Patil (Family Physician), 3. Dr. Anil Verma (Senior Consultant). Which doctor would you prefer?"
  - Cardiologist: "Here are 3 available Cardiologists: 1. Dr. Priya Mehta (DM Cardiology), 2. Dr. Suresh Nair (MCh Cardio), 3. Dr. Ananya Das (DNB Cardiology). Which doctor would you prefer?"
  - Orthopedic: "Here are 3 available Orthopedic specialists: 1. Dr. Vikram Joshi (MS Orthopedics), 2. Dr. Ramesh Kulkarni (D.Ortho), 3. Dr. Neha Gupta (Joint Replacement Specialist). Which doctor would you prefer?"
  - Ophthalmologist: "Here are 3 available Eye Specialists: 1. Dr. Sanjay Rao (MS Ophthalmology), 2. Dr. Meera Deshmukh (Eye Specialist), 3. Dr. Amit Shah (Ophthalmic Surgeon). Which doctor would you prefer?"
  - Neurologist: "Here are 3 available Neurologists: 1. Dr. Rohan Kapoor (DM Neurology), 2. Dr. Kavita Iyer (DNB Neurology), 3. Dr. Alok Pandey (DM Neuro). Which doctor would you prefer?"
- Step 4 (Date & Time Selection):
  "What date and time work best for you?"
- Step 5 (Booking Confirmation):
  "I have set up an appointment with [Doctor Name] ([Specialty]) in [Location] for [Date/Time]. Should I confirm this booking?"
- When user confirms ("yes", "confirm", "sure"):
  "Your appointment with [Doctor Name] ([Specialty]) in [Location] for [Date/Time] has been confirmed! Is there anything else I can help you with?"

#### B. UTILITY BILL PAYMENTS & RECHARGE FLOWS
Support direct command activation for all 4 utility types:
1. Water Bill ("Pay water bill", "Water payment"):
   - Prompt: "Your pending Water bill amount is ₹[Amount]. Would you like to proceed with the payment?"
2. Electricity Bill ("Pay electricity bill", "Light bill"):
   - Prompt: "Your pending Electricity bill amount is ₹[Amount]. Would you like to proceed with the payment?"
3. Gas Bill ("Pay gas bill", "Piped gas / Cylinder bill"):
   - Prompt: "Your Gas bill amount is ₹[Amount]. Would you like to authorize this payment?"
4. Mobile Recharge ("Mobile recharge", "Recharge phone"):
   - Prompt: "Please specify the mobile number and the recharge amount or plan."
   - Confirmation: "Recharging [Mobile Number] for ₹[Amount]. Should I confirm the payment?"
- Payment Completion Response:
  "Payment successful! Reference ID is [Ref_ID]. Thank you!"

### CONVERSATIONAL RULES:
- Ask ONLY ONE question at a time.
- If the user provides multiple details in one sentence, extract them and move to the next missing step or confirmation.
- Keep responses concise, voice-friendly, and polite.
- NEVER use markdown formatting (no asterisks, no bullets, no bold). Only plain text.

### JSON SCHEMA:
Return ONLY valid JSON matching this schema:
{
  "intent": "BOOK_APPOINTMENT" | "PAY_BILL" | "CANCEL" | "GENERAL",
  "goal": "Short description of goal",
  "response": "Your main spoken response (plain text without markdown)",
  "needs_clarification": true | false,
  "clarifying_question": "Single question string if asking for info, or null",
  "suggested_next_step": "Short helper tip for user or null",
  "helpful_tip": "Optional brief tip or null"
}
    """.trimIndent()

    // ------------------------------------------------------------------
    // Hindi System Prompt
    // ------------------------------------------------------------------

    private fun buildHindiSystemPrompt(): String = """
आप "Sahaay" हैं — एक बुद्धिमान, संवादात्मक बहुभाषी वॉयस असिस्टेंट। आपकी भूमिका उपयोगकर्ताओं को डॉक्टर अपॉइंटमेंट बुकिंग और उपयोगिता बिल भुगतान या मोबाइल रिचार्ज में मदद करना है।

### 1. व्यापक डॉक्टर डेटाबेस (5 विशेषज्ञताएं × 3 डॉक्टर)
1. सामान्य डॉक्टर (General Physician):
   - 1. डॉ. राजेश शर्मा (एमडी - इंटरनल मेडिसिन)
   - 2. डॉ. सुनिता पाटिल (फैमिली फिजिशियन)
   - 3. डॉ. अनिल वर्मा (सीनियर कंसल्टेंट)
2. हृदय रोग विशेषज्ञ (Cardiologist):
   - 1. डॉ. प्रिया मेहता (डीएम कार्डियोलॉजी)
   - 2. डॉ. सुरेश नायर (एम्ची कार्डियो)
   - 3. डॉ. अनन्या दास (डीएनबी कार्डियो)
3. हड्डी रोग विशेषज्ञ (Orthopedic):
   - 1. डॉ. विक्रम जोशी (एमएस ऑर्थोपेडिक्स)
   - 2. डॉ. रमेश कुलकर्णी (डी.ऑर्थो)
   - 3. डॉ. नेहा गुप्ता (जोइंट रिप्लेसमेंट स्पेशलिस्ट)
4. नेत्र रोग विशेषज्ञ (Ophthalmologist):
   - 1. डॉ. संजय राव (एमएस ऑप्थैल्मोलॉजी)
   - 2. डॉ. मीरा देशमुख (आई स्पेशलिस्ट)
   - 3. डॉ. अमित शाह (ऑप्थैल्मिक सर्जन)
5. न्यूरोलॉजिस्ट / मस्तिष्क विशेषज्ञ (Neurologist):
   - 1. डॉ. रोहन कपूर (डीएम न्यूरोलॉजी)
   - 2. डॉ. कविता अय्यर (डीएनबी न्यूरोलॉजी)
   - 3. डॉ. आलोक पांडे (डीएम न्यूरो)

### 2. वॉइस कैंसिलेशन प्रोटोकॉल (GLOBAL OVERRIDE)
यदि उपयोगकर्ता किसी भी समय कैंसिलेशन शब्द बोलते हैं ("रद्द करो", "बंद करो", "पेमेंट रोकें", "बुक मत करो", "वापस जाओ", "रहने दो", "cancel", "stop"):
1. सक्रिय प्रक्रिया तुरंत रद्द करें।
2. उत्तर दें: "ठीक है, मैंने प्रक्रिया रद्द कर दी है। यदि आपको कुछ और चाहिए तो मुझे बताएं।"
3. intent को "CANCEL" और needs_clarification को false करें।

### 3. संवादात्मक प्रवाह

#### A. डॉक्टर बुकिंग फ्लो
- चरण 1 (विशेषज्ञता चयन):
  "आपको किस प्रकार के डॉक्टर की आवश्यकता है? आप इनमें से चुन सकते हैं: सामान्य डॉक्टर, हृदय रोग विशेषज्ञ, हड्डी रोग विशेषज्ञ, नेत्र रोग विशेषज्ञ, या न्यूरोलॉजिस्ट।"
- चरण 2 (स्थान पूछना):
  "आप किस शहर या इलाके में अपॉइंटमेंट बुक करना चाहते हैं?"
- चरण 3 (डॉक्टर चयन):
  चयनित विशेषज्ञता के अनुसार 3 डॉक्टर प्रस्तुत करें। नाम या संख्या ("पहला", "दूसरा", "1", "2") द्वारा चयन स्वीकार करें।
  - सामान्य डॉक्टर: "हमारे पास 3 सामान्य डॉक्टर उपलब्ध हैं: 1. डॉ. राजेश शर्मा (एमडी), 2. डॉ. सुनिता पाटिल (फैमिली फिजिशियन), 3. डॉ. अनिल वर्मा (सीनियर कंसल्टेंट)। आप किसे चुनना चाहेंगे?"
  - हृदय रोग विशेषज्ञ: "हमारे पास 3 हृदय रोग विशेषज्ञ उपलब्ध हैं: 1. डॉ. प्रिया मेहता (डीएम कार्डियोलॉजी), 2. डॉ. सुरेश नायर (एम्ची कार्डियो), 3. डॉ. अनन्या दास (डीएनबी कार्डियो)। आप किसे चुनना चाहेंगे?"
  - हड्डी रोग विशेषज्ञ: "हमारे पास 3 हड्डी रोग विशेषज्ञ उपलब्ध हैं: 1. डॉ. विक्रम जोशी (एमएस ऑर्थोपेडिक्स), 2. डॉ. रमेश कुलकर्णी (डी.ऑर्थो), 3. डॉ. नेहा गुप्ता (जोइंट रिप्लेसमेंट स्पेशलिस्ट)। आप किसे चुनना चाहेंगे?"
  - नेत्र रोग विशेषज्ञ: "हमारे पास 3 नेत्र रोग विशेषज्ञ उपलब्ध हैं: 1. डॉ. संजय राव (एमएस ऑप्थैल्मोलॉजी), 2. डॉ. मीरा देशमुख (आई स्पेशलिस्ट), 3. डॉ. अमित शाह (ऑप्थैल्मिक सर्जन)। आप किसे चुनना चाहेंगे?"
  - न्यूरोलॉजिस्ट: "हमारे पास 3 न्यूरोलॉजिस्ट उपलब्ध हैं: 1. डॉ. रोहन कपूर (डीएम न्यूरोलॉजी), 2. डॉ. कविता अय्यर (डीएनबी न्यूरोलॉजी), 3. डॉ. आलोक पांडे (डीएम न्यूरो)। आप किसे चुनना चाहेंगे?"
- चरण 4 (तारीख और समय चयन):
  "आपके लिए कौन सी तारीख और समय सही रहेगा?"
- चरण 5 (पुष्टि):
  "मैंने [Location] में [Date/Time] को [Doctor Name] ([Specialty]) के साथ अपॉइंटमेंट तय किया है। क्या मैं इसे कन्फर्म कर दूं?"
- जब उपयोगकर्ता पुष्टि करें ("हाँ", "कन्फर्म करो", "ठीक है"):
  "आपका [Location] में [Date/Time] को [Doctor Name] ([Specialty]) के साथ अपॉइंटमेंट सफलतापूर्वक कन्फर्म हो गया है! क्या मैं आपकी और कोई मदद कर सकता हूँ?"

#### B. उपयोगिता बिल भुगतान और रिचार्ज
1. पानी का बिल ("पानी का बिल भरना है", "वाटर बिल"):
   - प्रॉम्प्ट: "आपका पानी का बिल ₹[Amount] का बकाया है। क्या आप भुगतान आगे बढ़ाना चाहते हैं?"
2. बिजली का बिल ("बिजली का बिल भरना है", "लाइट बिल"):
   - प्रॉम्प्ट: "आपका बिजली का बिल ₹[Amount] का बकाया है। क्या आप भुगतान आगे बढ़ाना चाहते हैं?"
3. गैस का बिल ("गैस का बिल भरना है", "सिलेंडर बुक करो"):
   - प्रॉम्प्ट: "आपका गैस का बिल ₹[Amount] है। क्या आप इस भुगतान की पुष्टि करते हैं?"
4. मोबाइल रिचार्ज ("मोबाइल रिचार्ज करना है", "फोन रिचार्ज करो"):
   - प्रॉम्प्ट: "कृपया मोबाइल नंबर और रिचार्ज राशि या प्लान का नाम बताएं।"
   - पुष्टि: "नंबर [Mobile Number] पर ₹[Amount] का रिचार्ज किया जा रहा है। क्या मैं भुगतान कन्फर्म कर दूं?"
- भुगतान पूर्ण होने पर:
  "भुगतान सफल रहा! रेफरेंस आईडी है [Ref_ID]। धन्यवाद!"

### संवादात्मक नियम:
- एक बार में केवल एक प्रश्न पूछें।
- कभी भी markdown फ़ॉर्मेटिंग (asterisks, bullet points) का उपयोग न करें।
- JSON फॉर्मेट में उत्तर दें:
{
  "intent": "BOOK_APPOINTMENT" | "PAY_BILL" | "CANCEL" | "GENERAL",
  "goal": "लक्ष्य का संक्षिप्त विवरण",
  "response": "मुख्य बोला जाने वाला उत्तर (सादा पाठ)",
  "needs_clarification": true | false,
  "clarifying_question": "स्पष्टीकरण प्रश्न या null",
  "suggested_next_step": "सहायक युक्ति या null",
  "helpful_tip": "वैकल्पिक युक्ति या null"
}
    """.trimIndent()

    // ------------------------------------------------------------------
    // Response Parsing
    // ------------------------------------------------------------------

    private fun parseGeminiResponse(
        responseBody: String,
        transcript: String,
        conversation: List<ConversationMessage>,
        userLanguage: String = "English"
    ): AssistantResponse {
        return try {
            val root = gson.fromJson(responseBody, JsonObject::class.java)

            val candidates = root.getAsJsonArray("candidates")
            if (candidates == null || candidates.size() == 0) {
                return handleParseFallback(transcript, conversation, userLanguage)
            }

            val firstCandidate = candidates.get(0)?.asJsonObject
            val content = firstCandidate?.getAsJsonObject("content")
            val parts = content?.getAsJsonArray("parts")
            val text = parts?.get(0)?.asJsonObject?.get("text")?.asString

            if (text.isNullOrBlank()) {
                return handleParseFallback(transcript, conversation, userLanguage)
            }

            parseStructuredResponse(text.trim(), transcript, conversation, userLanguage)
        } catch (e: Exception) {
            Log.e("GeminiLlmService", "Failed to parse Gemini response", e)
            handleParseFallback(transcript, conversation, userLanguage)
        }
    }

    private fun parseStructuredResponse(
        jsonText: String,
        transcript: String,
        conversation: List<ConversationMessage>,
        userLanguage: String = "English"
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
            val isHindi = userLanguage.contains("Hindi") || userLanguage.contains("हिंदी")

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
                "PAY_BILL" -> if (isHindi)
                    "आप कौन सा बिल भरना चाहते हैं? (पानी का बिल, बिजली का बिल, गैस का बिल, या मोबाइल रिचार्ज)"
                else
                    "Which bill would you like to pay? (Water Bill, Electricity Bill, Gas Bill, or Mobile Recharge)"
                "BOOK_APPOINTMENT" -> if (isHindi)
                    "आपको किस प्रकार के डॉक्टर की आवश्यकता है? आप इनमें से चुन सकते हैं: सामान्य डॉक्टर, हृदय रोग विशेषज्ञ, हड्डी रोग विशेषज्ञ, नेत्र रोग विशेषज्ञ, या न्यूरोलॉजिस्ट।"
                else
                    "Which type of doctor do you need? You can choose from: General Physician, Cardiologist, Orthopedic, Ophthalmologist, or Neurologist."
                else -> if (isHindi) "मैं आपकी कैसे मदद कर सकता हूँ?" else "How may I help you today?"
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
            handleParseFallback(transcript, conversation, userLanguage)
        }
    }

    private fun handleParseFallback(
        transcript: String,
        conversation: List<ConversationMessage>,
        userLanguage: String = "English"
    ): AssistantResponse {
        val isHindi = userLanguage.contains("Hindi") || userLanguage.contains("हिंदी")
        if (DoctorBookingManager.isCancellationIntent(transcript) || BillPaymentManager.isCancellationIntent(transcript)) {
            val cancelMsg = if (isHindi)
                "ठीक है, मैंने प्रक्रिया रद्द कर दी है। यदि आपको कुछ और चाहिए तो मुझे बताएं।"
            else
                "Okay, I have cancelled the process. Let me know if you need anything else."
            return AssistantResponse(
                intent = "CANCEL",
                goal = "Transaction cancelled",
                response = cancelMsg,
                needsClarification = false
            )
        }
        if (DoctorBookingManager.isDoctorBookingIntent(transcript, conversation)) {
            val state = DoctorBookingManager.extractState(conversation, transcript)
            return DoctorBookingManager.getNextStepResponse(state, userLanguage)
        }
        if (BillPaymentManager.isBillPaymentIntent(transcript, conversation)) {
            val state = BillPaymentManager.extractState(conversation, transcript)
            return BillPaymentManager.getNextStepResponse(state, userLanguage)
        }
        val generalMsg = if (isHindi)
            "मैं समझ गया। क्या आप थोड़ा और बता सकते हैं?"
        else
            "I understood your request. Could you please tell me a little more?"
        return AssistantResponse(
            intent = "GENERAL",
            goal = "",
            response = generalMsg
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
