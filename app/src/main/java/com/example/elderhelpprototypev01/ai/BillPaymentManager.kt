package com.example.elderhelpprototypev01.ai

import com.example.elderhelpprototypev01.model.AssistantResponse
import com.example.elderhelpprototypev01.model.BillType
import com.example.elderhelpprototypev01.model.ConversationMessage
import com.example.elderhelpprototypev01.model.MessageRole
import java.util.Locale

/**
 * BillPaymentManager
 *
 * Dedicated manager for the Pay Bills & Mobile Recharge conversation flow.
 * Handles slot extraction and provides deterministic, voice-friendly
 * step-by-step guidance following the exact flows:
 *
 * 1. Water Bill: "Your pending Water bill amount is ₹[Amount]. Would you like to proceed with the payment?"
 * 2. Electricity Bill: "Your pending Electricity bill amount is ₹[Amount]. Would you like to proceed with the payment?"
 * 3. Gas Bill: "Your Gas bill amount is ₹[Amount]. Would you like to authorize this payment?"
 * 4. Mobile Recharge: "Please specify the mobile number and the recharge amount or plan." -> "Recharging [Mobile Number] for ₹[Amount]. Should I confirm the payment?"
 * 5. Payment Completion: "Payment successful! Reference ID is [Ref_ID]. Thank you!"
 */
object BillPaymentManager {

    data class PaymentFlowState(
        var billType: BillType? = null,
        var accountId: String? = null,
        var provider: String? = null,
        var amount: String? = null,
        var isConfirmed: Boolean = false,
        var isCancelled: Boolean = false,
        var refId: String? = null
    )

    private val BILL_PAYMENT_INTENT_KEYWORDS = listOf(
        // English keywords
        "pay bill", "pay my bill", "pay bills", "bill payment",
        "recharge", "recharge my phone", "recharge phone", "recharge mobile", "mobile recharge",
        "phone recharge", "electricity bill", "water bill", "gas bill", "bijli bill", "light bill",
        "current bill", "phone bill", "pay electricity", "pay water", "pay mobile", "pay gas",
        "pay electricity bill", "pay water bill", "pay gas bill", "water payment",
        "electricity payment", "gas payment", "piped gas", "cylinder bill",
        "lpg", "cylinder", "mahanagar gas", "igl", "hp gas", "bharat gas", "indane",
        // Hindi keywords
        "पानी का बिल", "पानी का बिल भरना है", "वाटर बिल",
        "बिजली का बिल", "बिजली का बिल भरना है", "लाइट बिल",
        "गैस का बिल", "गैस का बिल भरना है", "सिलेंडर बुक करो",
        "मोबाइल रिचार्ज", "मोबाइल रिचार्ज करना है", "फोन रिचार्ज करो",
        "बिल भरना", "बिल का भुगतान", "बिल पे करना", "बिजली बिल", "भुगतान करना", "गैस", "सिलेंडर", "एलपीजी"
    )

    private val CANCELLATION_TRIGGERS = listOf(
        "cancel", "stop", "abort", "don't book", "dont book", "go back", "nevermind", "never mind", "stop payment",
        "रद्द करो", "रद्द करें", "बंद करो", "पेमेंट रोकें", "पेमेंट रोको", "बुक मत करो", "वापस जाओ", "रहने दो"
    )

    fun isCancellationIntent(transcript: String): Boolean {
        val lower = transcript.lowercase(Locale.ROOT).trim()
        return CANCELLATION_TRIGGERS.any { lower.contains(it) }
    }

