package ir.iran.shahrbanoorebuild

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper

/** Accessible game sound cues and a lightweight optional synthesized melody. */
object GameSound {
 private var tone: ToneGenerator? = null
 private val handler = Handler(Looper.getMainLooper())
 @Volatile private var musicPlayer: MediaPlayer? = null

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
  handler.postDelayed({ try { t.startTone(toneType, duration) } catch (_: Exception) { } }, 95L)
 }

 @Synchronized fun startMusic(context: Context) {\n  if (musicPlayer?.isPlaying == true) return\n  stopMusic()\n  try {\n   musicPlayer = MediaPlayer.create(context.applicationContext, R.raw.background_music)?.apply {\n    isLooping = true\n    setVolume(0.32f, 0.32f)\n    start()\n   }\n  } catch (_: Exception) { musicPlayer = null }\n }\n\n @Synchronized fun stopMusic() {\n  try { musicPlayer?.stop() } catch (_: Exception) { }\n  try { musicPlayer?.release() } catch (_: Exception) { }\n  musicPlayer = null\n }\n\n fun release() {
  stopMusic()
  try { tone?.release() } catch (_: Exception) { }
  tone = null
 }
}
