package ir.iran.shahrbanoorebuild

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator

/** Accessible game sound cues and a lightweight optional synthesized melody. */
object GameSound {
 private var tone: ToneGenerator? = null
 @Volatile private var musicRunning = false
 @Volatile private var musicThread: Thread? = null

 fun play(context: Context, kind: String) {
  try {
   if (tone == null) tone = ToneGenerator(AudioManager.STREAM_MUSIC, 75)
   val t = tone ?: return
   when (kind) {
    "tap" -> t.startTone(ToneGenerator.TONE_PROP_BEEP, 45)
    "focus" -> t.startTone(ToneGenerator.TONE_PROP_ACK, 75)
    "menu" -> { t.startTone(ToneGenerator.TONE_PROP_PROMPT, 90) }
    "start" -> { t.startTone(ToneGenerator.TONE_DTMF_1, 75); later(t, ToneGenerator.TONE_DTMF_3, 100) }
    "stage" -> { t.startTone(ToneGenerator.TONE_PROP_BEEP, 65); later(t, ToneGenerator.TONE_PROP_BEEP2, 100) }
    "hint" -> { t.startTone(ToneGenerator.TONE_PROP_ACK, 80); later(t, ToneGenerator.TONE_PROP_BEEP, 70) }
    "correct" -> { t.startTone(ToneGenerator.TONE_PROP_ACK, 90); later(t, ToneGenerator.TONE_PROP_BEEP2, 140) }
    "wrong" -> t.startTone(ToneGenerator.TONE_SUP_ERROR, 170)
    "win" -> { t.startTone(ToneGenerator.TONE_DTMF_1, 100); later(t, ToneGenerator.TONE_DTMF_3, 100); later(t, ToneGenerator.TONE_DTMF_5, 180) }
   }
  } catch (_: Exception) { }
 }
 private fun later(t: ToneGenerator, toneType: Int, duration: Int) {
  try { Thread.sleep(95); t.startTone(toneType, duration) } catch (_: Exception) { }
 }

 @Synchronized fun startMusic() {
  if (musicRunning) return
  musicRunning = true
  musicThread = Thread {
   val notes = intArrayOf(ToneGenerator.TONE_DTMF_1, ToneGenerator.TONE_DTMF_3, ToneGenerator.TONE_DTMF_5, ToneGenerator.TONE_DTMF_3,
    ToneGenerator.TONE_DTMF_2, ToneGenerator.TONE_DTMF_4, ToneGenerator.TONE_DTMF_6, ToneGenerator.TONE_DTMF_4)
   try {
    var i = 0
    while (musicRunning) {
     val t = tone ?: break
     t.startTone(notes[i++ % notes.size], 170)
     Thread.sleep(260)
    }
   } catch (_: InterruptedException) { }
   catch (_: Exception) { }
  }.apply { name = "ShahrbanooMelody"; isDaemon = true; start() }
 }

 @Synchronized fun stopMusic() {
  musicRunning = false
  musicThread?.interrupt()
  musicThread = null
  try { tone?.stopTone() } catch (_: Exception) { }
 }

 fun release() {
  stopMusic()
  try { tone?.release() } catch (_: Exception) { }
  tone = null
 }
}
