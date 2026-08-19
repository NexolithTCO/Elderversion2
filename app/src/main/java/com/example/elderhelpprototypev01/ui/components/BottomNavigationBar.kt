package com.example.elderhelpprototypev01.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
        shadowElevation = 10.dp,
        tonalElevation = 2.dp
    ) {
        Column {
            HorizontalDivider(
                color = AppleBorderSubtle,
                thickness = 0.8.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = index == selectedTab

                    val animatedIconTint by animateColorAsState(
                        targetValue = if (isSelected) AppleBlue else AppleTextMuted,
                        animationSpec = tween(250),
                        label = "navIconTint"
                    )

                    val animatedPillBg by animateColorAsState(
                        targetValue = if (isSelected) AppleBlueLight else Color.Transparent,
                        animationSpec = tween(250),
                        label = "navPillBg"
                    )

                    val interactionSource = remember { MutableInteractionSource() }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                role = Role.Tab,
                                onClick = { onTabSelected(index) }
                            )
                            .semantics {
                                contentDescription = "${item.title} tab, ${if (isSelected) "selected" else "not selected"}"
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        // Pill background for active tab
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .height(34.dp)
                                .width(56.dp)
                                .clip(RoundedCornerShape(17.dp))
                                .background(animatedPillBg)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = animatedIconTint,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.title,
                            style = Typography.bodySmall.copy(
                                color = if (isSelected) AppleBlue else AppleTextMuted,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
