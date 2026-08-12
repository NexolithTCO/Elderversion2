package com.example.elderhelpprototypev01.ui.components

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.ShieldMoon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.overlay.OverlayPermissionManager
import com.example.elderhelpprototypev01.overlay.SahaayOverlayService
import com.example.elderhelpprototypev01.overlay.SahaayPreferences
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * OverlayToggleCard
 *
 * A Compose card in the HomeScreen that lets the user:
 *  1. Grant the SYSTEM_ALERT_WINDOW overlay permission (opens system settings)
 *  2. Enable / Disable the floating Sahaay assistant
 *
 * Permission state and overlay enabled state are both reactive.
 * The card refreshes on every recomposition (lifecycle events in MainActivity
 * trigger recomposition via `overlayRefreshTick` state).
 */
@Composable
fun OverlayToggleCard(
    modifier: Modifier = Modifier,
    refreshTick: Int = 0  // increment to force re-check of permission
) {
    val context = LocalContext.current

    // Re-check every time refreshTick changes (i.e., on resume)
    val hasPermission by remember(refreshTick) {
        mutableStateOf(OverlayPermissionManager.canDrawOverlays(context))
    }
    var overlayEnabled by remember(refreshTick) {
        mutableStateOf(
            hasPermission && SahaayPreferences.isOverlayEnabled(context)
        )
    }

    val cardBg = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0F2537),
            Color(0xFF1E3A5A)
        )
    )
    val accentColor by animateColorAsState(
        targetValue = if (overlayEnabled) Color(0xFF34C759) else Color(0xFFFF9500),
        animationSpec = spring(),
        label = "accentColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBg)
                .padding(20.dp)
        ) {
            Column {
                // ---- Header Row ----
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Icon circle
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Sahaay Overlay",
                            tint = accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sahaay Assistant",
                            style = Typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = if (overlayEnabled) "Floating assistant is ON" else "Floating assistant is OFF",
                            style = Typography.bodySmall.copy(
                                color = accentColor,
                                fontSize = 13.sp
                            )
                        )
                    }

                    // Toggle switch
                    if (hasPermission) {
                        Switch(
                            checked = overlayEnabled,
                            onCheckedChange = { enabled ->
                                overlayEnabled = enabled
                                SahaayPreferences.setOverlayEnabled(context, enabled)
                                if (enabled) {
                                    startOverlayService(context)
                                } else {
                                    stopOverlayService(context)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF34C759),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFF636366)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ---- Status / Permission Section ----
                if (!hasPermission) {
                    // Show permission prompt
                    Surface(
                        color = Color(0xFFFFF3E0).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ShieldMoon,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9500),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Permission needed",
                                    style = Typography.labelLarge.copy(
                                        color = Color(0xFFFF9500),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "To show the floating Sahaay button above other apps, please allow \"Display over other apps\" in Settings.",
                                style = Typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val intent = OverlayPermissionManager.buildPermissionSettingsIntent(context)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF9500),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Open Settings → Grant Permission",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    // Show feature hints
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("🎙️ Voice", "👁️ Screen", "💡 Explain", "🆘 Help").forEach { hint ->
                            Surface(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = hint,
                                    style = Typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 12.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun startOverlayService(context: Context) {
    val intent = SahaayOverlayService.startIntent(context)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}

private fun stopOverlayService(context: Context) {
    context.stopService(SahaayOverlayService.stopIntent(context))
}
