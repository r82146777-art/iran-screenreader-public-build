package ir.iran.shahrbanoorebuild

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator

object GameSound {
 private var tone: ToneGenerator? = null
 fun play(context: Context, kind: String) {
  try {
   if (tone == null) tone = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
   val t = tone ?: return
   when (kind) {
    "tap" -> t.startTone(ToneGenerator.TONE_PROP_BEEP, 45)
    "focus" -> t.startTone(ToneGenerator.TONE_PROP_ACK, 75)
    "menu" -> t.startTone(ToneGenerator.TONE_PROP_PROMPT, 95)
    "start" -> t.startTone(ToneGenerator.TONE_DTMF_1, 75)
    "stage" -> t.startTone(ToneGenerator.TONE_PROP_BEEP2, 90)
    "hint" -> t.startTone(ToneGenerator.TONE_PROP_ACK, 100)
    "correct" -> t.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 180)
    "wrong" -> t.startTone(ToneGenerator.TONE_SUP_ERROR, 170)
    "win" -> t.startTone(ToneGenerator.TONE_PROP_ACK, 130)
   }
  } catch (_: Exception) { }
 }
 fun release() { try { tone?.release() } catch (_: Exception) { }; tone = null }
}
