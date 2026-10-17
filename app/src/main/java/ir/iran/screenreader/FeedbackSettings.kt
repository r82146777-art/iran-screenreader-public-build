package ir.iran.screenreader

import android.content.Context

object FeedbackSettings {
    private const val P = "iran_feedback_v5"

    fun audioEnabled(c: Context) = c.getSharedPreferences(P, 0).getBoolean("audio_on", true)
    fun setAudioEnabled(c: Context, v: Boolean) = c.getSharedPreferences(P, 0).edit().putBoolean("audio_on", v).apply()

    fun audioVolume(c: Context) = c.getSharedPreferences(P, 0).getFloat("audio_vol", 0.7f)
    fun setAudioVolume(c: Context, v: Float) = c.getSharedPreferences(P, 0).edit().putFloat("audio_vol", v.coerceIn(0f, 1f)).apply()

    fun vibrationEnabled(c: Context) = c.getSharedPreferences(P, 0).getBoolean("vib_on", true)
    fun setVibrationEnabled(c: Context, v: Boolean) = c.getSharedPreferences(P, 0).edit().putBoolean("vib_on", v).apply()

    fun vibrationIntensity(c: Context) = c.getSharedPreferences(P, 0).getFloat("vib_int", 0.5f)
    fun setVibrationIntensity(c: Context, v: Float) = c.getSharedPreferences(P, 0).edit().putFloat("vib_int", v.coerceIn(0f, 1f)).apply()

    fun soundPackageId(c: Context) = c.getSharedPreferences(P, 0).getString("sound_pkg", "default") ?: "default"
    fun setSoundPackageId(c: Context, id: String) = c.getSharedPreferences(P, 0).edit().putString("sound_pkg", id).apply()
}
