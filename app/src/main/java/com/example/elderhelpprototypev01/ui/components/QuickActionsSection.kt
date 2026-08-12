package com.example.elderhelpprototypev01.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Description
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
    val iconBgColor: Color
)

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
            title = strings.doctorTitle,
            subtitle = strings.doctorSubtitle,
            buttonText = strings.doctorBtn,
            icon = Icons.Default.MedicalServices,
            iconTint = DoctorBlueIcon,
            iconBgColor = DoctorBlueBg
        ),
        QuickActionItem(
            id = "bills",
            title = strings.billsTitle,
            subtitle = strings.billsSubtitle,
            buttonText = strings.billsBtn,
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            iconTint = BillsGreenIcon,
            iconBgColor = BillsGreenBg
        ),
        QuickActionItem(
            id = "forms",
            title = strings.formsTitle,
            subtitle = strings.formsSubtitle,
            buttonText = strings.formsBtn,
            icon = Icons.Default.Description,
            iconTint = FormsOrangeIcon,
            iconBgColor = FormsOrangeBg
        ),
        QuickActionItem(
            id = "help",
            title = strings.helpTitle,
            subtitle = strings.helpSubtitle,
            buttonText = strings.helpBtn,
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            iconTint = HelpPurpleIcon,
            iconBgColor = HelpPurpleBg
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
            Text(
                text = strings.exploreTasks,
                style = Typography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleTextPrimary
                )
            )
            Text(
                text = strings.seeAll,
                style = Typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = AppleBlue,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2x2 Grid of Action Cards
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            actions.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    rowItems.forEach { item ->
                        QuickActionCard(
                            title = item.title,
                            subtitle = item.subtitle,
                            actionButtonText = item.buttonText,
                            icon = item.icon,
                            iconTint = item.iconTint,
                            iconBgColor = item.iconBgColor,
                            onClick = { onActionClick(item) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
