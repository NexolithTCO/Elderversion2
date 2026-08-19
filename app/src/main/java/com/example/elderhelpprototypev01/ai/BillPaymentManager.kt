package com.example.elderhelpprototypev01.ai

import com.example.elderhelpprototypev01.model.AssistantResponse
import com.example.elderhelpprototypev01.model.BillType
import com.example.elderhelpprototypev01.model.ConversationMessage
import com.example.elderhelpprototypev01.model.MessageRole
import java.util.Locale

/**
 * BillPaymentManager
 *
 * Dedicated manager for the Pay Bills conversation flow.
 * Handles slot extraction and provides deterministic, voice-friendly
 * step-by-step guidance following the exact 5-step flow:
 *
 * 1. Bill Category / Type (Electricity, Water, or Mobile Recharge)
 * 2. Account Identifier / Details (Consumer ID, Meter Number, or Mobile Number)
 * 3. Provider / Operator (e.g., Adani Electricity, Tata Power, Jio, Airtel)
 * 4. Amount (e.g., ₹500, 299 rupees)
 * 5. Confirmation ("I have set up a payment of ₹[Amount] for your [Bill Type] ([Provider], ID: [Account ID]). Should I proceed to payment?")
 */
object BillPaymentManager {

    data class PaymentFlowState(
        var billType: BillType? = null,
        var accountId: String? = null,
        var provider: String? = null,
        var amount: String? = null,
        var isConfirmed: Boolean = false
    )

    private val BILL_PAYMENT_INTENT_KEYWORDS = listOf(
        // English keywords
        "pay bill", "pay my bill", "pay bills", "bill payment",
        "recharge", "recharge my phone", "recharge phone", "recharge mobile", "mobile recharge",
        "phone recharge", "electricity bill", "water bill", "gas bill", "bijli bill", "light bill",
        "current bill", "phone bill", "pay electricity", "pay water", "pay mobile", "pay gas",
        "electricity payment", "water payment", "mobile payment", "gas payment",
        "lpg", "cylinder", "piped gas", "mahanagar gas", "igl", "hp gas", "bharat gas", "indane",
        // Hindi keywords
        "बिल भरना", "बिजली का बिल", "पानी का बिल", "गैस बिल", "गैस का बिल", "मोबाइल रिचार्ज", "रिचार्ज करना",
        "बिल का भुगतान", "बिल पे करना", "बिजली बिल", "भुगतान करना", "गैस", "सिलेंडर", "एलपीजी"
    )

    /**
     * Checks if the transcript or history indicates a bill payment / recharge intent.
     */
    fun isBillPaymentIntent(transcript: String, conversation: List<ConversationMessage>): Boolean {
        val lowerTranscript = transcript.lowercase(Locale.ROOT)
        if (BILL_PAYMENT_INTENT_KEYWORDS.any { lowerTranscript.contains(it) }) {
            return true
        }

        // Check if ANY recent message in the conversation relates to bill payment
        val recentMessages = conversation.takeLast(10)
        for (msg in recentMessages) {
            val textLower = msg.text.lowercase(Locale.ROOT)
            if (BILL_PAYMENT_INTENT_KEYWORDS.any { textLower.contains(it) }) {
                return true
            }
            if (textLower.contains("which bill would you like to pay") ||
                textLower.contains("consumer or account id") ||
                textLower.contains("water consumer") ||
                textLower.contains("gas provider") ||
                textLower.contains("which gas provider") ||
                textLower.contains("mobile number would you like to recharge") ||
                textLower.contains("service provider or operator") ||
                textLower.contains("how much amount would you like to pay") ||
                textLower.contains("should i proceed to payment") ||
                textLower.contains("set up a payment of") ||
                textLower.contains("payment of ₹") ||
                textLower.contains("processed successfully") ||
                textLower.contains("paid successfully") ||
                textLower.contains("कौन सा बिल") ||
                textLower.contains("उपभोक्ता आईडी") ||
                textLower.contains("वाटर मीटर") ||
                textLower.contains("कंज्यूमर नंबर") ||
                textLower.contains("एलपीजी आईडी") ||
                textLower.contains("गैस कंपनी") ||
                textLower.contains("गैस प्रदाता") ||
                textLower.contains("मोबाइल नंबर पर रिचार्ज") ||
                textLower.contains("कंपनी का नाम") ||
                textLower.contains("आगे बढ़ूं") ||
                textLower.contains("सफलतापूर्वक हो गया")
            ) {
                return true
            }
        }
        return false
    }

