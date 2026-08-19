package com.example.elderhelpprototypev01.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.elderhelpprototypev01.SahaayViewModel
import com.example.elderhelpprototypev01.ai.DoctorBookingManager
import com.example.elderhelpprototypev01.model.BillPayment
import com.example.elderhelpprototypev01.model.BillType
import com.example.elderhelpprototypev01.model.TransactionCategory
import com.example.elderhelpprototypev01.model.TransactionRecord
import com.example.elderhelpprototypev01.ui.components.BillPaymentModal
import com.example.elderhelpprototypev01.ui.components.DoctorAppointmentModal
import com.example.elderhelpprototypev01.ui.localization.Localization
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * TransactionsScreen
 *
 * Dedicated tab for viewing recent completed transactions and payment history.
 * Provides instant "Pay Again" / "Repeat" functionality with pre-filled details.
 */
@Composable
fun TransactionsScreen(
    viewModel: SahaayViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val bookedAppointment by viewModel.bookedAppointment.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val strings = Localization.getStrings(currentLanguage)

    var selectedCategoryFilter by remember { mutableStateOf<TransactionCategory?>(null) }

    // Repeat / Pay Again Modal States
    var repeatingBillTransaction by remember { mutableStateOf<TransactionRecord?>(null) }
    var repeatingDoctorTransaction by remember { mutableStateOf<TransactionRecord?>(null) }

    val filteredTransactions = remember(transactions, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) {
            transactions
        } else {
            transactions.filter { it.category == selectedCategoryFilter }
        }
    }

    // Calculate total spend (excluding doctor appointments which have no bill amount)
    val totalAmount = remember(transactions) {
        transactions
            .filter { !it.isAppointment && it.category != TransactionCategory.DOCTOR }
            .mapNotNull { it.amount.replace(",", "").toIntOrNull() }
            .sum()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppleCanvasBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header & Subtitle
            item {
                Column {
                    Text(
                        text = strings.navTransactions,
                        style = Typography.headlineLarge.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppleTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (currentLanguage.contains("Hindi")) "भुगतान इतिहास और हाल ही की गतिविधियाँ" else "Payment history and recent activity",
                        style = Typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            color = AppleTextMuted
                        )
                    )
                }
            }

            // 2. Summary Overview Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, AppleBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (currentLanguage.contains("Hindi")) "इस महीने कुल भुगतान" else "Total Paid This Month",
                                style = Typography.bodySmall.copy(
                                    color = AppleTextMuted,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${"%,d".format(totalAmount)}",
                                style = Typography.headlineMedium.copy(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleTextPrimary
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentLanguage.contains("Hindi")) "${transactions.size} पूर्ण" else "${transactions.size} Completed",
                                    style = Typography.labelMedium.copy(
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 3. Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text(if (currentLanguage.contains("Hindi")) "सभी (${transactions.size})" else "All (${transactions.size})", fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppleBlue,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                    items(TransactionCategory.values()) { category ->
                        val count = transactions.count { it.category == category }
                        val categoryTitle = if (currentLanguage.contains("Hindi")) {
                            when (category) {
                                TransactionCategory.ELECTRICITY -> "बिजली"
                                TransactionCategory.WATER -> "पानी"
                                TransactionCategory.MOBILE -> "मोबाइल"
                                TransactionCategory.GAS -> "गैस"
                                TransactionCategory.DOCTOR -> "डॉक्टर"
                            }
                        } else {
                            category.title
                        }
                        FilterChip(
                            selected = selectedCategoryFilter == category,
                            onClick = {
                                selectedCategoryFilter = if (selectedCategoryFilter == category) null else category
                            },
                            label = {
                                Text("${category.iconEmoji} $categoryTitle ($count)", fontWeight = FontWeight.SemiBold)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppleBlue,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // 4. Section Title
            item {
                Text(
                    text = if (currentLanguage.contains("Hindi")) "गतिविधि विवरण" else "Activity Records",
                    style = Typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppleTextPrimary
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 5. List of Transaction Cards or Empty State
            if (filteredTransactions.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, AppleBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🧾", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = strings.noTransactionsTitle,
                                style = Typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppleTextPrimary,
                                    fontSize = 17.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = strings.noTransactionsSubtitle,
                                style = Typography.bodySmall.copy(
                                    color = AppleTextMuted,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            } else {
                items(filteredTransactions, key = { it.id }) { txn ->
                    TransactionHistoryCard(
                        transaction = txn,
                        currentLanguage = currentLanguage,
                        onPayAgain = {
                            if (txn.category == TransactionCategory.DOCTOR || txn.isAppointment) {
                                repeatingDoctorTransaction = txn
                            } else {
                                repeatingBillTransaction = txn
                            }
                        }
                    )
                }
            }
        }

        // Bill Re-Payment Modal (Prefilled)
        repeatingBillTransaction?.let { txn ->
            val billType = txn.billType ?: when (txn.category) {
                TransactionCategory.ELECTRICITY -> BillType.ELECTRICITY
                TransactionCategory.WATER -> BillType.WATER
                TransactionCategory.MOBILE -> BillType.MOBILE
                TransactionCategory.GAS -> BillType.GAS
                TransactionCategory.DOCTOR -> BillType.ELECTRICITY
            }

            BillPaymentModal(
                billType = billType,
                initialIdentifier = txn.identifier,
                initialProvider = txn.provider,
                initialAmount = txn.amount,
                onDismiss = { repeatingBillTransaction = null },
                onPaymentSuccess = { payment ->
                    viewModel.recordBillPayment(payment)
                    repeatingBillTransaction = null
                    Toast.makeText(context, "Payment of ₹${payment.amount} completed!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Doctor Re-booking Modal (Prefilled)
        repeatingDoctorTransaction?.let { txn ->
            val initialBookingState = DoctorBookingManager.BookingState(
                specialty = txn.doctorSpecialty ?: "Cardiologist",
                location = txn.provider.substringBefore("(").trim().ifBlank { "Bandra Medical Clinic" },
                dateTime = txn.appointmentDateTime ?: "Tomorrow at 5:00 PM",
                mode = "in-person clinic visit",
                isConfirmed = false
            )

            DoctorAppointmentModal(
                appointment = initialBookingState,
                initialTab = 1,
                currentLanguage = currentLanguage,
                onDismiss = { repeatingDoctorTransaction = null },
                onBookManual = { newBooking ->
                    viewModel.updateAppointment(newBooking)
                    repeatingDoctorTransaction = null
                    Toast.makeText(context, "Appointment re-booked successfully!", Toast.LENGTH_SHORT).show()
                },
                onCancelAppointment = {
                    repeatingDoctorTransaction = null
                }
            )
        }
    }
}

/**
 * Individual Accessible Transaction / Booking History Card
 */
@Composable
fun TransactionHistoryCard(
    transaction: TransactionRecord,
    currentLanguage: String = "English (India)",
    onPayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDoctorAppointment = transaction.isAppointment || transaction.category == TransactionCategory.DOCTOR
    val isHindi = currentLanguage.contains("Hindi") || currentLanguage.contains("हिंदी")

    val categoryBgColor = when (transaction.category) {
        TransactionCategory.ELECTRICITY -> Color(0xFFFFF8E1)
        TransactionCategory.WATER -> Color(0xFFE1F5FE)
        TransactionCategory.MOBILE -> Color(0xFFF3E5F5)
        TransactionCategory.GAS -> Color(0xFFFFF3E0)
        TransactionCategory.DOCTOR -> Color(0xFFEDE7F6)
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, AppleBorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Category Icon + Provider Name + Amount Paid (if not doctor booking)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(categoryBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = transaction.category.iconEmoji, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        // Title: Doctor Name or Provider
                        val displayTitle = if (isDoctorAppointment && !transaction.doctorName.isNullOrBlank()) {
                            "${transaction.doctorName} (${transaction.provider})"
                        } else {
                            transaction.provider
                        }

                        Text(
                            text = displayTitle,
                            style = Typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleTextPrimary,
                                fontSize = 16.sp
                            ),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))

                        // Subtitle: Specialty / Bill Type & Ref
                        val subtitleText = if (isDoctorAppointment) {
                            val spec = transaction.doctorSpecialty ?: "Doctor Booking"
                            val time = transaction.appointmentDateTime ?: ""
                            if (time.isNotBlank()) "$spec • $time" else "$spec • Ref: ${transaction.identifier}"
                        } else {
                            "${transaction.category.title} • Ref: ${transaction.identifier}"
                        }

                        Text(
                            text = subtitleText,
                            style = Typography.bodySmall.copy(
                                color = AppleTextMuted,
                                fontSize = 12.sp
                            ),
                            maxLines = 1
                        )
                    }
                }

                // Amount (HIDDEN for Doctor Bookings)
                if (!isDoctorAppointment && transaction.amount.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "₹${transaction.amount}",
                        style = Typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppleTextPrimary,
                            fontSize = 18.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = AppleBorderSubtle.copy(alpha = 0.6f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Row: Date/Time + Status Badge + Voice Badge + Re-Book / Pay Again Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Status Badge
                        val badgeBg = if (isDoctorAppointment) Color(0xFFEDE7F6) else Color(0xFFE8F5E9)
                        val badgeText = if (isDoctorAppointment) Color(0xFF6A1B9A) else Color(0xFF2E7D32)
                        val statusLabel = if (isDoctorAppointment) {
                            if (isHindi) "अपॉइंटमेंट बुक हुआ" else "Appointment Booked"
                        } else {
                            if (isHindi) "भुगतान सफल" else transaction.status
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = badgeBg
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isDoctorAppointment) Icons.Default.EventAvailable else Icons.Default.CheckCircle,
                                    contentDescription = "Status",
                                    tint = badgeText,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = statusLabel,
                                    style = Typography.labelSmall.copy(
                                        color = badgeText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Voice Badge
                        if (transaction.isVoicePayment) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFE3F2FD)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isHindi) "🎙️ सहाय वॉयस" else "🎙️ Sahaay Voice",
                                        style = Typography.labelSmall.copy(
                                            color = AppleBlue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Booking / Transaction Timestamp
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Date",
                            tint = AppleTextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = transaction.timestamp,
                            style = Typography.bodySmall.copy(
                                color = AppleTextMuted,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // "Pay Again" / "Re-Book" Button
                Button(
                    onClick = onPayAgain,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppleBlueLight,
                        contentColor = AppleBlue
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = null,
                        tint = AppleBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isDoctorAppointment) {
                            if (isHindi) "फिर बुक करें" else "Re-Book"
                        } else {
                            if (isHindi) "पुनः भुगतान" else "Pay Again"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

