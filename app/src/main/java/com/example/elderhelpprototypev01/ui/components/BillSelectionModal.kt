package com.example.elderhelpprototypev01.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.elderhelpprototypev01.model.BillType
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * BillSelectionModal
 *
 * Intermediate selection screen displayed when tapping "Pay" in Quick Tasks.
 * Shows all four payment options as uniform cards. Tapping one navigates
 * directly to that type's BillPaymentModal.
 */
@Composable
fun BillSelectionModal(
    currentLanguage: String = "English (India)",
    onDismiss: () -> Unit,
    onSelectBillType: (BillType) -> Unit
) {
    val isHindi = currentLanguage.contains("Hindi") || currentLanguage.contains("हिंदी")

    val billOptions = listOf(
        BillOption(
            type = BillType.ELECTRICITY,
            emoji = "⚡",
            label = if (isHindi) "बिजली बिल" else "Electricity Bill",
            hint = if (isHindi) "उपभोक्ता संख्या (Consumer ID)" else "Consumer ID",
            bgColor = TintElectricityBg,
            accentColor = TintElectricityIcon
        ),
        BillOption(
            type = BillType.WATER,
            emoji = "💧",
            label = if (isHindi) "पानी बिल" else "Water Bill",
            hint = if (isHindi) "मीटर संख्या (Meter ID)" else "Meter ID",
            bgColor = TintWaterBg,
            accentColor = TintWaterIcon
        ),
        BillOption(
            type = BillType.MOBILE,
            emoji = "📱",
            label = if (isHindi) "मोबाइल रिचार्ज" else "Mobile Recharge",
            hint = if (isHindi) "मोबाइल नंबर (Mobile Number)" else "Mobile Number",
            bgColor = TintMobileBg,
            accentColor = TintMobileIcon
        ),
        BillOption(
            type = BillType.GAS,
            emoji = "🔥",
            label = if (isHindi) "गैस बिल" else "Gas Bill",
            hint = if (isHindi) "उपभोक्ता संख्या / LPG ID" else "Consumer / LPG ID",
            bgColor = TintGasBg,
            accentColor = TintGasIcon
        )
    )

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = AppBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💳", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isHindi) "उपयोगिता बिल भुगतान" else "Pay Bills & Utilities",
                                style = Typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppTextPrimary,
                                    fontSize = 19.sp
                                )
                            )
                            Text(
                                text = if (isHindi) "जारी रखने के लिए श्रेणी चुनें" else "Select a category to continue",
                                style = Typography.bodySmall.copy(color = AppTextSecondary)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AppBorder.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Info banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AppleBlueLight,
                    border = BorderStroke(1.dp, AppleBlueSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🛡️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isHindi) "भारत बिलपे द्वारा सुरक्षित • रसीद तुरंत प्राप्त होगी" else "Instant receipt generated • BBPS Verified Safe",
                            style = Typography.bodySmall.copy(
                                color = AppPrimary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4 Category Cards
                billOptions.forEach { option ->
                    BillOptionCard(
                        option = option,
                        onClick = {
                            onDismiss()
                            onSelectBillType(option.type)
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Cancel Button
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, AppBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppTextSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = if (isHindi) "रद्द करें" else "Cancel",
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

private data class BillOption(
    val type: BillType,
    val emoji: String,
    val label: String,
    val hint: String,
    val bgColor: Color,
    val accentColor: Color
)

@Composable
private fun BillOptionCard(
    option: BillOption,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(18.dp),
        color = AppSurface,
        border = BorderStroke(1.dp, AppBorder),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(option.bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(option.emoji, fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = option.label,
                        style = Typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppTextPrimary,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = option.hint,
                        style = Typography.bodySmall.copy(
                            color = AppTextSecondary,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AppleBlueLight,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "Pay →",
                    style = Typography.labelMedium.copy(
                        color = AppPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
