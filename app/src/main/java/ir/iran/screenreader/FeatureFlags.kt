package ir.iran.screenreader
import android.content.Context
object FeatureFlags {
    private const val P = "feature_flags_v5"
    private val defaults = mapOf(
        "speak_events" to true, "speak_notifications" to true, "gesture_mode" to true,
        "voice_commands" to true, "smart_silence" to true, "visual_fallback" to true
    )
    fun get(c: Context, key: String): Boolean =
        c.getSharedPreferences(P, 0).getBoolean(key, defaults[key] ?: false)
    fun set(c: Context, key: String, value: Boolean) =
        c.getSharedPreferences(P, 0).edit().putBoolean(key, value).apply()
}
