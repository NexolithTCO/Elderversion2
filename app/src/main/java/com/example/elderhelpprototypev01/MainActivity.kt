package com.example.elderhelpprototypev01

import android.os.Build
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
import com.example.elderhelpprototypev01.overlay.OverlayPermissionManager
import com.example.elderhelpprototypev01.overlay.SahaayOverlayService
import com.example.elderhelpprototypev01.overlay.SahaayPreferences
import com.example.elderhelpprototypev01.ui.screens.SahaayHomeScreen
import com.example.elderhelpprototypev01.ui.theme.ElderHelpPrototypeV01Theme

class MainActivity : ComponentActivity() {

    companion object {
        /** Intent extra: open the voice tab directly (used by overlay Voice button) */
        const val EXTRA_OPEN_VOICE_TAB = "open_voice_tab"
    }

    // ViewModel owned at Activity scope — survives tab switches
    private val sahaayViewModel: SahaayViewModel by viewModels()

    // Incremented every onResume so Compose re-checks overlay permission & state
    private var overlayRefreshTick by mutableIntStateOf(0)

    // Which tab to open (can be set by overlay intent)
    private var initialTab by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle intent from overlay Voice button
        handleVoiceTabIntent()

        setContent {
            ElderHelpPrototypeV01Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SahaayHomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        overlayRefreshTick = overlayRefreshTick,
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

    override fun onResume() {
        super.onResume()
        // Increment tick → triggers recomposition → re-evaluates overlay permission state
        overlayRefreshTick++

        // If the user just granted overlay permission AND had the overlay enabled,
        // restart the service automatically.
        if (OverlayPermissionManager.canDrawOverlays(this) &&
            SahaayPreferences.isOverlayEnabled(this)
        ) {
            startOverlayService()
        }
    }

    private fun handleVoiceTabIntent() {
        if (intent?.getBooleanExtra(EXTRA_OPEN_VOICE_TAB, false) == true) {
            initialTab = 1
        }
    }

    private fun startOverlayService() {
        val serviceIntent = SahaayOverlayService.startIntent(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
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