    /**
     * Checks if the transcript or history indicates a bill payment / recharge intent.
     */
    fun isBillPaymentIntent(transcript: String, conversation: List<ConversationMessage>): Boolean {
        val lowerTranscript = transcript.lowercase(Locale.ROOT)
        if (BILL_PAYMENT_INTENT_KEYWORDS.any { lowerTranscript.contains(it) }) {
            return true
        }

        val recentMessages = conversation.takeLast(10)
        for (msg in recentMessages) {
            val textLower = msg.text.lowercase(Locale.ROOT)
            if (BILL_PAYMENT_INTENT_KEYWORDS.any { textLower.contains(it) }) {
                return true
            }
            if (textLower.contains("pending water bill amount") ||
                textLower.contains("pending electricity bill amount") ||
                textLower.contains("gas bill amount is") ||
                textLower.contains("specify the mobile number and the recharge amount") ||
                textLower.contains("should i confirm the payment") ||
                textLower.contains("payment successful") ||
                textLower.contains("पानी का बिल ₹") ||
                textLower.contains("बिजली का बिल ₹") ||
                textLower.contains("गैस का बिल ₹") ||
                textLower.contains("मोबाइल नंबर और रिचार्ज राशि") ||
                textLower.contains("भुगतान सफल रहा")
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
                if (isCancellationIntent(msg.text)) {
                    state.isCancelled = true
                } else {
                    parseUserUtterance(msg.text, lastQuestion, state)
                }
            } else {
                val assistantText = msg.text.lowercase(Locale.ROOT)
                lastQuestion = assistantText
                if (assistantText.contains("payment successful") ||
                    assistantText.contains("भुगतान सफल रहा") ||
                    assistantText.contains("processed successfully") ||
                    assistantText.contains("paid successfully")
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

        // Fill default amount and provider if billType is set
        if (state.billType != null) {
            if (state.amount == null) state.amount = state.billType!!.defaultAmount
            if (state.provider == null) state.provider = state.billType!!.defaultProvider
        }

        if (state.refId == null) {
            state.refId = "PAY-${(System.currentTimeMillis() % 100000).toString().padStart(5, '0')}"
        }

        return state
    }

    private fun parseAssistantText(text: String, state: PaymentFlowState) {
        val lower = text.lowercase(Locale.ROOT)
        // Extract bill type if missing
        if (state.billType == null) {
            when {
                lower.contains("electricity") || lower.contains("bijli") || lower.contains("बिजली") || lower.contains("light") -> state.billType = BillType.ELECTRICITY
                lower.contains("water") || lower.contains("पानी") -> state.billType = BillType.WATER
                lower.contains("mobile") || lower.contains("recharge") || lower.contains("मोबाइल") -> state.billType = BillType.MOBILE
                lower.contains("gas") || lower.contains("lpg") || lower.contains("गैस") || lower.contains("cylinder") || lower.contains("सिलेंडर") -> state.billType = BillType.GAS
            }
        }
        // Extract amount if mentioned
        if (state.amount == null) {
            val amountRegex = Regex("(?:₹|rs\\.?|rupees|रुपये)\\s*(\\d+(?:\\.\\d{1,2})?)|(\\d+(?:\\.\\d{1,2})?)\\s*(?:₹|rs\\.?|rupees|रुपये)")
            val match = amountRegex.find(text)
            if (match != null) {
                state.amount = (match.groups[1] ?: match.groups[2])?.value
            }
        }
    }

    private fun parseUserUtterance(text: String, lastQuestion: String, state: PaymentFlowState) {
        val lower = text.lowercase(Locale.ROOT).trim()

        // 1. Confirmation check
        val isConfirmationQuestion = lastQuestion.contains("would you like to proceed with the payment") ||
                lastQuestion.contains("would you like to authorize this payment") ||
                lastQuestion.contains("should i confirm the payment") ||
                lastQuestion.contains("should i proceed to payment") ||
                lastQuestion.contains("क्या आप भुगतान आगे बढ़ाना चाहते हैं") ||
                lastQuestion.contains("क्या आप इस भुगतान की पुष्टि करते हैं") ||
                lastQuestion.contains("क्या मैं भुगतान कन्फर्म कर दूं") ||
                lastQuestion.contains("कन्फर्म")

        if (isConfirmationQuestion || (state.billType != null && state.amount != null && (state.billType != BillType.MOBILE || state.accountId != null))) {
            if (lower.contains("yes") || lower.contains("proceed") || lower.contains("sure") ||
                lower.contains("confirm") || lower.contains("ha") || lower.contains("haa") ||
                lower.contains("haan") || lower.contains("हाँ") || lower.contains("हां") ||
                lower.contains("yep") || lower.contains("ok") || lower.contains("okay") ||
                lower.contains("pay") || lower.contains("correct") || lower.contains("do it") ||
                lower.contains("karo") || lower.contains("kardo") || lower.contains("करो") ||
                lower.contains("कर दो") || lower.contains("कर दीजिए")
            ) {
                state.isConfirmed = true
                return
            }
        }

        // 2. Bill Type Extraction
        if (state.billType == null) {
            when {
                lower.contains("water") || lower.contains("pani") || lower.contains("पानी") -> {
                    state.billType = BillType.WATER
                    if (state.amount == null) state.amount = "480"
                }
                lower.contains("electricity") || lower.contains("bijli") || lower.contains("light") || lower.contains("बिजली") || lower.contains("लाइट") -> {
                    state.billType = BillType.ELECTRICITY
                    if (state.amount == null) state.amount = "1450"
                }
                lower.contains("gas") || lower.contains("lpg") || lower.contains("cylinder") || lower.contains("piped") || lower.contains("गैस") || lower.contains("सिलेंडर") -> {
                    state.billType = BillType.GAS
                    if (state.amount == null) state.amount = "680"
                }
                lower.contains("mobile") || lower.contains("recharge") || lower.contains("phone") || lower.contains("मोबाइल") || lower.contains("रिचार्ज") -> {
                    state.billType = BillType.MOBILE
                }
            }
        }

        // 3. Mobile Number Extraction (for mobile recharge flow)
        if (state.billType == BillType.MOBILE || lower.contains("mobile") || lower.contains("recharge")) {
            val phoneMatch = Regex("\\b[6-9]\\d{9}\\b").find(text.replace(" ", ""))
            if (phoneMatch != null) {
                state.accountId = phoneMatch.value
                if (state.billType == null) state.billType = BillType.MOBILE
            } else if (lastQuestion.contains("specify the mobile number") || lastQuestion.contains("मोबाइल नंबर और रिचार्ज राशि")) {
                val digits = text.filter { it.isDigit() }
                if (digits.length >= 10) {
                    state.accountId = digits.takeLast(10)
                }
            }
        }

        // 4. Amount Extraction
        val amountRegex = Regex("(?i)(?:₹|rs\\.?|rupees|रुपये|amount|for)\\s*(\\d+(?:\\.\\d{1,2})?)|(\\d+(?:\\.\\d{1,2})?)\\s*(?:₹|rs\\.?|rupees|रुपये)")
        val match = amountRegex.find(text)
        if (match != null) {
            state.amount = (match.groups[1] ?: match.groups[2])?.value
        } else if (lastQuestion.contains("recharge amount or plan") || lastQuestion.contains("रिचार्ज राशि या प्लान")) {
            val digits = text.filter { it.isDigit() || it == '.' }
            if (digits.isNotBlank()) {
                state.amount = digits
            }
        }

        // Default mobile recharge amount if unspecified
        if (state.billType == BillType.MOBILE && state.amount == null && state.accountId != null) {
            state.amount = "299"
        }
    }

    /**
     * Generates the next question or confirmation response in the exact flow sequence.
     */
    fun getNextStepResponse(state: PaymentFlowState, userLanguage: String = "English"): AssistantResponse {
        val isHindi = userLanguage.contains("Hindi") || userLanguage.contains("हिंदी")
        if (state.isCancelled) {
            val cancelMsg = if (isHindi)
                "ठीक है, मैंने प्रक्रिया रद्द कर दी है। यदि आपको कुछ और चाहिए तो मुझे बताएं।"
            else
                "Okay, I have cancelled the process. Let me know if you need anything else."
            return AssistantResponse(
                intent = "PAY_BILL",
                goal = "Payment cancelled",
                response = cancelMsg,
                needsClarification = false,
                clarifyingQuestion = null,
                suggestedNextStep = null
            )
        }
        return if (isHindi) getNextStepResponseHindi(state) else getNextStepResponseEnglish(state)
    }

    private fun getNextStepResponseEnglish(state: PaymentFlowState): AssistantResponse {
        val refId = state.refId ?: "REF-${(System.currentTimeMillis() % 100000).toString().padStart(5, '0')}"

        return when {
            // No bill type selected
            state.billType == null -> {
                val prompt = "Which bill would you like to pay? (Water Bill, Electricity Bill, Gas Bill, or Mobile Recharge)"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "Ask bill category",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "Say Water bill, Electricity bill, Gas bill, or Mobile recharge."
                )
            }

            // Mobile Recharge Flow: Step 1 (Ask mobile number and amount if not provided)
            state.billType == BillType.MOBILE && (state.accountId == null || state.amount == null) -> {
                val prompt = "Please specify the mobile number and the recharge amount or plan."
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "Ask mobile number and plan",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "Say your 10-digit mobile number and amount, like 9876543210 for 299 rupees."
                )
            }

            // Mobile Recharge Flow: Step 2 (Confirmation)
            state.billType == BillType.MOBILE && !state.isConfirmed -> {
                val prompt = "Recharging ${state.accountId} for ₹${state.amount}. Should I confirm the payment?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "Confirm mobile recharge",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "Say 'Yes' to confirm, or 'Cancel' to abort."
                )
            }

            // Water Bill Flow
            state.billType == BillType.WATER && !state.isConfirmed -> {
                val prompt = "Your pending Water bill amount is ₹${state.amount}. Would you like to proceed with the payment?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "Confirm water bill payment",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "Say 'Yes' to proceed, or 'Cancel' to stop."
                )
            }

            // Electricity Bill Flow
            state.billType == BillType.ELECTRICITY && !state.isConfirmed -> {
                val prompt = "Your pending Electricity bill amount is ₹${state.amount}. Would you like to proceed with the payment?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "Confirm electricity bill payment",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "Say 'Yes' to proceed, or 'Cancel' to stop."
                )
            }

