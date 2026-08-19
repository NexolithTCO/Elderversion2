package com.example.elderhelpprototypev01.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
 * 10. TRANSACTIONS (Page 8, 9)
 *
 * Information hierarchy:
 * - Top: Title "Transactions / Payment history & recent activity"
 * - Summary: Single clean financial summary surface with large ₹0 typography & ✓ completed status
 * - Filters: Clean segmented control (active: solid blue + white text, inactive: transparent + muted)
 * - Activity empty state: Vertically centered with refined icon, strong title, short explanation
 * - Activity records: Clean 68-76px rows with subtle separators
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
    val isHindi = currentLanguage.contains("Hindi")

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

    // Calculate total spend
    val totalAmount = remember(transactions) {
        transactions
            .filter { !it.isAppointment && it.category != TransactionCategory.DOCTOR }
            .mapNotNull { it.amount.replace(",", "").toIntOrNull() }
            .sum()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Header
            item {
                Column {
                    Text(
                        text = strings.navTransactions,
                        style = Typography.headlineLarge.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isHindi) "भुगतान इतिहास और हाल ही की गतिविधियाँ" else "Payment history & recent activity",
                        style = Typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            color = AppTextSecondary
                        )
                    )
                }
            }

            // 2. Financial Summary Surface (16px radius, subtle border)
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AppSurface,
                    border = BorderStroke(1.dp, AppBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = if (isHindi) "इस महीने कुल भुगतान" else "TOTAL PAID THIS MONTH",
                            style = Typography.bodySmall.copy(
                                color = AppTextMuted,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "₹${"%,d".format(totalAmount)}",
                                style = Typography.headlineLarge.copy(
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTextPrimary
                                )
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFDCFCE7))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "✓ ${transactions.size} पूर्ण" else "✓ ${transactions.size} completed",
                                    style = Typography.bodySmall.copy(
                                        color = AppSuccess,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 3. Segmented Filter Control (Avoid giant pills)
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppSurface,
                    border = BorderStroke(1.dp, AppBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item {
                            SegmentedFilterTab(
                                label = if (isHindi) "सभी (${transactions.size})" else "All (${transactions.size})",
                                isSelected = selectedCategoryFilter == null,
                                onClick = { selectedCategoryFilter = null }
                            )
                        }
                        items(TransactionCategory.values()) { category ->
                            val count = transactions.count { it.category == category }
                            val categoryTitle = if (isHindi) {
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
                            SegmentedFilterTab(
                                label = "$categoryTitle ($count)",
                                isSelected = selectedCategoryFilter == category,
                                onClick = {
                                    selectedCategoryFilter = if (selectedCategoryFilter == category) null else category
                                }
                            )
                        }
                    }
                }
            }

            // 4. Activity Section Title
            item {
                Text(
                    text = if (isHindi) "गतिविधि विवरण" else "Activity Records",
                    style = Typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTextPrimary
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 5. Activity Records List or Centered Empty State
            if (filteredTransactions.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = AppSurface,
                        border = BorderStroke(1.dp, AppBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp, horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(AppleBlueLight)
                            ) {
                                Text("🧾", fontSize = 28.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = strings.noTransactionsTitle,
                                style = Typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppTextPrimary,
                                    fontSize = 17.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = strings.noTransactionsSubtitle,
                                style = Typography.bodySmall.copy(
                                    color = AppTextSecondary,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            } else {
                items(filteredTransactions, key = { it.id }) { txn ->
                    TransactionHistoryRow(
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
 * Segmented Filter Tab (Page 9: Active = solid blue + white text; Inactive = transparent + muted)
 */
@Composable
private fun SegmentedFilterTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) AppPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = Typography.bodySmall.copy(
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) Color.White else AppTextSecondary
            )
        )
    }
}

/**
 * Clean 68-76px Transaction History Record Row
 */
@Composable
fun TransactionHistoryRow(
    transaction: TransactionRecord,
    currentLanguage: String = "English (India)",
    onPayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDoctorAppointment = transaction.isAppointment || transaction.category == TransactionCategory.DOCTOR

    val categoryBg = when (transaction.category) {
        TransactionCategory.ELECTRICITY -> TintElectricityBg
        TransactionCategory.WATER -> TintWaterBg
        TransactionCategory.MOBILE -> TintMobileBg
        TransactionCategory.GAS -> TintGasBg
        TransactionCategory.DOCTOR -> TintWaterBg
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = AppSurface,
        border = BorderStroke(1.dp, AppBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(categoryBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = transaction.category.iconEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                val displayTitle = if (isDoctorAppointment && !transaction.doctorName.isNullOrBlank()) {
                    "${transaction.doctorName} (${transaction.provider})"
                } else {
                    transaction.provider
                }

                Text(
                    text = displayTitle,
                    style = Typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = AppTextPrimary,
                        fontSize = 15.sp
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = "${transaction.timestamp} • Ref: ${transaction.identifier.take(12)}",
                    style = Typography.bodySmall.copy(
                        color = AppTextSecondary,
                        fontSize = 12.sp
                    ),
                    maxLines = 1
                )
            }

            if (!isDoctorAppointment && transaction.amount.isNotBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "₹${transaction.amount}",
                    style = Typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppTextPrimary,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}
