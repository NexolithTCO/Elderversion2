package com.example.elderhelpprototypev01.overlay

import android.content.Context

/**
 * Lightweight SharedPreferences wrapper for Sahaay app state.
 *
 * Persists:
 *  - Overlay toggle state
 *  - Language selection (chosen on first launch)
 *  - First-launch flag (cleared after login/signup completes)
 *  - Full user profile fields (name, phone, address, emergency contact)
 */
object SahaayPreferences {

    private const val PREFS_NAME = "sahaay_prefs"

    // ── Overlay ──────────────────────────────────────────────────────────────
    private const val KEY_OVERLAY_ENABLED = "overlay_enabled"

    // ── App launch gate ───────────────────────────────────────────────────────
    /** True until the user completes Language Selection + Login/Signup. */
    private const val KEY_FIRST_LAUNCH = "first_launch"

    // ── Language ─────────────────────────────────────────────────────────────
    private const val KEY_LANGUAGE = "selected_language"

    // ── User Profile ─────────────────────────────────────────────────────────
    private const val KEY_PROFILE_FULL_NAME              = "profile_full_name"
    private const val KEY_PROFILE_AGE                    = "profile_age"
    private const val KEY_PROFILE_CONTACT_NUMBER         = "profile_contact_number"
    private const val KEY_PROFILE_ADDRESS                = "profile_address"
    private const val KEY_PROFILE_EMERGENCY_NAME         = "profile_emergency_name"
    private const val KEY_PROFILE_EMERGENCY_RELATIONSHIP = "profile_emergency_relationship"
    private const val KEY_PROFILE_EMERGENCY_PHONE        = "profile_emergency_phone"

    // ── Overlay ───────────────────────────────────────────────────────────────

    fun setOverlayEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_OVERLAY_ENABLED, enabled).apply()
    }

    fun isOverlayEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_OVERLAY_ENABLED, false)

    // ── App launch gate ───────────────────────────────────────────────────────

    /** Returns true when the app has never completed the onboarding flow. */
    fun isFirstLaunch(context: Context): Boolean =
        prefs(context).getBoolean(KEY_FIRST_LAUNCH, true)

    /** Call after the user finishes Login/Signup to mark onboarding complete. */
    fun markOnboardingComplete(context: Context) {
        prefs(context).edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    // ── Language ──────────────────────────────────────────────────────────────

    fun setLanguage(context: Context, language: String) {
        prefs(context).edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getLanguage(context: Context): String =
        prefs(context).getString(KEY_LANGUAGE, "English (India)") ?: "English (India)"

    // ── User Profile ─────────────────────────────────────────────────────────

    fun saveProfile(
        context: Context,
        fullName: String,
        age: String,
        contactNumber: String,
        address: String,
        emergencyName: String,
        emergencyRelationship: String,
        emergencyPhone: String
    ) {
        prefs(context).edit()
            .putString(KEY_PROFILE_FULL_NAME,              fullName)
            .putString(KEY_PROFILE_AGE,                    age)
            .putString(KEY_PROFILE_CONTACT_NUMBER,         contactNumber)
            .putString(KEY_PROFILE_ADDRESS,                address)
            .putString(KEY_PROFILE_EMERGENCY_NAME,         emergencyName)
            .putString(KEY_PROFILE_EMERGENCY_RELATIONSHIP, emergencyRelationship)
            .putString(KEY_PROFILE_EMERGENCY_PHONE,        emergencyPhone)
            .apply()
    }

    fun getProfileFullName(context: Context): String =
        prefs(context).getString(KEY_PROFILE_FULL_NAME, "") ?: ""

    fun getProfileAge(context: Context): String =
        prefs(context).getString(KEY_PROFILE_AGE, "") ?: ""

    fun getProfileContactNumber(context: Context): String =
        prefs(context).getString(KEY_PROFILE_CONTACT_NUMBER, "") ?: ""

    fun getProfileAddress(context: Context): String =
        prefs(context).getString(KEY_PROFILE_ADDRESS, "") ?: ""

    fun getProfileEmergencyName(context: Context): String =
        prefs(context).getString(KEY_PROFILE_EMERGENCY_NAME, "") ?: ""

    fun getProfileEmergencyRelationship(context: Context): String =
        prefs(context).getString(KEY_PROFILE_EMERGENCY_RELATIONSHIP, "") ?: ""

    fun getProfileEmergencyPhone(context: Context): String =
        prefs(context).getString(KEY_PROFILE_EMERGENCY_PHONE, "") ?: ""

    /** True only if the user has saved at least a name during onboarding. */
    fun hasProfile(context: Context): Boolean =
        getProfileFullName(context).isNotBlank()

    // ── Internal helper ──────────────────────────────────────────────────────

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}

