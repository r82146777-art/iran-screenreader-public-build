package ir.iran.screenreader

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import java.util.Locale

class SpeechManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var ready = false
    private val main = Handler(Looper.getMainLooper())
    private val pending = mutableListOf<Pair<String, Boolean>>()

    init { main.post { create(SettingsStore.selectedEngine(context) ?: preferPersianEngine(context)) } }

    private fun preferPersianEngine(c: Context): String? {
        val engines = engines(c)
        engines.firstOrNull {
            val n = (it.name + " " + it.label).lowercase()
            n.contains("guya") || n.contains("گویا") || n.contains("ir.guya") || n.contains("com.guya")
        }?.let { return it.name }
        return engines.firstOrNull {
            val n = (it.name + " " + it.label).lowercase()
            n.contains("persian") || n.contains("farsi") || n.contains("فارسی") || n.contains("iran")
        }?.name
    }

    private fun create(engine: String?) {
        try { tts?.shutdown() } catch (_: Exception) {}
        ready = false
        tts = try {
            if (engine.isNullOrBlank()) TextToSpeech(context, this) else TextToSpeech(context, this, engine)
        } catch (_: Exception) { TextToSpeech(context, this) }
    }

    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
        if (ready) {
            val r = tts?.setLanguage(Locale("fa", "IR"))
            if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED) tts?.language = Locale.getDefault()
            tts?.setSpeechRate(SettingsStore.speechRate(context))
            tts?.setPitch(SettingsStore.pitch(context))
            SettingsStore.selectedVoice(context)?.let { name ->
                tts?.voices?.firstOrNull { it.name == name }?.let { tts?.voice = it }
            } ?: tts?.voices?.firstOrNull {
                it.locale.language == "fa" || it.name.contains("fa", true) || it.name.contains("persian", true)
            }?.let { tts?.voice = it }
            val copy = pending.toList(); pending.clear()
            for ((t, f) in copy) speak(t, f)
        }
    }

    fun setEngine(id: String?) { SettingsStore.setEngine(context, id); create(id) }
    fun setVoice(voice: Voice) { if (ready) { tts?.voice = voice; SettingsStore.setVoice(context, voice.name) } }
    fun setRate(r: Float) { SettingsStore.setSpeechRate(context, r); if (ready) tts?.setSpeechRate(r) }

    fun speak(raw: String, flush: Boolean = true) {
        if (raw.isBlank()) return
        if (!ready) {
            pending.add(raw to flush)
            if (tts == null) create(SettingsStore.selectedEngine(context))
            return
        }
        val text = PronunciationStore.apply(context, raw.take(4000))
        HistoryStore.add(context, text)
        tts?.language = LanguageRouter.localeFor(text)
        tts?.speak(text, if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null, "iran-${System.nanoTime()}")
    }

    fun stop() { tts?.stop() }
    fun shutdown() { try { tts?.shutdown() } catch (_: Exception) {}; tts = null; ready = false }
    fun voices(): List<Voice> = tts?.voices?.toList().orEmpty()
    fun isReady() = ready

    companion object {
        fun engines(c: Context): List<TextToSpeech.EngineInfo> {
            return try {
                val x = TextToSpeech(c, null)
                val r = x.engines?.toList().orEmpty()
                x.shutdown(); r
            } catch (_: Exception) { emptyList() }
        }
    }
}