    /**
     * Reconstructs the current payment state by analyzing the full conversation + new transcript.
     */
    fun extractState(conversation: List<ConversationMessage>, currentTranscript: String = ""): PaymentFlowState {
        val state = PaymentFlowState()

        var lastQuestion = ""
        for (msg in conversation) {
            if (msg.role == MessageRole.USER) {
                parseUserUtterance(msg.text, lastQuestion, state)
            } else {
                val assistantText = msg.text.lowercase(Locale.ROOT)
                lastQuestion = assistantText
                // Check if assistant already acknowledged successful processing
                if (assistantText.contains("processed successfully") ||
                    assistantText.contains("has been processed") ||
                    assistantText.contains("paid successfully") ||
                    assistantText.contains("सफलतापूर्वक हो गया") ||
                    assistantText.contains("सफलतापूर्वक भुगतान")
                ) {
                    state.isConfirmed = true
                }
                parseAssistantText(msg.text, state)
            }
        }

        if (currentTranscript.isNotBlank() && conversation.lastOrNull()?.text != currentTranscript) {
            parseUserUtterance(currentTranscript, lastQuestion, state)
        }

        // Fill default provider if missing but billType is recognized
        if (state.provider == null && state.billType != null) {
            state.provider = state.billType!!.defaultProvider
        }

        return state
    }

    private fun parseAssistantText(text: String, state: PaymentFlowState) {
        val lower = text.lowercase(Locale.ROOT)
        // Extract bill type if missing
        if (state.billType == null) {
            when {
                lower.contains("electricity") || lower.contains("bijli") || lower.contains("बिजली") -> state.billType = BillType.ELECTRICITY
                lower.contains("water") || lower.contains("पानी") -> state.billType = BillType.WATER
                lower.contains("mobile") || lower.contains("recharge") || lower.contains("मोबाइल") -> state.billType = BillType.MOBILE
                lower.contains("gas") || lower.contains("lpg") || lower.contains("गैस") -> state.billType = BillType.GAS
            }
        }
        // Extract amount if missing (e.g. ₹1450 or ₹500 or 1450)
        if (state.amount == null) {
            val amountRegex = Regex("(?:₹|rs\\.?|rupees|रुपये)\\s*(\\d+(?:\\.\\d{1,2})?)|(\\d+(?:\\.\\d{1,2})?)\\s*(?:₹|rs\\.?|rupees|रुपये)")
            val match = amountRegex.find(text)
            if (match != null) {
                state.amount = (match.groups[1] ?: match.groups[2])?.value
            }
        }
        // Extract provider if mentioned in parenthesis or text e.g. "(Adani Electricity)"
        if (state.provider == null) {
            when {
                lower.contains("adani electricity") -> state.provider = "Adani Electricity"
                lower.contains("tata power") -> state.provider = "Tata Power"
                lower.contains("mahanagar gas") -> state.provider = "Mahanagar Gas"
                lower.contains("hp gas") -> state.provider = "HP Gas"
                lower.contains("bharat gas") -> state.provider = "Bharat Gas"
                lower.contains("indane") -> state.provider = "Indane Gas"
                lower.contains("jio") -> state.provider = "Jio Prepaid"
                lower.contains("airtel") -> state.provider = "Airtel"
                lower.contains("water board") -> state.provider = "Municipal Corporation Water Board"
            }
        }
    }

