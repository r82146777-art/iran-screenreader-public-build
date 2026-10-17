package ir.iran.screenreader

import android.content.Context

object FeatureFlags {
    private const val P = "feature_flags_v3"
    private val defaults = mapOf(
        "speak_events" to true, "speak_notifications" to true, "gesture_mode" to true,
        "voice_commands" to true, "private_notifications" to true, "smart_silence" to true,
        "read_image" to true, "read_barcode" to true, "web_search" to true,
        "visual_fallback" to true, "read_actions" to true, "read_state" to true,
        "plugins_enabled" to true
    )
    fun get(c: Context, key: String): Boolean =
        c.getSharedPreferences(P, 0).getBoolean(key, defaults[key] ?: false)
    fun set(c: Context, key: String, value: Boolean) =
        c.getSharedPreferences(P, 0).edit().putBoolean(key, value).apply()
}
