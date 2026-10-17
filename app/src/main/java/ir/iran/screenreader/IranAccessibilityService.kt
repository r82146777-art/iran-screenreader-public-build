package ir.iran.screenreader
import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
class IranAccessibilityService : AccessibilityService(), PluginHost {
    companion object { var instance: IranAccessibilityService? = null }
    private lateinit var speech: SpeechManager
    private lateinit var silence: SmartSilence
    private var lastSpoken = ""; private var lastAt = 0L; private var lastPackage = ""
    override fun onServiceConnected() {
        super.onServiceConnected(); instance = this
        speech = SpeechManager(this); silence = SmartSilence(this)
        PluginRegistry.init(this, this)
        speech.speak("\u0635\u0641\u062d\u0647\u200c\u062e\u0648\u0627\u0646 \u0627\u06cc\u0631\u0627\u0646 \u0646\u0633\u062e\u0647 \u0686\u0647\u0627\u0631 \u0622\u0645\u0627\u062f\u0647 \u0627\u0633\u062a")
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!FeatureFlags.get(this, "speak_events")) return
        val e = event ?: return
        val pkg = e.packageName?.toString().orEmpty()
        if (pkg == packageName) return
        if (pkg != lastPackage && pkg.isNotBlank()) { lastPackage = pkg; GestureManager.rebuildTree(this) }
        val source = e.source ?: return
        val type = e.eventType
        if (type == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED ||
            type == AccessibilityEvent.TYPE_VIEW_FOCUSED ||
            type == AccessibilityEvent.TYPE_VIEW_CLICKED ||
            type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val focused = ReadingController.focusedNode(source)
            val label = if (focused != null) ReadingController.nodeLabel(focused) else ReadingController.nodeLabel(source)
            focused?.recycle()
            val cleaned = label.trim().replace(Regex("\\s+"), " ")
            if (cleaned.isNotBlank() && silence.shouldSpeak(cleaned)) {
                val now = SystemClock.uptimeMillis()
                if (cleaned != lastSpoken || now - lastAt > 900) {
                    lastSpoken = cleaned; lastAt = now; speech.speak(cleaned)
                }
            }
        }
        source.recycle()
    }
    override fun onGesture(gestureId: Int): Boolean = GestureManager.handle(this, gestureId)
    override fun speak(text: String, flush: Boolean) { if (::speech.isInitialized) speech.speak(text, flush) }
    override fun stopSpeech() { if (::speech.isInitialized) speech.stop() }
    override fun getService(): IranAccessibilityService? = this
    override fun getContext(): Context = this
    override fun registerTool(tool: PluginTool) {}
    fun speak(t: String) = speak(t, true)
    fun stop() = stopSpeech()
    fun goBack() { performGlobalAction(GLOBAL_ACTION_BACK) }
    fun goHome() { performGlobalAction(GLOBAL_ACTION_HOME) }
    fun openRecents() { performGlobalAction(GLOBAL_ACTION_RECENTS) }
    fun openNotifications() { performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS) }
    override fun onInterrupt() { stop() }
    override fun onDestroy() { instance = null; if (::speech.isInitialized) speech.shutdown(); super.onDestroy() }
}
