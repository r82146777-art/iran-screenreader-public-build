package ir.iran.screenreader

import android.content.Context

class SmartSilence(private val c: Context) {
    private var last = ""
    private var at = 0L
    fun shouldSpeak(text: String): Boolean {
        if (!FeatureFlags.get(c, "smart_silence")) return true
        val now = System.currentTimeMillis()
        if (text == last && now - at < 1200) return false
        last = text; at = now; return true
    }
}
