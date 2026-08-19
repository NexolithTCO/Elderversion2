package com.example.elderhelpprototypev01.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.animation.core.tween
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
 * 4. 🔥 Gas Bill (Piped Gas / LPG Cylinder)
 *
 * Includes a 4-Way Senior-Friendly Payment System (UPI, Cards, Net Banking, Wallets).
 */
/**
 * 8. BILLS & UTILITIES (Page 6, 7)
 *
 * Clean 2×2 service grid:
 * [ Electricity ]  [ Water ]
 * [ Mobile ]       [ Gas ]
 *
 * Dimensions: height 104px, gap 12px, 16px radius, uniform #E4E7EC border.
 * Icon containers: 42px softly tinted.
 */
@Composable
fun PayBillsSection(
    currentLanguage: String = "English (India)",
    onCategoryClick: (BillType) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage.contains("Hindi") || currentLanguage.contains("हिंदी")

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Text(
            text = if (isHindi) "उपयोगिता बिल भुगतान" else "Pay Bills & Utilities",
            style = Typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppTextPrimary
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = if (isHindi) "सुरक्षित 4-तरीकों से तुरंत भुगतान" else "Quick 4-way secure bill payments",
            style = Typography.bodySmall.copy(
                color = AppTextMuted,
                fontSize = 13.sp
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2x2 Service Grid with 12px gap
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ServiceGridItem(
                    type = BillType.ELECTRICITY,
                    title = if (isHindi) "बिजली" else "Electricity",
                    subtitle = if (isHindi) "पावर व ग्रिड" else "Power & Grid",
                    iconBg = TintElectricityBg,
                    onClick = { onCategoryClick(BillType.ELECTRICITY) },
                    modifier = Modifier.weight(1f)
                )
                ServiceGridItem(
                    type = BillType.WATER,
                    title = if (isHindi) "पानी" else "Water",
                    subtitle = if (isHindi) "जल निगम व आपूर्ति" else "Municipal & Jal",
                    iconBg = TintWaterBg,
                    onClick = { onCategoryClick(BillType.WATER) },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ServiceGridItem(
                    type = BillType.MOBILE,
                    title = if (isHindi) "मोबाइल" else "Mobile",
                    subtitle = if (isHindi) "रिचार्ज और प्लान" else "Recharge & Plan",
                    iconBg = TintMobileBg,
                    onClick = { onCategoryClick(BillType.MOBILE) },
                    modifier = Modifier.weight(1f)
                )
                ServiceGridItem(
                    type = BillType.GAS,
                    title = if (isHindi) "गैस / एलपीजी" else "Gas / LPG",
                    subtitle = if (isHindi) "सिलेंडर व पाइप" else "Piped & Cylinder",
                    iconBg = TintGasBg,
                    onClick = { onCategoryClick(BillType.GAS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ServiceGridItem(
    type: BillType,
    title: String,
    subtitle: String,
    iconBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = tween(120),
        label = "serviceScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        color = AppSurface,
        border = BorderStroke(1.dp, AppBorder),
        modifier = modifier
            .scale(scale)
            .height(108.dp)
            .semantics {
                contentDescription = "$title bill payment, $subtitle. Tap to pay."
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = type.iconEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = Typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = AppTextPrimary,
                    fontSize = 15.sp
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                style = Typography.bodySmall.copy(
                    color = AppTextSecondary,
                    fontSize = 12.sp
                ),
                maxLines = 1
            )
        }
    }
}


/**
 * Payment Method item data class
 */
private data class PaymentMethodItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector,
    val accentColor: Color
)

/**
 * BillPaymentModal
 *
 * Full-featured billing & payment modal supporting a **4-Way Payment System**
 * (UPI, Cards, Net Banking, Wallets) tailored for elderly accessibility.
 */
@Composable
fun BillPaymentModal(
    billType: BillType,
    initialIdentifier: String? = null,
    initialProvider: String? = null,
    initialAmount: String? = null,
    userProfile: com.example.elderhelpprototypev01.model.UserProfile? = null,
    onDismiss: () -> Unit,
    onPaymentSuccess: (BillPayment) -> Unit
) {
    val defaultId: String = initialIdentifier ?: when (billType) {
        BillType.MOBILE -> {
            val digits = userProfile?.contactNumber?.filter { it.isDigit() }?.takeLast(10)
            if (!digits.isNullOrBlank()) digits else billType.sampleIdentifier
        }
        else -> billType.sampleIdentifier
    }

    var identifier by remember(billType, initialIdentifier) { mutableStateOf(defaultId) }
    var provider by remember(billType, initialProvider) { mutableStateOf(initialProvider ?: billType.defaultProvider) }
    var amount by remember(billType, initialAmount) { mutableStateOf(initialAmount ?: billType.defaultAmount) }

    // 4-Way Payment System State
    var selectedPaymentMode by remember { mutableStateOf("upi") }
    var selectedUpiApp by remember { mutableStateOf("Google Pay") }
    var upiId by remember { mutableStateOf("9876512345@paytm") }
    var cardNumber by remember { mutableStateOf("4532 8892 1092 4819") }
    var cardExpiry by remember { mutableStateOf("08/28") }
    var cardCvv by remember { mutableStateOf("782") }
    var selectedBank by remember { mutableStateOf("State Bank of India (SBI)") }
    var selectedWallet by remember { mutableStateOf("Paytm Wallet") }

    var isProcessing by remember { mutableStateOf(false) }
    var isPaid by remember { mutableStateOf(false) }
    var receiptPayment by remember { mutableStateOf<BillPayment?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val quickAmounts = when (billType) {
        BillType.GAS -> listOf("480", "680", "850", "1050", "1250")
        BillType.MOBILE -> listOf("199", "299", "499", "699", "999")
        BillType.ELECTRICITY -> listOf("750", "1200", "1450", "2000", "3500")
        BillType.WATER -> listOf("250", "480", "720", "950", "1500")
    }

    val quickProviders = when (billType) {
        BillType.GAS -> listOf("Mahanagar Gas", "IGL", "HP Gas", "Bharat Gas", "Indane")
        BillType.ELECTRICITY -> listOf("Adani Electricity", "Tata Power", "MSEDCL", "BESCOM")
        BillType.WATER -> listOf("Municipal Corporation", "Delhi Jal Board", "BMC Water")
        BillType.MOBILE -> listOf("Jio Prepaid", "Airtel", "Vi Prepaid", "BSNL")
    }

    // 4-Way Payment System Modes Definition
    val paymentMethods = listOf(
        PaymentMethodItem(
            id = "upi",
            title = "UPI Payment",
            subtitle = "GPay, PhonePe, Paytm, BHIM",
            badge = "Instant ⚡",
            icon = Icons.Default.QrCodeScanner,
            accentColor = Color(0xFF0066CC)
        ),
        PaymentMethodItem(
            id = "card",
            title = "Debit & Credit Card",
            subtitle = "Visa, MasterCard, RuPay",
            badge = "All Cards",
            icon = Icons.Default.CreditCard,
            accentColor = Color(0xFF34C759)
        ),
        PaymentMethodItem(
            id = "netbanking",
            title = "Net Banking",
            subtitle = "SBI, HDFC, ICICI, Axis Bank",
            badge = "All Banks",
            icon = Icons.Default.AccountBalance,
            accentColor = Color(0xFFFF9500)
        ),
        PaymentMethodItem(
            id = "wallet",
            title = "Wallets & Auto-Debit",
            subtitle = "Paytm Wallet, Amazon Pay",
            badge = "Auto Pay",
            icon = Icons.Default.AccountBalanceWallet,
            accentColor = Color(0xFFAF52DE)
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp),
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
                        Text(billType.iconEmoji, fontSize = 30.sp)
                        Spacer(modifier = Modifier.width(12.dp))
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
                                text = "4-Way Secure Payment System",
                                style = Typography.bodySmall.copy(
                                    color = AppleBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
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

                Spacer(modifier = Modifier.height(16.dp))

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
                            shadowElevation = 3.dp,
                            border = BorderStroke(1.dp, Color(0xFF34C759).copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8F5E9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Success",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(40.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Payment Successful!",
                                    style = Typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32),
                                        fontSize = 22.sp
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
                                ReceiptRow(label = "Payment Method", value = receiptPayment!!.paymentMode, isHighlight = true)
                                ReceiptRow(label = "Date & Time", value = receiptPayment!!.timestamp)
                                ReceiptRow(label = "Status", value = receiptPayment!!.status, isHighlight = true)

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = onDismiss,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    Text("Done", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    } else {
                        // Billing & 4-Way Payment Form
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Section 1: Bill Details Form
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                shadowElevation = 2.dp,
                                border = BorderStroke(1.dp, AppleBorderSubtle)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Receipt,
                                            contentDescription = null,
                                            tint = AppleBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "1. Account & Bill Details",
                                            style = Typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = AppleTextPrimary,
                                                fontSize = 16.sp
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Identifier
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
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = AppleTextPrimary,
                                            unfocusedTextColor = AppleTextPrimary,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = AppleBlue,
                                            unfocusedBorderColor = AppleBorderSubtle
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Provider Selection
                                    Text(
                                        text = if (billType == BillType.GAS) "Gas Provider / Board" else "Operator / Provider",
                                        style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(quickProviders) { p ->
                                            val isSelected = provider == p
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { provider = p },
                                                label = { Text(p, fontSize = 12.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = AppleBlue,
                                                    selectedLabelColor = Color.White
                                                )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = provider,
                                        onValueChange = { provider = it },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = AppleTextPrimary,
                                            unfocusedTextColor = AppleTextPrimary,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = AppleBlue,
                                            unfocusedBorderColor = AppleBorderSubtle
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Bill Amount
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
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = AppleTextPrimary,
                                            unfocusedTextColor = AppleTextPrimary,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = AppleBlue,
                                            unfocusedBorderColor = AppleBorderSubtle
                                        )
                                    )
                                }
                            }

                            // Section 2: 4-Way Payment System Selection
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                shadowElevation = 2.dp,
                                border = BorderStroke(1.dp, AppleBorderSubtle)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Payment,
                                                contentDescription = null,
                                                tint = Color(0xFF34C759),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "2. Select Payment Method",
                                                style = Typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = AppleTextPrimary,
                                                    fontSize = 16.sp
                                                )
                                            )
                                        }
                                        Text(
                                            text = "4 Options",
                                            style = Typography.bodySmall.copy(
                                                color = Color(0xFF34C759),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // 4 Payment Mode Cards
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        paymentMethods.forEach { method ->
                                            val isSelected = selectedPaymentMode == method.id
                                            PaymentModeSelectorCard(
                                                item = method,
                                                isSelected = isSelected,
                                                onClick = { selectedPaymentMode = method.id }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Dynamic Sub-Form based on Selected Payment Method
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = Color(0xFFF9F9FB),
                                            border = BorderStroke(1.dp, AppleBorderSubtle),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp)) {
                                                when (selectedPaymentMode) {
                                                    "upi" -> {
                                                        Text(
                                                            text = "Fast UPI Checkout",
                                                            style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                                                        )
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                            items(listOf("Google Pay", "PhonePe", "Paytm UPI", "BHIM UPI")) { app ->
                                                                FilterChip(
                                                                    selected = selectedUpiApp == app,
                                                                    onClick = { selectedUpiApp = app },
                                                                    label = { Text(app, fontSize = 12.sp) },
                                                                    colors = FilterChipDefaults.filterChipColors(
                                                                        selectedContainerColor = Color(0xFF0066CC),
                                                                        selectedLabelColor = Color.White
                                                                    )
                                                                )
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        OutlinedTextField(
                                                            value = upiId,
                                                            onValueChange = { upiId = it },
                                                            label = { Text("UPI ID (VPA)") },
                                                            shape = RoundedCornerShape(10.dp),
                                                            modifier = Modifier.fillMaxWidth(),
                                                            singleLine = true,
                                                            colors = OutlinedTextFieldDefaults.colors(
                                                                focusedTextColor = AppleTextPrimary,
                                                                unfocusedTextColor = AppleTextPrimary,
                                                                focusedContainerColor = Color.White,
                                                                unfocusedContainerColor = Color.White,
                                                                focusedBorderColor = AppleBlue,
                                                                unfocusedBorderColor = AppleBorderSubtle
                                                            )
                                                        )
                                                    }

                                                    "card" -> {
                                                        Text(
                                                            text = "Card Details",
                                                            style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                                                        )
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        OutlinedTextField(
                                                            value = cardNumber,
                                                            onValueChange = { cardNumber = it },
                                                            label = { Text("Card Number") },
                                                            shape = RoundedCornerShape(10.dp),
                                                            modifier = Modifier.fillMaxWidth(),
                                                            singleLine = true,
                                                            colors = OutlinedTextFieldDefaults.colors(
                                                                focusedTextColor = AppleTextPrimary,
                                                                unfocusedTextColor = AppleTextPrimary,
                                                                focusedContainerColor = Color.White,
                                                                unfocusedContainerColor = Color.White,
                                                                focusedBorderColor = AppleBlue,
                                                                unfocusedBorderColor = AppleBorderSubtle
                                                            )
                                                        )
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                            OutlinedTextField(
                                                                value = cardExpiry,
                                                                onValueChange = { cardExpiry = it },
                                                                label = { Text("Expiry (MM/YY)") },
                                                                shape = RoundedCornerShape(10.dp),
                                                                modifier = Modifier.weight(1f),
                                                                singleLine = true,
                                                                colors = OutlinedTextFieldDefaults.colors(
                                                                    focusedTextColor = AppleTextPrimary,
                                                                    unfocusedTextColor = AppleTextPrimary,
                                                                    focusedContainerColor = Color.White,
                                                                    unfocusedContainerColor = Color.White,
                                                                    focusedBorderColor = AppleBlue,
                                                                    unfocusedBorderColor = AppleBorderSubtle
                                                                )
                                                            )
                                                            OutlinedTextField(
                                                                value = cardCvv,
                                                                onValueChange = { cardCvv = it },
                                                                label = { Text("CVV") },
                                                                shape = RoundedCornerShape(10.dp),
                                                                modifier = Modifier.weight(1f),
                                                                singleLine = true,
                                                                colors = OutlinedTextFieldDefaults.colors(
                                                                    focusedTextColor = AppleTextPrimary,
                                                                    unfocusedTextColor = AppleTextPrimary,
                                                                    focusedContainerColor = Color.White,
                                                                    unfocusedContainerColor = Color.White,
                                                                    focusedBorderColor = AppleBlue,
                                                                    unfocusedBorderColor = AppleBorderSubtle
                                                                )
                                                            )
                                                        }
                                                    }

                                                    "netbanking" -> {
                                                        Text(
                                                            text = "Select Net Banking Provider",
                                                            style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                                                        )
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                            items(listOf("SBI", "HDFC Bank", "ICICI Bank", "Axis Bank", "PNB", "Kotak")) { bank ->
                                                                FilterChip(
                                                                    selected = selectedBank.startsWith(bank),
                                                                    onClick = { selectedBank = "$bank Net Banking" },
                                                                    label = { Text(bank, fontSize = 12.sp) },
                                                                    colors = FilterChipDefaults.filterChipColors(
                                                                        selectedContainerColor = Color(0xFFFF9500),
                                                                        selectedLabelColor = Color.White
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    "wallet" -> {
                                                        Text(
                                                            text = "Select Digital Wallet",
                                                            style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                                                        )
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                            items(listOf("Paytm Wallet", "Amazon Pay", "PhonePe Wallet", "Mobikwik")) { wallet ->
                                                                FilterChip(
                                                                    selected = selectedWallet == wallet,
                                                                    onClick = { selectedWallet = wallet },
                                                                    label = { Text(wallet, fontSize = 12.sp) },
                                                                    colors = FilterChipDefaults.filterChipColors(
                                                                        selectedContainerColor = Color(0xFFAF52DE),
                                                                        selectedLabelColor = Color.White
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Action Proceed Button
                                    val paymentModeTitle = when (selectedPaymentMode) {
                                        "upi" -> "UPI ($selectedUpiApp)"
                                        "card" -> "Debit/Credit Card"
                                        "netbanking" -> selectedBank
                                        "wallet" -> selectedWallet
                                        else -> "UPI"
                                    }

                                    Button(
                                        onClick = {
                                            isProcessing = true
                                            coroutineScope.launch {
                                                delay(1000L) // Mock payment processing
                                                val df = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                                                val newPayment = BillPayment(
                                                    id = "PAY-${System.currentTimeMillis() % 100000}",
                                                    type = billType,
                                                    identifier = identifier,
                                                    provider = provider,
                                                    amount = amount,
                                                    paymentMode = paymentModeTitle,
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
                                            .height(54.dp)
                                    ) {
                                        if (isProcessing) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text("Processing Payment...", fontWeight = FontWeight.Bold)
                                        } else {
                                            Text("Pay ₹$amount via $selectedPaymentMode".uppercase(), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
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
}

/**
 * Modern selection card for 4-Way Payment modes
 */
@Composable
private fun PaymentModeSelectorCard(
    item: PaymentMethodItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "payModeScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) item.accentColor.copy(alpha = 0.08f) else Color.White,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) item.accentColor else AppleBorderSubtle
        ),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio button selection indicator
            Icon(
                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) item.accentColor else AppleTextMuted,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Icon box
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title & Subtitle
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = Typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppleTextPrimary,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = item.accentColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = item.badge,
                            style = Typography.labelSmall.copy(
                                color = item.accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    style = Typography.bodySmall.copy(
                        color = AppleTextMuted,
                        fontSize = 12.sp
                    )
                )
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
