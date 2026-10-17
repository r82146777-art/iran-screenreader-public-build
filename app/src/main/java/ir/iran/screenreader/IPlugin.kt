package ir.iran.screenreader
import android.content.Context
interface IPlugin {
    val id: String; val name: String; val description: String
    val version: String get() = "1.0"; val author: String get() = "IranScreenReader"
    fun onLoad(context: Context, host: PluginHost) {}
    fun onUnload(context: Context) {}
    fun tools(): List<PluginTool> = emptyList()
    fun voiceCommands(): Map<String, (Context, PluginHost) -> String> = emptyMap()
    fun beforeSpeak(text: String): String = text
    fun onAccessibilityEvent(eventType: Int, packageName: String, label: String) {}
}
data class PluginTool(val id: String, val title: String, val description: String, val action: (Context, PluginHost) -> String)
interface PluginHost {
    fun speak(text: String, flush: Boolean = true)
    fun stopSpeech()
    fun getService(): IranAccessibilityService?
    fun getContext(): Context
    fun registerTool(tool: PluginTool)
}
