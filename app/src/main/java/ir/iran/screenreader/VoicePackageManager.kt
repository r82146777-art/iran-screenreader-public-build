package ir.iran.screenreader
import android.content.Context
object VoicePackageManager {
    fun detectGuya(context: Context): Boolean =
        SpeechManager.engines(context).any {
            it.name.contains("guya", true) || it.label.contains("\u06af\u0648\u06cc\u0627", true)
        }
}
