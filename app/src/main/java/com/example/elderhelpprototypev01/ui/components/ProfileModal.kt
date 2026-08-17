package com.example.elderhelpprototypev01.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.elderhelpprototypev01.model.UserProfile
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * ProfileModal
 *
 * Profile Creation & Edit form allowing users to manage identity and emergency contact info:
 * - Full Name
 * - Age / Date of Birth
 * - Contact Number
 * - Emergency Contact Info (Name + Phone)
 * - Address / Location
 */
@Composable
fun ProfileModal(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSaveProfile: (UserProfile) -> Unit
) {
    var fullName by remember { mutableStateOf(profile.fullName) }
    var age by remember { mutableStateOf(profile.age) }
    var contactNumber by remember { mutableStateOf(profile.contactNumber) }
    var emergencyContactName by remember { mutableStateOf(profile.emergencyContactName) }
    var emergencyContactPhone by remember { mutableStateOf(profile.emergencyContactPhone) }
    var address by remember { mutableStateOf(profile.address) }

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
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AppleBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = AppleBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "My Profile & Emergency Info",
                                style = Typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppleTextPrimary,
                                    fontSize = 19.sp
                                )
                            )
                            Text(
                                text = "Personal setup for Sahaay & SOS safety",
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

                // Scrollable Form Fields
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, AppleBorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Section: Personal Details
                            Text(
                                text = "PERSONAL DETAILS",
                                style = Typography.labelMedium.copy(
                                    color = AppleBlue,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 12.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Full Name
                            Text("Full Name", style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary))
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                placeholder = { Text("e.g. Ramesh Sharma") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Age / DOB
                            Text("Age / Date of Birth", style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary))
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = age,
                                onValueChange = { age = it },
                                placeholder = { Text("e.g. 68 years") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Contact Number
                            Text("Contact Number", style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary))
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = contactNumber,
                                onValueChange = { contactNumber = it },
                                placeholder = { Text("e.g. +91 98765 12345") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Address / Location
                            Text("Address / City Location", style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary))
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                placeholder = { Text("e.g. Bandra West, Mumbai") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Section: Emergency Contact Info
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, AppleBorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🚨", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "EMERGENCY CONTACT (FOR SOS)",
                                    style = Typography.labelMedium.copy(
                                        color = Color(0xFFFF3B30),
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Emergency Contact Person Name
                            Text("Emergency Contact Name & Relation", style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary))
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = emergencyContactName,
                                onValueChange = { emergencyContactName = it },
                                placeholder = { Text("e.g. Rahul (Son)") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Emergency Contact Phone
                            Text("Emergency Phone Number", style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary))
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = emergencyContactPhone,
                                onValueChange = { emergencyContactPhone = it },
                                placeholder = { Text("e.g. +91 98765 43210") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button
                    Button(
                        onClick = {
                            val updated = UserProfile(
                                fullName = fullName.trim(),
                                age = age.trim(),
                                contactNumber = contactNumber.trim(),
                                emergencyContactName = emergencyContactName.trim(),
                                emergencyContactPhone = emergencyContactPhone.trim(),
                                address = address.trim()
                            )
                            onSaveProfile(updated)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text("Save Profile Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
