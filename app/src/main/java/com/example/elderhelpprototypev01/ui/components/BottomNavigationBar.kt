package com.example.elderhelpprototypev01.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.ui.localization.Localization
import com.example.elderhelpprototypev01.ui.theme.*

data class NavItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun BottomNavigationBar(
    selectedTab: Int = 0,
    currentLanguage: String = "English (India)",
    onTabSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = Localization.getStrings(currentLanguage)

    val items = listOf(
        NavItem(strings.navHome, Icons.Default.Home),
        NavItem(strings.navVoice, Icons.Default.Mic),
        NavItem(strings.navTransactions, Icons.AutoMirrored.Filled.ReceiptLong),
        NavItem(strings.navSettings, Icons.Default.Settings)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = AppleSurfaceWhite,
        shadowElevation = 12.dp,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedTab
                val color = if (isSelected) AppleBlue else AppleTextMuted

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = item.title,
                        style = Typography.bodyMedium.copy(
                            color = color,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
