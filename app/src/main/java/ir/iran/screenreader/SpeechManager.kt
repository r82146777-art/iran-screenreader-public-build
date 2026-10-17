package ir.iran.screenreader
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import java.util.Locale
class SpeechManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var ready = false
    init { create(SettingsStore.selectedEngine(context)) }
    private fun create(engine: String?) {
        tts?.shutdown()
        tts = if (engine.isNullOrBlank()) TextToSpeech(context, this) else TextToSpeech(context, this, engine)
    }
    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
        if (ready) {
            setLanguage(LanguageRouter.localeFor("\u0633\u0644\u0627\u0645"))
            SettingsStore.selectedVoice(context)?.let { name ->
                tts?.voices?.firstOrNull { it.name == name }?.let { tts?.voice = it }
            }
        }
    }
    fun setEngine(id: String?) { SettingsStore.setEngine(context, id); create(id) }
    fun setLanguage(locale: Locale) { if (ready) tts?.language = locale }
    fun setVoice(voice: Voice) { if (ready) { tts?.voice = voice; SettingsStore.setVoice(context, voice.name) } }
    fun speak(raw: String, flush: Boolean = true) {
        if (!ready || raw.isBlank()) return
        var text = PronunciationStore.apply(context, raw.take(5000))
        text = PluginRegistry.processBeforeSpeak(text)
        HistoryStore.add(context, text)
        setLanguage(LanguageRouter.localeFor(text))
        tts?.speak(text, if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null, "iran-${'$'}{System.nanoTime()}")
    }
    fun stop() { tts?.stop() }
    fun shutdown() { tts?.shutdown() }
    fun voices(): List<Voice> = tts?.voices?.toList().orEmpty()
    companion object {
        fun engines(c: Context): List<TextToSpeech.EngineInfo> {
            val x = TextToSpeech(c, null); val r = x.engines; x.shutdown(); return r
        }
    }
}
