package com.example.elderhelpprototypev01.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.elderhelpprototypev01.ui.localization.Localization
import com.example.elderhelpprototypev01.ui.theme.Typography
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import android.location.Location
import kotlin.coroutines.resume
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Emergency Services Modal
 *
 * Senior-friendly emergency choice screen with two prominent options:
 * 1. Call Emergency Contact — immediately dials the saved contact
 * 2. Find Nearest Police Station — uses GPS + Google Maps search
 *
 * Matches the app's dark high-contrast modal design system.
 */
@Composable
fun EmergencyServicesModal(
    onDismiss: () -> Unit,
    contactName: String = "Rahul",
    contactNumber: String = "+91 98765 43210",
    currentLanguage: String = "English (India)"
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val strings = Localization.getStrings(currentLanguage)

    // Location state
    var isLoadingLocation by remember { mutableStateOf(false) }
    var locationDenied by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            locationDenied = false
            // Permission just granted — trigger location fetch
            isLoadingLocation = true
            scope.launch {
                fetchLocationAndOpenMaps(context, strings.openingMaps) { success ->
                    isLoadingLocation = false
                    if (!success) {
                        feedbackMessage = "Unable to get location. Please try again."
                    }
                }
            }
        } else {
            locationDenied = true
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        // Full-screen dark overlay backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .border(2.dp, Color(0xFFFF3B30), RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF1C1C1E),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Header icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF0000).copy(alpha = 0.15f))
                            .border(2.dp, Color(0xFFFF0000), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency",
                            tint = Color(0xFFFF0000),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = strings.emergencyServicesTitle.uppercase(),
                        style = Typography.headlineSmall.copy(
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Option 1: Call Emergency Contact
                    EmergencyActionButton(
                        icon = Icons.Default.PhoneInTalk,
                        title = strings.callEmergencyContact,
                        subtitle = "$contactName • $contactNumber",
                        accentColor = Color(0xFF34C759),
                        feedbackText = feedbackMessage?.takeIf { it == strings.callingContact },
                        onClick = {
                            feedbackMessage = strings.callingContact
                            triggerCall(context, contactNumber)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Option 2: Find Nearest Police Station
                    EmergencyActionButton(
                        icon = Icons.Default.LocalPolice,
                        title = strings.findNearestPolice,
                        subtitle = if (isLoadingLocation) strings.openingMaps else "🚔 Google Maps",
                        accentColor = Color(0xFF0066CC),
                        isLoading = isLoadingLocation,
                        onClick = {
                            val hasFine = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                            val hasCoarse = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasFine || hasCoarse) {
                                isLoadingLocation = true
                                locationDenied = false
                                scope.launch {
                                    fetchLocationAndOpenMaps(context, strings.openingMaps) { success ->
                                        isLoadingLocation = false
                                        if (!success) {
                                            feedbackMessage = "Unable to get location. Please try again."
                                        }
                                    }
                                }
                            } else {
                                // Request location permission
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        }
                    )

                    // Location permission denied message
                    if (locationDenied) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF2C2C2E),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9500),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = strings.locationRequired,
                                        style = Typography.bodyMedium.copy(
                                            color = Color(0xFFFF9500),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF9500),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = strings.grantLocation,
                                        style = Typography.titleMedium.copy(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Close button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3A3A3C),
                            contentColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            Color.White.copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.close.uppercase(),
                                style = Typography.titleMedium.copy(
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Large, accessible emergency action button with icon, title, subtitle,
 * and spring-physics press animation matching the app's design system.
 */
@Composable
private fun EmergencyActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    isLoading: Boolean = false,
    feedbackText: String? = null,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "emergencyBtnScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            },
        shape = RoundedCornerShape(20.dp),
        color = accentColor.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f)),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(1.5.dp, accentColor, CircleShape)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = accentColor,
                        strokeWidth = 3.dp
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = Typography.titleMedium.copy(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = feedbackText ?: subtitle,
                    style = Typography.bodyMedium.copy(
                        color = if (feedbackText != null) accentColor else Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = if (feedbackText != null) FontWeight.SemiBold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

/**
 * Fetches the device's current GPS location and opens Google Maps
 * to search for "police station near me" at those coordinates.
 */
private suspend fun fetchLocationAndOpenMaps(
    context: Context,
    toastMessage: String,
    onResult: (Boolean) -> Unit
) {
    try {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        val cancellationToken = CancellationTokenSource()

        @Suppress("MissingPermission")
        val location: Location? = suspendCancellableCoroutine { continuation ->
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).addOnSuccessListener { loc ->
                if (continuation.isActive) continuation.resume(loc)
            }.addOnFailureListener {
                if (continuation.isActive) continuation.resume(null)
            }.addOnCanceledListener {
                if (continuation.isActive) continuation.resume(null)
            }
            continuation.invokeOnCancellation {
                cancellationToken.cancel()
            }
        }

        if (location != null) {
            val lat = location.latitude
            val lng = location.longitude
            // Open Google Maps with a nearby search for police stations
            val gmmUri = Uri.parse("geo:$lat,$lng?q=police+station+near+me")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmUri).apply {
                setPackage("com.google.android.apps.maps")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            // Fall back to browser if Google Maps is not installed
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            } else {
                // Open in browser as fallback
                val webUri = Uri.parse(
                    "https://www.google.com/maps/search/police+station+near+me/@$lat,$lng,14z"
                )
                val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            }
            onResult(true)
        } else {
            Toast.makeText(context, "Unable to determine location. Please try again.", Toast.LENGTH_LONG).show()
            onResult(false)
        }
    } catch (e: SecurityException) {
        Toast.makeText(context, "Location permission required.", Toast.LENGTH_LONG).show()
        onResult(false)
    } catch (e: Exception) {
        Toast.makeText(context, "Error getting location: ${e.message}", Toast.LENGTH_LONG).show()
        onResult(false)
    }
}

/**
 * Triggers phone call intent to dial the emergency contact.
 */
private fun triggerCall(context: Context, phoneNumber: String) {
    try {
        val cleanNumber = phoneNumber.replace(" ", "").replace("-", "")
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$cleanNumber")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        Toast.makeText(context, "Dialing Emergency Contact ($cleanNumber)...", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to launch dialer: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
