package ir.iran.screenreader

import android.content.Context

object SettingsStore {
    private const val P = "iran_settings_v3"
    fun speakEvents(c: Context) = c.getSharedPreferences(P, 0).getBoolean("speak_events", true)
    fun setSpeakEvents(c: Context, v: Boolean) = c.getSharedPreferences(P, 0).edit().putBoolean("speak_events", v).apply()
    fun selectedEngine(c: Context) = c.getSharedPreferences(P, 0).getString("engine", null)
    fun setEngine(c: Context, id: String?) = c.getSharedPreferences(P, 0).edit().putString("engine", id).apply()
    fun selectedVoice(c: Context) = c.getSharedPreferences(P, 0).getString("voice", null)
    fun setVoice(c: Context, name: String) = c.getSharedPreferences(P, 0).edit().putString("voice", name).apply()
    fun profile(c: Context) = c.getSharedPreferences(P, 0).getString("profile", "GENERAL") ?: "GENERAL"
    fun setProfile(c: Context, p: String) = c.getSharedPreferences(P, 0).edit().putString("profile", p).apply()
}