    private fun parseUserUtterance(text: String, lastQuestion: String, state: PaymentFlowState) {
        val lower = text.lowercase(Locale.ROOT).trim()

        // 1. Confirmation check
        val isConfirmationQuestion = lastQuestion.contains("should i proceed to payment") ||
                lastQuestion.contains("proceed to payment") ||
                lastQuestion.contains("आगे बढ़ूं") ||
                lastQuestion.contains("confirm") ||
                lastQuestion.contains("कन्फर्म") ||
                lastQuestion.contains("payment of")

        if (isConfirmationQuestion || (state.billType != null && state.amount != null)) {
            if (lower.contains("yes") || lower.contains("proceed") || lower.contains("sure") ||
                lower.contains("confirm") || lower.contains("ha") || lower.contains("haa") ||
                lower.contains("haan") || lower.contains("हाँ") || lower.contains("हां") ||
                lower.contains("yep") || lower.contains("ok") || lower.contains("okay") ||
                lower.contains("pay") || lower.contains("correct") || lower.contains("do it") ||
                lower.contains("karo") || lower.contains("kardo") || lower.contains("करो") ||
                lower.contains("कर दो") || lower.contains("कर दीजिए")
            ) {
                state.isConfirmed = true
            }
        }


        // 2. Bill Type Extraction
        if (state.billType == null) {
            when {
                lower.contains("gas") || lower.contains("lpg") || lower.contains("cylinder") ||
                        lower.contains("piped") || lower.contains("गैस") || lower.contains("सिलेंडर") ||
                        lower.contains("mahanagar gas") || lower.contains("igl") || lower.contains("indane") -> {
                    state.billType = BillType.GAS
                }
                lower.contains("electricity") || lower.contains("bijli") || lower.contains("power") ||
                        lower.contains("light bill") || lower.contains("current bill") -> {
                    state.billType = BillType.ELECTRICITY
                }
                lower.contains("water") || lower.contains("pani") || lower.contains("jal") || lower.contains("meter") -> {
                    state.billType = BillType.WATER
                }
                lower.contains("mobile") || lower.contains("recharge") || lower.contains("phone") ||
                        lower.contains("sim") || lower.contains("prepaid") || lower.contains("postpaid") -> {
                    state.billType = BillType.MOBILE
                }
                lastQuestion.contains("which bill would you like to pay") || lastQuestion.contains("कौन सा बिल") -> {
                    if (lower.contains("first") || lower.contains("electric") || lower.contains("बिजली")) {
                        state.billType = BillType.ELECTRICITY
                    } else if (lower.contains("second") || lower.contains("water") || lower.contains("पानी")) {
                        state.billType = BillType.WATER
                    } else if (lower.contains("third") || lower.contains("mobile") || lower.contains("recharge") || lower.contains("मोबाइल")) {
                        state.billType = BillType.MOBILE
                    } else if (lower.contains("fourth") || lower.contains("gas") || lower.contains("गैस")) {
                        state.billType = BillType.GAS
                    }
                }
            }
        }

        // 3. Provider Extraction
        when {
            lower.contains("mahanagar gas") || lower.contains("mgl") || lower.contains("महानगर गैस") -> state.provider = "Mahanagar Gas"
            lower.contains("igl") || lower.contains("indraprastha gas") || lower.contains("आईजीएल") -> state.provider = "Indraprastha Gas (IGL)"
            lower.contains("hp gas") || lower.contains("एचपी गैस") || lower.contains("hindustan petroleum") -> state.provider = "HP Gas"
            lower.contains("bharat gas") || lower.contains("भारत गैस") || lower.contains("bpcl") -> state.provider = "Bharat Gas"
            lower.contains("indane") || lower.contains("इंडेन") || lower.contains("indian oil") -> state.provider = "Indane Gas"
            lower.contains("adani gas") || lower.contains("adani total gas") || (lower.contains("adani") && state.billType == BillType.GAS) -> state.provider = "Adani Total Gas"
            lower.contains("gujarat gas") -> state.provider = "Gujarat Gas"
            lower.contains("adani electricity") || lower.contains("adani") -> state.provider = "Adani Electricity"
            lower.contains("tata power") || (lower.contains("tata") && state.billType == BillType.ELECTRICITY) -> state.provider = "Tata Power"
            lower.contains("msedcl") || lower.contains("mahadiscom") -> state.provider = "MSEDCL"
            lower.contains("bescom") -> state.provider = "BESCOM"
            lower.contains("torrent power") || lower.contains("torrent") -> state.provider = "Torrent Power"
            lower.contains("bses yamuna") || lower.contains("bses rajdhani") || lower.contains("bses") -> state.provider = "BSES"
            lower.contains("jio") -> state.provider = "Jio"
            lower.contains("airtel") -> state.provider = "Airtel"
            lower.contains("vodafone") || lower.contains("idea") || lower.contains("vi") -> state.provider = "Vi"
            lower.contains("bsnl") -> state.provider = "BSNL"
            lower.contains("delhi jal board") -> state.provider = "Delhi Jal Board"
            lower.contains("bmc") -> state.provider = "Municipal Corporation (BMC)"
            lower.contains("water board") || lower.contains("jal board") -> state.provider = "Municipal Corporation Water Board"
            lastQuestion.contains("service provider or operator") || lastQuestion.contains("who is your service provider") ||
                    lastQuestion.contains("which gas provider") || lastQuestion.contains("गैस कंपनी") || lastQuestion.contains("कंपनी का नाम") -> {
                val cleaned = text.replace(Regex("(?i)^(my provider is|it is|operator is|provider is|is|the provider is|my gas provider is|गैस कंपनी है|कंपनी है)\\s+"), "").trim()
                if (cleaned.isNotBlank() && !cleaned.contains("rupees", ignoreCase = true) && !cleaned.contains("rs", ignoreCase = true) && !cleaned.contains("रुपये")) {
                    state.provider = cleaned.capitalizeWords()
                }
            }
        }

        // 4. Account Identifier / Details Extraction
        if (state.accountId == null) {
            if (state.billType == BillType.MOBILE || lower.contains("mobile") || lower.contains("phone") || lower.contains("recharge")) {
                // Look for 10-digit phone number
                val phoneMatch = Regex("\\b[6-9]\\d{9}\\b").find(text.replace(" ", ""))
                if (phoneMatch != null) {
                    state.accountId = phoneMatch.value
                    if (state.billType == null) state.billType = BillType.MOBILE
                } else if (lastQuestion.contains("mobile number would you like to recharge") || lastQuestion.contains("which mobile number")) {
                    val digits = text.filter { it.isDigit() }
                    if (digits.length >= 10) {
                        state.accountId = digits.takeLast(10)
                    } else if (digits.isNotBlank()) {
                        state.accountId = digits
                    }
                }
            } else if (lastQuestion.contains("consumer or account id") ||
                lastQuestion.contains("water consumer / meter number") ||
                lastQuestion.contains("consumer number or lpg id") ||
                lastQuestion.contains("consumer") ||
                lastQuestion.contains("account id") ||
                lastQuestion.contains("कंज्यूमर नंबर") ||
                lastQuestion.contains("उपभोक्ता आईडी") ||
                lastQuestion.contains("एलपीजी आईडी")
            ) {
                val cleaned = text.replace(Regex("(?i)^(my id is|my consumer id is|my account id is|my number is|it is|id is|number is|consumer number is|lpg id is|मेरा नंबर है|आईडी है)\\s+"), "").trim()
                if (cleaned.isNotBlank() && !cleaned.contains("adani", ignoreCase = true) && !cleaned.contains("jio", ignoreCase = true) && !cleaned.contains("mahanagar", ignoreCase = true)) {
                    state.accountId = cleaned
                }
            } else {
                // Generic ID pattern like WB-8839201 or 102938475 or 16-digit LPG ID
                val idMatch = Regex("(?i)\\b(id|consumer|meter|account|lpg)?\\s*[:#]?\\s*([A-Z0-9\\-]{6,17})\\b").find(text)
                if (idMatch != null) {
                    val candidate = idMatch.groupValues[2]
                    if (!candidate.equals("electricity", ignoreCase = true) &&
                        !candidate.equals("recharge", ignoreCase = true) &&
                        !candidate.equals("tomorrow", ignoreCase = true) &&
                        !candidate.equals("mahanagar", ignoreCase = true)
                    ) {
                        state.accountId = candidate
                    }
                }
            }
        }

        // 5. Amount Extraction
        if (state.amount == null) {
            val amountRegex = Regex("(?i)(?:₹|rs\\.?|rupees|रुपये|amount|for)\\s*(\\d+(?:\\.\\d{1,2})?)|(\\d+(?:\\.\\d{1,2})?)\\s*(?:₹|rs\\.?|rupees|रुपये)")
            val match = amountRegex.find(text)
            if (match != null) {
                state.amount = (match.groups[1] ?: match.groups[2])?.value
            } else if (lastQuestion.contains("how much amount") || lastQuestion.contains("amount would you like to pay") || lastQuestion.contains("कितने रुपये")) {
                val digits = text.filter { it.isDigit() || it == '.' }
                if (digits.isNotBlank()) {
                    state.amount = digits
                }
            }
        }
    }

