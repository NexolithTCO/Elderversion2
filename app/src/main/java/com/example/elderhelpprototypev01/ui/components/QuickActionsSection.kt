package com.example.elderhelpprototypev01.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.MedicalServices
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
 * Explore Tasks & Services section:
 *
 * [ 👨‍⚕️ Book Doctor → ]
 * [ 💡 Pay Bills → ]
 * [ 📄 Government Forms & Pension → ]
 * [ 🚨 Emergency Assistance → ]
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

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = strings.exploreTasks,
                    style = Typography.titleLarge.copy(
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppleTextPrimary
                    )
                )
                Text(
                    text = "Quick shortcuts to all Sahaay features",
                    style = Typography.bodySmall.copy(
                        color = AppleTextMuted,
                        fontSize = 12.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Clean Vertical Stack
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            actions.forEach { item ->
                QuickActionCard(
                    title = item.title,
                    subtitle = item.subtitle,
                    actionButtonText = item.buttonText,
                    icon = item.icon,
                    iconTint = item.iconTint,
                    iconBgColor = item.iconBgColor,
                    emoji = item.emoji,
                    onClick = { onActionClick(item) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
