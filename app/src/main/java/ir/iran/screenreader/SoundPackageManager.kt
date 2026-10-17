package ir.iran.screenreader

import android.content.Context
import android.media.ToneGenerator
import org.json.JSONObject

object SoundPackageManager {
    private const val P = "iran_sound_pkg_v5"
    data class Package(val id: String, val name: String, val description: String)
    private val builtins = listOf(
        Package("default", "پیش‌فرض ایران", "تون‌های استاندارد"),
        Package("soft", "نرم", "تون‌های ملایم"),
        Package("sharp", "تیز", "تون‌های واضح"),
        Package("classic", "کلاسیک", "الگوی کلاسیک")
    )
    private val defaultMap = mapOf(
        "next" to ToneGenerator.TONE_PROP_BEEP,
        "prev" to ToneGenerator.TONE_PROP_ACK,
        "activate" to ToneGenerator.TONE_PROP_PROMPT,
        "menu" to ToneGenerator.TONE_CDMA_PIP,
        "read" to ToneGenerator.TONE_PROP_BEEP2,
        "stop" to ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,
        "empty" to ToneGenerator.TONE_SUP_ERROR,
        "home" to ToneGenerator.TONE_CDMA_CONFIRM,
        "back" to ToneGenerator.TONE_CDMA_ANSWER,
        "focus" to ToneGenerator.TONE_PROP_BEEP,
        "granularity" to ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE
    )
    private val softMap = defaultMap.mapValues { ToneGenerator.TONE_CDMA_PIP }
    private val sharpMap = defaultMap.mapValues { ToneGenerator.TONE_DTMF_1 }
    fun listPackages(): List<Package> = builtins
    fun toneFor(c: Context, action: String): Int {
        val pkg = FeedbackSettings.soundPackageId(c)
        val custom = loadCustom(c, pkg)
        if (custom.has(action)) return custom.getInt(action)
        val base = when (pkg) { "soft" -> softMap; "sharp" -> sharpMap; else -> defaultMap }
        return base[action] ?: ToneGenerator.TONE_PROP_BEEP
    }
    fun setCustomTone(c: Context, action: String, toneType: Int) {
        val pkg = FeedbackSettings.soundPackageId(c)
        val o = loadCustom(c, pkg)
        o.put(action, toneType)
        c.getSharedPreferences(P, 0).edit().putString("custom_$pkg", o.toString()).apply()
    }
    fun actions(): List<Pair<String, String>> = listOf(
        "next" to "بعدی", "prev" to "قبلی", "activate" to "فعال‌سازی", "menu" to "منوی اصلی",
        "read" to "خواندن", "stop" to "توقف", "empty" to "خالی", "home" to "خانه",
        "back" to "بازگشت", "focus" to "تمرکز", "granularity" to "دانه‌بندی"
    )
    fun availableTones(): List<Pair<Int, String>> = listOf(
        ToneGenerator.TONE_PROP_BEEP to "بوق ۱", ToneGenerator.TONE_PROP_BEEP2 to "بوق ۲",
        ToneGenerator.TONE_PROP_ACK to "تأیید", ToneGenerator.TONE_PROP_NACK to "رد",
        ToneGenerator.TONE_PROP_PROMPT to "اعلان", ToneGenerator.TONE_CDMA_PIP to "پیپ",
        ToneGenerator.TONE_CDMA_CONFIRM to "تأیید قوی", ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE to "خطای نرم",
        ToneGenerator.TONE_SUP_ERROR to "خطا", ToneGenerator.TONE_DTMF_1 to "DTMF ۱", ToneGenerator.TONE_DTMF_5 to "DTMF ۵"
    )
    private fun loadCustom(c: Context, pkg: String): JSONObject {
        val s = c.getSharedPreferences(P, 0).getString("custom_$pkg", "{}") ?: "{}"
        return try { JSONObject(s) } catch (_: Exception) { JSONObject() }
    }
}
