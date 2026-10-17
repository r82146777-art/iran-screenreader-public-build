package ir.iran.screenreader
import android.content.Context
object SettingsStore {
    private const val P = "iran_settings_v5"
    fun selectedEngine(c: Context) = c.getSharedPreferences(P, 0).getString("engine", null)
    fun setEngine(c: Context, id: String?) = c.getSharedPreferences(P, 0).edit().putString("engine", id).apply()
    fun selectedVoice(c: Context) = c.getSharedPreferences(P, 0).getString("voice", null)
    fun setVoice(c: Context, name: String) = c.getSharedPreferences(P, 0).edit().putString("voice", name).apply()
    fun speechRate(c: Context) = c.getSharedPreferences(P, 0).getFloat("rate", 1.0f)
    fun setSpeechRate(c: Context, r: Float) = c.getSharedPreferences(P, 0).edit().putFloat("rate", r).apply()
    fun pitch(c: Context) = c.getSharedPreferences(P, 0).getFloat("pitch", 1.0f)
    fun setPitch(c: Context, p: Float) = c.getSharedPreferences(P, 0).edit().putFloat("pitch", p).apply()
}
