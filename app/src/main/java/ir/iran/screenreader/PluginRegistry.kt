package ir.iran.screenreader
import android.content.Context
object PluginRegistry {
    data class PluginInfo(val id: String, val name: String, val description: String, val version: String, val author: String, val enabled: Boolean, val isBuiltin: Boolean)
    private val loaded = mutableMapOf<String, IPlugin>()
    fun init(context: Context, host: PluginHost) {}
    fun allInfo(context: Context): List<PluginInfo> = emptyList()
    fun setEnabled(context: Context, id: String, enabled: Boolean, host: PluginHost? = null) {}
    fun tools(): List<PluginTool> = emptyList()
    fun processBeforeSpeak(text: String): String = text
    fun notifyEvent(eventType: Int, packageName: String, label: String) {}
    fun findVoiceCommand(cmd: String): ((Context, PluginHost) -> String)? = null
}
