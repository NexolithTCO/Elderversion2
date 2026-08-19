package com.example.elderhelpprototypev01.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.ui.localization.Localization
import com.example.elderhelpprototypev01.ui.theme.*

data class QuickActionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val buttonText: String,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBgColor: Color,
    val emoji: String? = null
)

/**
 * Editorial-Style Quick Tasks & Services Section:
 *
 * Clean grouped list with subtle dividers:
 * - 👨‍⚕️ Book Doctor Consultation
 * - 💡 Pay Utility Bills
 * - 📄 Government Forms & Pension
 * - 🚨 24/7 Emergency Assistance
 */
@Composable
fun QuickActionsSection(
    currentLanguage: String = "English (India)",
    onActionClick: (QuickActionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = Localization.getStrings(currentLanguage)

    val actions = listOf(
        QuickActionItem(
            id = "doctor",
            title = strings.bookDoctor,
            subtitle = strings.doctorSubtitle,
            buttonText = strings.doctorBtn,
            icon = Icons.Default.MedicalServices,
            iconTint = DoctorBlueIcon,
            iconBgColor = DoctorBlueBg,
            emoji = "👨‍⚕️"
        ),
        QuickActionItem(
            id = "bills",
            title = strings.billsTitle,
            subtitle = strings.billsSubtitle,
            buttonText = strings.billsBtn,
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            iconTint = BillsGreenIcon,
            iconBgColor = BillsGreenBg,
            emoji = "💡"
        ),
        QuickActionItem(
            id = "forms",
            title = strings.governmentForms,
            subtitle = strings.formsSubtitle,
            buttonText = strings.formsBtn,
            icon = Icons.Default.Description,
            iconTint = FormsOrangeIcon,
            iconBgColor = FormsOrangeBg,
            emoji = "📄"
        ),
        QuickActionItem(
            id = "emergency",
            title = strings.emergencyAssistance,
            subtitle = strings.sosSubtitle,
            buttonText = strings.sosBtn,
            icon = Icons.Default.LocalPolice,
            iconTint = SosRedIcon,
            iconBgColor = SosRedBg,
            emoji = "🚨"
        )
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Text(
            text = strings.exploreTasks,
            style = Typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppleTextPrimary
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = if (currentLanguage.contains("Hindi")) "आपातकालीन और दैनिक सेवाएं" else "Essential daily services and assistance",
            style = Typography.bodySmall.copy(
                color = AppleTextMuted,
                fontSize = 13.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Grouped Editorial Surface (16px radius, uniform #E4E7EC border)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AppSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                actions.forEachIndexed { index, item ->
                    val isEmergency = item.id == "emergency"
                    QuickActionRowItem(
                        title = item.title,
                        subtitle = item.subtitle,
                        icon = item.icon,
                        iconTint = item.iconTint,
                        iconBgColor = item.iconBgColor,
                        emoji = item.emoji,
                        isEmergency = isEmergency,
                        onClick = { onActionClick(item) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (index < actions.lastIndex) {
                        HorizontalDivider(
                            color = AppleBorderSubtle,
                            thickness = 0.6.dp,
                            modifier = Modifier.padding(start = 60.dp)
                        )
                    }
                }
            }
        }
    }
}
