package com.example.elderhelpprototypev01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.elderhelpprototypev01.overlay.SahaayOverlayService
import com.example.elderhelpprototypev01.ui.screens.SahaayHomeScreen
import com.example.elderhelpprototypev01.ui.theme.ElderHelpPrototypeV01Theme

class MainActivity : ComponentActivity() {

    companion object {
        /** Intent extra: open the voice tab directly */
        const val EXTRA_OPEN_VOICE_TAB = "open_voice_tab"
    }

    // ViewModel owned at Activity scope — survives tab switches
    private val sahaayViewModel: SahaayViewModel by viewModels()

    // Which tab to open
    private var initialTab by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Stop any background overlay service so floating overlay assistant is completely removed
        try {
            stopService(SahaayOverlayService.stopIntent(this))
        } catch (_: Exception) {}

        // Handle intent from voice action triggers
        handleVoiceTabIntent()

        setContent {
            ElderHelpPrototypeV01Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SahaayHomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = sahaayViewModel,
                        initialTab = initialTab
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleVoiceTabIntent()
    }

    private fun handleVoiceTabIntent() {
        if (intent?.getBooleanExtra(EXTRA_OPEN_VOICE_TAB, false) == true) {
            initialTab = 1
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SahaayHomeScreenMainPreview() {
    ElderHelpPrototypeV01Theme {
        SahaayHomeScreen()
    }
}