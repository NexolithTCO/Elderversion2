package com.example.elderhelpprototypev01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.elderhelpprototypev01.overlay.SahaayOverlayService
import com.example.elderhelpprototypev01.overlay.SahaayPreferences
import com.example.elderhelpprototypev01.ui.screens.LanguageSelectionScreen
import com.example.elderhelpprototypev01.ui.screens.LoginSignupScreen
import com.example.elderhelpprototypev01.ui.screens.SahaayHomeScreen
import com.example.elderhelpprototypev01.ui.theme.AppleCanvasBg
import com.example.elderhelpprototypev01.ui.theme.ElderHelpPrototypeV01Theme

enum class OnboardingStep {
    LANGUAGE_SELECTION,
    LOGIN_SIGNUP,
    COMPLETED
}

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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppleCanvasBg
                ) {
                    val context = LocalContext.current

                    // Restore ViewModel state from saved preferences if returning user
                    remember {
                        if (!SahaayPreferences.isFirstLaunch(context) && SahaayPreferences.hasProfile(context)) {
                            sahaayViewModel.initFromPreferences(context)
                        }
                        true
                    }

                    var onboardingStep by remember {
                        mutableStateOf(
                            if (SahaayPreferences.isFirstLaunch(context) || !SahaayPreferences.hasProfile(context)) {
                                OnboardingStep.LANGUAGE_SELECTION
                            } else {
                                OnboardingStep.COMPLETED
                            }
                        )
                    }

                    var chosenLanguage by remember {
                        mutableStateOf(SahaayPreferences.getLanguage(context))
                    }

                    when (onboardingStep) {
                        OnboardingStep.LANGUAGE_SELECTION -> {
                            LanguageSelectionScreen(
                                onLanguageSelected = { lang ->
                                    chosenLanguage = lang
                                    sahaayViewModel.setLanguage(lang)
                                    onboardingStep = OnboardingStep.LOGIN_SIGNUP
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        OnboardingStep.LOGIN_SIGNUP -> {
                            LoginSignupScreen(
                                language = chosenLanguage,
                                onProfileSaved = { profile ->
                                    sahaayViewModel.updateUserProfile(profile)
                                    SahaayPreferences.markOnboardingComplete(context)
                                    onboardingStep = OnboardingStep.COMPLETED
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        OnboardingStep.COMPLETED -> {
                            SahaayHomeScreen(
                                viewModel = sahaayViewModel,
                                initialTab = initialTab,
                                onReliveLoginClick = {
                                    onboardingStep = OnboardingStep.LANGUAGE_SELECTION
                                }
                            )
                        }
                    }
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