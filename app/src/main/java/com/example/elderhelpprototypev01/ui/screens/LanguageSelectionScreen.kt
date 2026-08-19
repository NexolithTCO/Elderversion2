package com.example.elderhelpprototypev01.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderhelpprototypev01.overlay.SahaayPreferences
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * LanguageSelectionScreen
 *
 * First screen shown on a fresh install / first launch.
 * Presents two large, accessible tiles — English (A) and हिंदी (अ).
 * On selection, the language is persisted and [onLanguageSelected] is invoked
 * to navigate to the Login/Signup screen.
 */
@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (language: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF007AFF).copy(alpha = 0.08f), AppleCanvasBg)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // App Logo / Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(AppleBlueLight, RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🙏", fontSize = 42.sp)
            }

            Spacer(modifier = Modifier.height(22.dp))

            // App name
            Text(
                text = "Sahaay",
                style = Typography.headlineLarge.copy(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppleBlue
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = "Choose Your Language\nअपनी भाषा चुनें",
                style = Typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppleTextPrimary,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select the language you are comfortable with\nवह भाषा चुनें जो आपको सबसे अच्छी लगे",
                style = Typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = AppleTextMuted,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Language Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LanguageTile(
                    symbol = "A",
                    label = "English",
                    sublabel = "English (India)",
                    isSelected = selected == "English (India)",
                    accentColor = AppleBlue,
                    onClick = { selected = "English (India)" },
                    modifier = Modifier.weight(1f)
                )
                LanguageTile(
                    symbol = "अ",
                    label = "हिंदी",
                    sublabel = "Hindi (India)",
                    isSelected = selected == "Hindi (हिंदी)",
                    accentColor = Color(0xFFFF9500),
                    onClick = { selected = "Hindi (हिंदी)" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Continue button — enabled only once a language is chosen
            Button(
                onClick = {
                    val lang = selected ?: return@Button
                    SahaayPreferences.setLanguage(context, lang)
                    onLanguageSelected(lang)
                },
                enabled = selected != null,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppleBlue,
                    disabledContainerColor = AppleBorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = if (selected == "Hindi (हिंदी)") "आगे बढ़ें  →" else "Continue  →",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        }
    }
}

/**
 * Single accessible language selection tile.
 */
@Composable
private fun LanguageTile(
    symbol: String,
    label: String,
    sublabel: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else AppleBorderSubtle,
        animationSpec = tween(200),
        label = "tileBorder"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.10f) else Color.White,
        animationSpec = tween(200),
        label = "tileBg"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(160.dp)
            .semantics { contentDescription = "Select $label" },
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = BorderStroke(
            width = if (isSelected) 2.5.dp else 1.dp,
            color = borderColor
        ),
        shadowElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Large symbol
            Text(
                text = symbol,
                style = Typography.displayMedium.copy(
                    fontSize = 52.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) accentColor else AppleTextMuted
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                style = Typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isSelected) accentColor else AppleTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = sublabel,
                style = Typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = AppleTextMuted
                )
            )
        }
    }
}
