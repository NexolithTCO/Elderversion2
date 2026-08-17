package com.example.elderhelpprototypev01.ai

import com.example.elderhelpprototypev01.model.ConversationMessage
import com.example.elderhelpprototypev01.model.MessageRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [DoctorBookingManager].
 *
 * Verifies:
 * - 5-step sequential dialogue flow:
 *   1. Specialty / Doctor Type
 *   2. Location / Place
 *   3. Preferred Date and Time
 *   4. Consultation Mode
 *   5. Confirmation
 * - Skipping questions when multiple details are provided in a single message
 * - Confirmed appointment completion
 */
class DoctorBookingTest {

    @Test
    fun `step 1 - ask specialty on initial intent`() {
        val transcript = "book a doctor"
        val state = DoctorBookingManager.extractState(emptyList(), transcript)
        val response = DoctorBookingManager.getNextStepResponse(state)

        assertEquals("Which type of doctor would you like to book? (e.g., General Physician, Dermatologist, Cardiologist)", response.response)
        assertTrue(response.needsClarification)
    }

    @Test
    fun `step 2 - ask location after specialty provided`() {
        val conversation = listOf(
            ConversationMessage(MessageRole.USER, "book a doctor"),
            ConversationMessage(MessageRole.ASSISTANT, "Which type of doctor would you like to book? (e.g., General Physician, Dermatologist, Cardiologist)")
        )
        val transcript = "Dermatologist"
        val state = DoctorBookingManager.extractState(conversation, transcript)
        val response = DoctorBookingManager.getNextStepResponse(state)

        assertEquals("Dermatologist", state.specialty)
        assertEquals("Which area, city, or clinic location do you prefer?", response.response)
        assertTrue(response.needsClarification)
    }

    @Test
    fun `step 3 - ask date and time after location provided`() {
        val conversation = listOf(
            ConversationMessage(MessageRole.USER, "book a doctor"),
            ConversationMessage(MessageRole.ASSISTANT, "Which type of doctor would you like to book? (e.g., General Physician, Dermatologist, Cardiologist)"),
            ConversationMessage(MessageRole.USER, "Dermatologist"),
            ConversationMessage(MessageRole.ASSISTANT, "Which area, city, or clinic location do you prefer?")
        )
        val transcript = "Bandra"
        val state = DoctorBookingManager.extractState(conversation, transcript)
        val response = DoctorBookingManager.getNextStepResponse(state)

        assertEquals("Bandra", state.location)
        assertEquals("What date and time work best for you?", response.response)
        assertTrue(response.needsClarification)
    }

    @Test
    fun `step 4 - ask consultation mode after date and time provided`() {
        val conversation = listOf(
            ConversationMessage(MessageRole.USER, "book a doctor"),
            ConversationMessage(MessageRole.ASSISTANT, "Which type of doctor would you like to book? (e.g., General Physician, Dermatologist, Cardiologist)"),
            ConversationMessage(MessageRole.USER, "Dermatologist"),
            ConversationMessage(MessageRole.ASSISTANT, "Which area, city, or clinic location do you prefer?"),
            ConversationMessage(MessageRole.USER, "Bandra"),
            ConversationMessage(MessageRole.ASSISTANT, "What date and time work best for you?")
        )
        val transcript = "Tomorrow at 5 PM"
        val state = DoctorBookingManager.extractState(conversation, transcript)
        val response = DoctorBookingManager.getNextStepResponse(state)

        assertEquals("Tomorrow at 5 PM", state.dateTime)
        assertEquals("Would you prefer an in-person clinic visit or an online consultation?", response.response)
        assertTrue(response.needsClarification)
    }

    @Test
    fun `step 5 - summarize and ask confirmation`() {
        val conversation = listOf(
            ConversationMessage(MessageRole.USER, "book a doctor"),
            ConversationMessage(MessageRole.ASSISTANT, "Which type of doctor would you like to book? (e.g., General Physician, Dermatologist, Cardiologist)"),
            ConversationMessage(MessageRole.USER, "Dermatologist"),
            ConversationMessage(MessageRole.ASSISTANT, "Which area, city, or clinic location do you prefer?"),
            ConversationMessage(MessageRole.USER, "Bandra"),
            ConversationMessage(MessageRole.ASSISTANT, "What date and time work best for you?"),
            ConversationMessage(MessageRole.USER, "Tomorrow at 5 PM"),
            ConversationMessage(MessageRole.ASSISTANT, "Would you prefer an in-person clinic visit or an online consultation?")
        )
        val transcript = "In-person clinic visit"
        val state = DoctorBookingManager.extractState(conversation, transcript)
        val response = DoctorBookingManager.getNextStepResponse(state)

        assertEquals("in-person clinic visit", state.mode)
        assertEquals(
            "I have noted your appointment details: Dermatologist in Bandra on Tomorrow at 5 PM for an in-person clinic visit. Would you like me to confirm this booking?",
            response.response
        )
        assertTrue(response.needsClarification)
    }

    @Test
    fun `step 6 - confirmed appointment completion`() {
        val conversation = listOf(
            ConversationMessage(MessageRole.USER, "book a dermatologist in Bandra for tomorrow at 5 PM in-person"),
            ConversationMessage(
                MessageRole.ASSISTANT,
                "I have noted your appointment details: Dermatologist in Bandra on tomorrow at 5 PM for an in-person clinic visit. Would you like me to confirm this booking?"
            )
        )
        val transcript = "Yes, please confirm"
        val state = DoctorBookingManager.extractState(conversation, transcript)
        val response = DoctorBookingManager.getNextStepResponse(state)

        assertTrue(state.isConfirmed)
        assertEquals(
            "Your appointment for Dermatologist in Bandra on tomorrow at 5 PM (in-person clinic visit) has been confirmed! Is there anything else I can help you with?",
            response.response
        )
        assertFalse(response.needsClarification)
    }

    @Test
    fun `multi slot extraction skips provided details`() {
        // User provides specialty, location, and date/time in first utterance
        val transcript = "Book a dermatologist in Bandra for tomorrow at 5 PM"
        val state = DoctorBookingManager.extractState(emptyList(), transcript)
        val response = DoctorBookingManager.getNextStepResponse(state)

        assertEquals("Dermatologist", state.specialty)
        assertEquals("Bandra", state.location)
        assertEquals("tomorrow at 5 PM", state.dateTime)
        // Should ask only for the missing slot (Consultation Mode)
        assertEquals("Would you prefer an in-person clinic visit or an online consultation?", response.response)
    }
}
