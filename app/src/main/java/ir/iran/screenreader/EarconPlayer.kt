package ir.iran.screenreader

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object EarconPlayer {
    private var tone: ToneGenerator? = null

    private fun tg(c: Context): ToneGenerator? {
        if (tone == null) {
            try {
                val vol = (FeedbackSettings.audioVolume(c) * 100).toInt().coerceIn(0, 100)
                tone = ToneGenerator(AudioManager.STREAM_MUSIC, vol)
            } catch (_: Exception) { }
        }
        return tone
    }

    fun refreshVolume(c: Context) {
        try { tone?.release() } catch (_: Exception) {}
        tone = null
        tg(c)
    }

    fun play(c: Context, action: String) {
        if (FeedbackSettings.audioEnabled(c)) {
            val toneType = SoundPackageManager.toneFor(c, action)
            try { tg(c)?.startTone(toneType, 80) } catch (_: Exception) { }
        }
        if (FeedbackSettings.vibrationEnabled(c)) vibrate(c)
    }

    fun vibrate(c: Context) {
        val intensity = FeedbackSettings.vibrationIntensity(c)
        val ms = (30 + intensity * 70).toLong()
        val amp = (50 + intensity * 200).toInt().coerceIn(1, 255)
        try {
            val v = if (Build.VERSION.SDK_INT >= 31) {
                (c.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                c.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= 26) {
                v.vibrate(VibrationEffect.createOneShot(ms, amp))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(ms)
            }
        } catch (_: Exception) { }
    }

    fun release() {
        try { tone?.release() } catch (_: Exception) {}
        tone = null
    }
}