            // Gas Bill Flow
            state.billType == BillType.GAS && !state.isConfirmed -> {
                val prompt = "Your Gas bill amount is ₹${state.amount}. Would you like to authorize this payment?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "Authorize gas bill payment",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "Say 'Yes' to authorize, or 'Cancel' to stop."
                )
            }

            // Completed / Confirmed
            else -> {
                val completionMsg = "Payment successful! Reference ID: $refId. Is there anything else I can help you with?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "Payment successful with continuity",
                    response = completionMsg,
                    needsClarification = false,
                    clarifyingQuestion = null,
                    suggestedNextStep = "You can book a doctor appointment or pay another bill.",
                    helpfulTip = "Receipt has been added to your payment history."
                )
            }
        }
    }

    private fun getNextStepResponseHindi(state: PaymentFlowState): AssistantResponse {
        val refId = state.refId ?: "REF-${(System.currentTimeMillis() % 100000).toString().padStart(5, '0')}"

        return when {
            // कोई बिल प्रकार चयनित नहीं
            state.billType == null -> {
                val prompt = "आप कौन सा बिल भरना चाहते हैं? (पानी का बिल, बिजली का बिल, गैस का बिल, या मोबाइल रिचार्ज)"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "बिल श्रेणी पूछें",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "पानी का बिल, बिजली का बिल, गैस का बिल, या मोबाइल रिचार्ज बोलें।"
                )
            }

            // मोबाइल रिचार्ज: नंबर और राशि पूछें
            state.billType == BillType.MOBILE && (state.accountId == null || state.amount == null) -> {
                val prompt = "कृपया मोबाइल नंबर और रिचार्ज राशि या प्लान का नाम बताएं।"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "मोबाइल नंबर और राशि पूछें",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "अपना 10 अंकों का मोबाइल नंबर और रिचार्ज राशि बोलें।"
                )
            }

            // मोबाइल रिचार्ज: पुष्टि
            state.billType == BillType.MOBILE && !state.isConfirmed -> {
                val prompt = "नंबर ${state.accountId} पर ₹${state.amount} का रिचार्ज किया जा रहा है। क्या मैं भुगतान कन्फर्म कर दूं?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "मोबाइल रिचार्ज की पुष्टि करें",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "पुष्टि के लिए 'हाँ' बोलें, या रद्द करने के लिए 'रद्द करो' बोलें।"
                )
            }

            // पानी का बिल: पुष्टि
            state.billType == BillType.WATER && !state.isConfirmed -> {
                val prompt = "आपका पानी का बिल ₹${state.amount} का बकाया है। क्या आप भुगतान आगे बढ़ाना चाहते हैं?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "पानी के बिल भुगतान की पुष्टि करें",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "भुगतान के लिए 'हाँ' बोलें, या रोकने के लिए 'रद्द करो' बोलें।"
                )
            }

            // बिजली का बिल: पुष्टि
            state.billType == BillType.ELECTRICITY && !state.isConfirmed -> {
                val prompt = "आपका बिजली का बिल ₹${state.amount} का बकाया है। क्या आप भुगतान आगे बढ़ाना चाहते हैं?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "बिजली के बिल भुगतान की पुष्टि करें",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "भुगतान के लिए 'हाँ' बोलें, या रोकने के लिए 'रद्द करो' बोलें।"
                )
            }

            // गैस का बिल: पुष्टि
            state.billType == BillType.GAS && !state.isConfirmed -> {
                val prompt = "आपका गैस का बिल ₹${state.amount} है। क्या आप इस भुगतान की पुष्टि करते हैं?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "गैस बिल भुगतान की पुष्टि करें",
                    response = prompt,
                    needsClarification = true,
                    clarifyingQuestion = prompt,
                    suggestedNextStep = "पुष्टि के लिए 'हाँ' बोलें, या रोकने के लिए 'रद्द करो' बोलें।"
                )
            }

            // भुगतान पूर्ण
            else -> {
                val completionMsg = "भुगतान सफल रहा! रेफरेंस आईडी है $refId। क्या मैं आपकी कोई और सहायता कर सकता हूं?"
                AssistantResponse(
                    intent = "PAY_BILL",
                    goal = "भुगतान सफल व निरंतरता",
                    response = completionMsg,
                    needsClarification = false,
                    clarifyingQuestion = null,
                    suggestedNextStep = "आप डॉक्टर का अपॉइंटमेंट बुक कर सकते हैं या कोई अन्य बिल भर सकते हैं।",
                    helpfulTip = "रसीद आपके भुगतान इतिहास में जोड़ दी गई है।"
                )
            }
        }
    }
}