    /**
     * Generates the next question or confirmation response in the exact 5-step sequence.
     *
     * @param state        Current payment state extracted from the conversation.
     * @param userLanguage Language preference from the ViewModel (e.g. "Hindi (हिंदी)").
     *                     When Hindi is active, all step prompts are returned in Hindi.
     */
    fun getNextStepResponse(state: PaymentFlowState, userLanguage: String = "English"): AssistantResponse {
        val isHindi = userLanguage.contains("Hindi") || userLanguage.contains("हिंदी")
        return if (isHindi) getNextStepResponseHindi(state) else getNextStepResponseEnglish(state)
    }

    private fun getNextStepResponseEnglish(state: PaymentFlowState): AssistantResponse {
        return when {
            // Step 1: Bill Category / Type (if not specified)
            state.billType == null -> {
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "Ask bill category",
                    response = "Which bill would you like to pay? (Electricity, Water, Mobile Recharge, or Gas Bill)",
                    needsClarification = true,
                    clarifyingQuestion = "Which bill would you like to pay? (Electricity, Water, Mobile Recharge, or Gas Bill)",
                    suggestedNextStep = "Say Electricity, Water, Mobile Recharge, or Gas Bill."
                )
            }

            // Step 2: Account Identifier / Details
            state.accountId == null -> {
                val (question, hint) = when (state.billType!!) {
                    BillType.ELECTRICITY -> "Please tell me your Consumer or Account ID." to "Say your electricity consumer or account ID."
                    BillType.WATER -> "Please tell me your Water Consumer / Meter Number." to "Say your water consumer or meter number."
                    BillType.MOBILE -> "Which mobile number would you like to recharge?" to "Say your 10-digit mobile number."
                    BillType.GAS -> "Please tell me your Consumer Number or LPG ID." to "Say your Consumer Number or LPG ID."
                }
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "Ask account identifier",
                    response = question,
                    needsClarification = true,
                    clarifyingQuestion = question,
                    suggestedNextStep = hint
                )
            }

