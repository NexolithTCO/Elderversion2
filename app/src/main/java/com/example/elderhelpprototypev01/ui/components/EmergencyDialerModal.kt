package com.example.elderhelpprototypev01.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Backspace
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.elderhelpprototypev01.model.UserProfile
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * EmergencyDialerModal
 *
 * Dedicated Elderly-Accessible Emergency Number Dialer with:
 * - 1-Tap quick helplines (112 Police, 102 Ambulance, 14567 Elderline, 101 Fire, 1091 Women Helpline, Saved Contact)
 * - Large visual display with backspace & clear
 * - Accessible 12-key numeric dialpad
 * - Direct phone call execution via Intent.ACTION_CALL
 */
@Composable
fun EmergencyDialerModal(
    userProfile: UserProfile = UserProfile(),
    currentLanguage: String = "English (India)",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isHindi = currentLanguage.contains("Hindi") || currentLanguage.contains("हिंदी")

    var dialedNumber by remember {
        mutableStateOf(
            if (userProfile.emergencyContactPhone.isNotBlank()) userProfile.emergencyContactPhone else "112"
        )
    }

    val callPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (dialedNumber.isNotBlank()) {
            EmergencyCallHelper.makeCall(context, dialedNumber)
        }
    }

    fun callDirectly(numberToCall: String) {
        val clean = numberToCall.ifBlank { dialedNumber }
        if (clean.isBlank()) return

        if (EmergencyCallHelper.hasCallPermission(context)) {
            EmergencyCallHelper.makeCall(context, clean)
        } else {
            dialedNumber = clean
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    val scrollState = rememberScrollState()

    val quickHelplines = remember(userProfile, isHindi) {
        val list = mutableListOf(
            QuickHelpline("112", if (isHindi) "पुलिस / 112" else "Police 112", "🚨", Color(0xFFFFEBEE), AppEmergency),
            QuickHelpline("102", if (isHindi) "एम्बुलेंस / 102" else "Ambulance 102", "🚑", AppleBlueLight, AppPrimary),
            QuickHelpline("14567", if (isHindi) "एल्डरलाइन / 14567" else "Elderline 14567", "🧓", TintMobileBg, TintMobileIcon),
            QuickHelpline("101", if (isHindi) "दमकल / 101" else "Fire 101", "🚒", TintGasBg, TintGasIcon),
            QuickHelpline("1091", if (isHindi) "महिला हेल्पलाइन" else "Women 1091", "👩", Color(0xFFFCE4EC), Color(0xFFC2185B))
        )
        if (userProfile.emergencyContactPhone.isNotBlank()) {
            list.add(
                0,
                QuickHelpline(
                    userProfile.emergencyContactPhone,
                    userProfile.emergencyContactDisplayName.ifBlank { if (isHindi) "पारिवारिक संपर्क" else "Family Contact" },
                    "👨‍👩‍👧",
                    Color(0xFFDCFCE7),
                    AppSuccess
                )
            )
        }
        list
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = AppBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Modal Header
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
                                .background(SosRedBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🚨", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isHindi) "आपातकालीन डायलर" else "Emergency Dialer",
                                style = Typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppTextPrimary,
                                    fontSize = 19.sp
                                )
                            )
                            Text(
                                text = if (isHindi) "तत्काल सीधी आपातकालीन कॉल" else "Direct emergency calling",
                                style = Typography.bodySmall.copy(color = AppTextSecondary)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AppBorder.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Quick Helplines Horizontal Carousel
                Text(
                    text = if (isHindi) "त्वरित आपातकालीन नंबर" else "QUICK HELPLINE NUMBERS",
                    style = Typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppTextMuted,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickHelplines) { item ->
                        Surface(
                            onClick = {
                                dialedNumber = item.number
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = item.bgColor,
                            border = BorderStroke(1.dp, item.accentColor.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.emoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = item.label,
                                        style = Typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = item.accentColor,
                                            fontSize = 12.5.sp
                                        )
                                    )
                                    Text(
                                        text = item.number,
                                        style = Typography.labelSmall.copy(
                                            color = AppTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Dial Display Screen
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = AppSurface,
                    border = BorderStroke(1.5.dp, if (dialedNumber.isNotBlank()) AppEmergency else AppBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (dialedNumber.isBlank()) (if (isHindi) "नंबर डायल करें..." else "Enter number...") else dialedNumber,
                            style = Typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (dialedNumber.isBlank()) AppTextMuted else AppTextPrimary,
                                fontSize = 26.sp,
                                letterSpacing = 1.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (dialedNumber.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    if (dialedNumber.isNotEmpty()) {
                                        dialedNumber = dialedNumber.dropLast(1)
                                    }
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "Backspace",
                                    tint = AppEmergency,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 12-Key Numeric Keypad
                val dialPadKeys = listOf(
                    listOf("1" to "", "2" to "ABC", "3" to "DEF"),
                    listOf("4" to "GHI", "5" to "JKL", "6" to "MNO"),
                    listOf("7" to "PQRS", "8" to "TUV", "9" to "WXYZ"),
                    listOf("*" to "", "0" to "+", "#" to "")
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    dialPadKeys.forEach { rowKeys ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            rowKeys.forEach { (mainKey, subKey) ->
                                DialPadKey(
                                    digit = mainKey,
                                    subtext = subKey,
                                    onClick = {
                                        if (dialedNumber.length < 15) {
                                            dialedNumber += mainKey
                                        }
                                    },
                                    onLongClick = {
                                        if (mainKey == "0" && dialedNumber.length < 15) {
                                            dialedNumber += "+"
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Big Green Direct CALL Button
                Button(
                    onClick = {
                        if (dialedNumber.isNotBlank()) {
                            callDirectly(dialedNumber)
                        }
                    },
                    enabled = dialedNumber.isNotBlank(),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppEmergency,
                        contentColor = Color.White,
                        disabledContainerColor = AppBorder,
                        disabledContentColor = AppTextMuted
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isHindi) "सीधे कॉल करें" else "CALL EMERGENCY NOW",
                            style = Typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Cancel Button
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, AppBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppTextSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isHindi) "बंद करें" else "Close",
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    )
                }
            }
        }
    }
}

private data class QuickHelpline(
    val number: String,
    val label: String,
    val emoji: String,
    val bgColor: Color,
    val accentColor: Color
)

@Composable
private fun DialPadKey(
    digit: String,
    subtext: String,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "dialKeyScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        color = AppSurface,
        border = BorderStroke(1.dp, AppBorder),
        shadowElevation = if (isPressed) 0.dp else 1.dp,
        modifier = modifier
            .height(58.dp)
            .scale(scale)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = digit,
                style = Typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppTextPrimary,
                    fontSize = 22.sp
                )
            )
            if (subtext.isNotBlank()) {
                Text(
                    text = subtext,
                    style = Typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = AppTextMuted,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}
