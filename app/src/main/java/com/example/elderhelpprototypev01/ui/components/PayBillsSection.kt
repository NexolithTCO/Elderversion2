package com.example.elderhelpprototypev01.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.elderhelpprototypev01.model.BillPayment
import com.example.elderhelpprototypev01.model.BillType
import com.example.elderhelpprototypev01.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * PayBillsSection
 *
 * Expanded Pay Bills dashboard section displaying clear, clickable category cards:
 * 1. ⚡ Electricity Bill
 * 2. 💧 Water Bill
 * 3. 📱 Mobile Recharge
 */
@Composable
fun PayBillsSection(
    onCategoryClick: (BillType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pay Bills & Utilities",
                style = Typography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleTextPrimary
                )
            )
            Text(
                text = "Instant & Safe",
                style = Typography.bodySmall.copy(
                    color = Color(0xFF34C759),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3 Distinct Category Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BillCategoryCard(
                type = BillType.ELECTRICITY,
                color = Color(0xFFFFF8E1),
                borderColor = Color(0xFFFFB300),
                onClick = { onCategoryClick(BillType.ELECTRICITY) },
                modifier = Modifier.weight(1f)
            )
            BillCategoryCard(
                type = BillType.WATER,
                color = Color(0xFFE1F5FE),
                borderColor = Color(0xFF0288D1),
                onClick = { onCategoryClick(BillType.WATER) },
                modifier = Modifier.weight(1f)
            )
            BillCategoryCard(
                type = BillType.MOBILE,
                color = Color(0xFFF3E5F5),
                borderColor = Color(0xFF8E24AA),
                onClick = { onCategoryClick(BillType.MOBILE) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BillCategoryCard(
    type: BillType,
    color: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(vertical = 16.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(text = type.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = type.title.replace(" Bill", ""),
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppleTextPrimary,
                    fontSize = 13.sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Pay Now",
                style = Typography.labelSmall.copy(
                    color = AppleBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
        }
    }
}


/**
 * BillPaymentModal
 *
 * Mock payment/input form modal for the selected category.
 */
@Composable
fun BillPaymentModal(
    billType: BillType,
    initialIdentifier: String? = null,
    initialProvider: String? = null,
    initialAmount: String? = null,
    onDismiss: () -> Unit,
    onPaymentSuccess: (BillPayment) -> Unit
) {
    var identifier by remember { mutableStateOf(initialIdentifier ?: billType.sampleIdentifier) }
    var provider by remember { mutableStateOf(initialProvider ?: billType.defaultProvider) }
    var amount by remember { mutableStateOf(initialAmount ?: billType.defaultAmount) }
    var isProcessing by remember { mutableStateOf(false) }
    var isPaid by remember { mutableStateOf(false) }
    var receiptPayment by remember { mutableStateOf<BillPayment?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val quickAmounts = listOf("299", "499", "1000", "1500", "2000")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = AppleCanvasBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(billType.iconEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = billType.title,
                                style = Typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppleTextPrimary,
                                    fontSize = 20.sp
                                )
                            )
                            Text(
                                text = "Secure Bill Settlement",
                                style = Typography.bodySmall.copy(color = AppleTextMuted)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AppleBorderSubtle.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppleTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    if (isPaid && receiptPayment != null) {
                        // Success Receipt View
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, Color(0xFF34C759).copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8F5E9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Success",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Payment Successful!",
                                    style = Typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32),
                                        fontSize = 20.sp
                                    )
                                )

                                Text(
                                    text = "₹${receiptPayment!!.amount} paid towards ${receiptPayment!!.type.title}",
                                    style = Typography.bodyMedium.copy(color = AppleTextSecondary)
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = AppleBorderSubtle, thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(16.dp))

                                ReceiptRow(label = "Transaction ID", value = receiptPayment!!.id)
                                ReceiptRow(label = "Billed Account", value = receiptPayment!!.identifier)
                                ReceiptRow(label = "Provider", value = receiptPayment!!.provider)
                                ReceiptRow(label = "Date & Time", value = receiptPayment!!.timestamp)
                                ReceiptRow(label = "Status", value = receiptPayment!!.status, isHighlight = true)

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = onDismiss,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Done", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // Payment Input Form
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, AppleBorderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Bill Details",
                                    style = Typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AppleTextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                // Identifier (Account ID / Consumer No / Phone)
                                Text(
                                    text = billType.fieldLabel,
                                    style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = identifier,
                                    onValueChange = { identifier = it },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Provider
                                Text(
                                    text = "Operator / Provider",
                                    style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = provider,
                                    onValueChange = { provider = it },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Amount
                                Text(
                                    text = "Bill Amount (₹)",
                                    style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(quickAmounts) { a ->
                                        val isSelected = amount == a
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { amount = a },
                                            label = { Text("₹$a", fontSize = 13.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = AppleBlue,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = amount,
                                    onValueChange = { amount = it },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = {
                                        isProcessing = true
                                        coroutineScope.launch {
                                            delay(1000L) // Mock processing
                                            val df = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                                            val newPayment = BillPayment(
                                                id = "PAY-${System.currentTimeMillis() % 100000}",
                                                type = billType,
                                                identifier = identifier,
                                                provider = provider,
                                                amount = amount,
                                                timestamp = df.format(Date())
                                            )
                                            receiptPayment = newPayment
                                            isPaid = true
                                            isProcessing = false
                                            onPaymentSuccess(newPayment)
                                        }
                                    },
                                    enabled = !isProcessing && identifier.isNotBlank() && amount.isNotBlank(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    if (isProcessing) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Processing Payment...", fontWeight = FontWeight.Bold)
                                    } else {
                                        Text("Proceed to Pay ₹$amount", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = Typography.bodySmall.copy(color = AppleTextMuted, fontSize = 13.sp))
        Text(
            text = value,
            style = Typography.bodyMedium.copy(
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
                color = if (isHighlight) Color(0xFF2E7D32) else AppleTextPrimary,
                fontSize = 14.sp
            )
        )
    }
}