            // Step 3: Provider / Operator (if applicable)
            state.provider == null -> {
                val prompt = when (state.billType!!) {
                    BillType.GAS -> "Which gas provider do you use? (e.g., Mahanagar Gas, IGL, HP Gas)"
                    else -> "Who is your service provider or operator? (e.g., Adani Electricity, Tata Power, Jio, Airtel)"
                }
                val hint = when (state.billType!!) {
                    BillType.GAS -> "Say Mahanagar Gas, IGL, HP Gas, Bharat Gas, or Indane."
                    else -> "Say your service provider or telecom operator."
                }
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "Ask service provider",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = hint
                )
            }

            // Step 4: Amount
            state.amount == null -> {
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "Ask payment amount",
                    response = "How much amount would you like to pay or recharge?",
                    needsClarification = true,
                    clarifyingQuestion = "How much amount would you like to pay or recharge?",
                    suggestedNextStep = "Say the amount to pay, for example 500 rupees."
                )
            }

            // Step 5: Confirmation
            !state.isConfirmed -> {
                val billTypeTitle = when (state.billType!!) {
                    BillType.ELECTRICITY -> "Electricity Bill"
                    BillType.WATER -> "Water Bill"
                    BillType.MOBILE -> "Mobile Recharge"
                    BillType.GAS -> "Gas Bill"
                }
                val summary = "I have set up a payment of ₹${state.amount} for your $billTypeTitle (${state.provider}, ID: ${state.accountId}). Should I proceed to payment?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "Confirm payment details",
                    response = summary,
                    needsClarification = true,
                    clarifyingQuestion = summary,
                    suggestedNextStep = "Say yes to proceed with payment, or tell me if you want to change any detail."
                )
            }

            // Completed / Confirmed
            else -> {
                val billTypeTitle = when (state.billType!!) {
                    BillType.ELECTRICITY -> "Electricity Bill"
                    BillType.WATER -> "Water Bill"
                    BillType.MOBILE -> "Mobile Recharge"
                    BillType.GAS -> "Gas Bill"
                }
                val confirmedMsg = "Your payment of ₹${state.amount} for $billTypeTitle (${state.provider}) has been processed successfully! Is there anything else I can help you with?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "Payment completed",
                    response = confirmedMsg,
                    needsClarification = false,
                    clarifyingQuestion = null,
                    suggestedNextStep = "You can ask to book a doctor appointment or pay another bill.",
                    helpfulTip = "The payment receipt has been recorded in your Sahaay history."
                )
            }
        }
    }

    private fun getNextStepResponseHindi(state: PaymentFlowState): AssistantResponse {
        return when {
            // चरण 1: बिल की श्रेणी
            state.billType == null -> {
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "बिल श्रेणी पूछें",
                    response = "आप कौन सा बिल भरना चाहते हैं? (बिजली बिल, पानी का बिल, मोबाइल रिचार्ज, या गैस बिल)",
                    needsClarification = true,
                    clarifyingQuestion = "आप कौन सा बिल भरना चाहते हैं? (बिजली बिल, पानी का बिल, मोबाइल रिचार्ज, या गैस बिल)",
                    suggestedNextStep = "बिजली बिल, पानी का बिल, मोबाइल रिचार्ज, या गैस बिल बोलें।"
                )
            }

            // चरण 2: खाता विवरण
            state.accountId == null -> {
                val (question, hint) = when (state.billType!!) {
                    BillType.ELECTRICITY ->
                        "कृपया अपना उपभोक्ता आईडी (Consumer ID) बताएं।" to "अपना बिजली उपभोक्ता आईडी बोलें।"
                    BillType.WATER ->
                        "कृपया अपना वाटर मीटर नंबर बताएं।" to "अपना पानी का मीटर नंबर बोलें।"
                    BillType.MOBILE ->
                        "आप किस मोबाइल नंबर पर रिचार्ज करना चाहते हैं?" to "अपना 10 अंकों का मोबाइल नंबर बोलें।"
                    BillType.GAS ->
                        "कृपया अपना कंज्यूमर नंबर या एलपीजी आईडी बताएं।" to "अपना गैस उपभोक्ता नंबर या एलपीजी आईडी बताएं।"
                }
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "खाता विवरण पूछें",
                    response = question,
                    needsClarification = true,
                    clarifyingQuestion = question,
                    suggestedNextStep = hint
                )
            }

            // चरण 3: सेवा प्रदाता और राशि
            state.provider == null -> {
                val (question, hint) = when (state.billType!!) {
                    BillType.GAS ->
                        "आपकी गैस कंपनी कौन सी है? (जैसे: महानगर गैस, एचपी गैस)" to "महानगर गैस, एचपी गैस, भारत गैस या इंडेन बताएं।"
                    else ->
                        "आपकी कंपनी का नाम क्या है और आप कितने रुपये का भुगतान करना चाहते हैं?" to "अपनी कंपनी का नाम और भुगतान राशि बताएं।"
                }
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "सेवा प्रदाता पूछें",
                    response = question,
                    needsClarification = true,
                    clarifyingQuestion = question,
                    suggestedNextStep = hint
                )
            }

            // चरण 4: राशि (यदि provider के साथ amount नहीं मिली)
            state.amount == null -> {
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "भुगतान राशि पूछें",
                    response = "आप कितने रुपये का भुगतान करना चाहते हैं?",
                    needsClarification = true,
                    clarifyingQuestion = "आप कितने रुपये का भुगतान करना चाहते हैं?",
                    suggestedNextStep = "भुगतान राशि बताएं, जैसे 500 रुपये।"
                )
            }

            // चरण 5: पुष्टि
            !state.isConfirmed -> {
                val billTypeTitle = when (state.billType!!) {
                    BillType.ELECTRICITY -> "बिजली बिल"
                    BillType.WATER -> "पानी का बिल"
                    BillType.MOBILE -> "मोबाइल रिचार्ज"
                    BillType.GAS -> "गैस बिल"
                }
                val summary = "मैं आपके $billTypeTitle के लिए ₹${state.amount} का भुगतान करने जा रहा हूं (${state.provider}, ID: ${state.accountId})। क्या मैं आगे बढ़ूं?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "भुगतान विवरण की पुष्टि करें",
                    response = summary,
                    needsClarification = true,
                    clarifyingQuestion = summary,
                    suggestedNextStep = "भुगतान के लिए 'हाँ' बोलें, या यदि कोई बदलाव चाहते हैं तो बताएं।"
                )
            }

            // पूर्ण / कन्फर्म
            else -> {
                val billTypeTitle = when (state.billType!!) {
                    BillType.ELECTRICITY -> "बिजली बिल"
                    BillType.WATER -> "पानी का बिल"
                    BillType.MOBILE -> "मोबाइल रिचार्ज"
                    BillType.GAS -> "गैस बिल"
                }
                val confirmedMsg = "आपका ₹${state.amount} का $billTypeTitle भुगतान (${state.provider}) सफलतापूर्वक हो गया! क्या मैं आपकी और कोई मदद कर सकता हूँ?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "भुगतान पूर्ण",
                    response = confirmedMsg,
                    needsClarification = false,
                    clarifyingQuestion = null,
                    suggestedNextStep = "आप कोई और बिल भर सकते हैं या डॉक्टर का अपॉइंटमेंट बुक कर सकते हैं।",
                    helpfulTip = "भुगतान की रसीद आपके Sahaay इतिहास में दर्ज हो गई है।"
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
