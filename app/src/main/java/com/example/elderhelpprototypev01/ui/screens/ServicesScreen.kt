package com.example.elderhelpprototypev01.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.elderhelpprototypev01.SahaayViewModel
import com.example.elderhelpprototypev01.model.BillPayment
import com.example.elderhelpprototypev01.model.BillType
import com.example.elderhelpprototypev01.ui.components.*
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * ServicesScreen
 *
 * Dedicated tab for quick access to all essential eldercare services:
 * - Doctor Booking widget & manager
 * - Pay Bills (Electricity, Water, Mobile Recharge)
 * - Government Forms & Help
 */
@Composable
fun ServicesScreen(
    viewModel: SahaayViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val bookedAppointment by viewModel.bookedAppointment.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showDoctorModal by remember { mutableStateOf(false) }
    var selectedBillType by remember { mutableStateOf<BillType?>(null) }
    var isSosModalOpen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppleCanvasBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp)
                .padding(top = 20.dp, bottom = 32.dp)
        ) {
            // Header
            Text(
                text = "Services & Tasks",
                style = Typography.headlineLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleTextPrimary
                )
            )
            Text(
                text = "Fast, one-tap access to your daily essentials",
                style = Typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = AppleTextMuted
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Doctor Booking Quick Widget
            DoctorBookingWidget(
                appointment = bookedAppointment,
                onClick = { showDoctorModal = true },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(22.dp))

            // 2. Pay Bills Section
            PayBillsSection(
                onCategoryClick = { type ->
                    selectedBillType = type
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(22.dp))

            // 3. Get Help & Emergency
            GetHelpCard(
                onEmergencyClick = { isSosModalOpen = true },
                emergencyContactName = userProfile.emergencyContactName,
                emergencyContactPhone = userProfile.emergencyContactPhone,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Modals
        if (showDoctorModal) {
            DoctorAppointmentModal(
                appointment = bookedAppointment,
                onDismiss = { showDoctorModal = false },
                onBookManual = { newBooking ->
                    viewModel.updateAppointment(newBooking)
                    Toast.makeText(context, "Appointment booked successfully!", Toast.LENGTH_SHORT).show()
                },
                onCancelAppointment = {
                    viewModel.cancelAppointment()
                    Toast.makeText(context, "Appointment cancelled.", Toast.LENGTH_SHORT).show()
                }
            )
        }

        selectedBillType?.let { type ->
            BillPaymentModal(
                billType = type,
                onDismiss = { selectedBillType = null },
                onPaymentSuccess = { payment ->
                    viewModel.recordBillPayment(payment)
                    Toast.makeText(context, "₹${payment.amount} paid successfully!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        if (isSosModalOpen) {
            EmergencySosModal(
                onDismiss = { isSosModalOpen = false },
                onEmergencyTriggered = {
                    Toast.makeText(context, "Emergency call initiated to ${userProfile.emergencyContactPhone}", Toast.LENGTH_LONG).show()
                },
                contactName = userProfile.emergencyContactName,
                contactNumber = userProfile.emergencyContactPhone
            )
        }
    }
}
