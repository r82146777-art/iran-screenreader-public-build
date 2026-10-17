package ir.iran.screenreader

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent

class IranAccessibilityService : AccessibilityService() {
    companion object { var instance: IranAccessibilityService? = null }

    private lateinit var speech: SpeechManager
    private lateinit var silence: SmartSilence
    private var lastSpoken = ""
    private var lastAt = 0L
    private var lastPackage = ""

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        speech = SpeechManager(this)
        silence = SmartSilence(this)
        EarconPlayer.refreshVolume(this)

        // Enable touch exploration so GESTURE_SWIPE_* are delivered
        val info = serviceInfo
        info.flags = info.flags or
            AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE or
            AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
            AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
            AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
        serviceInfo = info

        android.os.Handler(mainLooper).postDelayed({
            EarconPlayer.play(this, "menu")
            speech.speak("صفحه‌خوان ایران نسخه پنج آماده است")
        }, 800)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!FeatureFlags.get(this, "speak_events")) return
        val e = event ?: return
        val pkg = e.packageName?.toString().orEmpty()
        if (pkg == packageName) return
        if (pkg != lastPackage && pkg.isNotBlank()) {
            lastPackage = pkg
            GestureManager.rebuildTree(this)
        }
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
                    lastSpoken = cleaned; lastAt = now
                    EarconPlayer.play(this, "focus")
                    speech.speak(cleaned)
                }
            }
        }
        source.recycle()
    }

    override fun onGesture(gestureId: Int): Boolean = GestureManager.handle(this, gestureId)

    fun speak(t: String, flush: Boolean = true) { if (::speech.isInitialized) speech.speak(t, flush) }
    fun stop() { if (::speech.isInitialized) speech.stop() }
    fun goBack() { performGlobalAction(GLOBAL_ACTION_BACK) }
    fun goHome() { performGlobalAction(GLOBAL_ACTION_HOME) }
    fun openRecents() { performGlobalAction(GLOBAL_ACTION_RECENTS) }
    fun openNotifications() { performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS) }

    fun openFeedbackSettings() = startActivity(Intent(this, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("open", "feedback"))
    fun openTtsSettings() = startActivity(Intent(this, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("open", "tts"))
    fun openSoundPackages() = startActivity(Intent(this, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("open", "sounds"))
    fun openEditSoundPackage() = startActivity(Intent(this, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("open", "edit_sound"))
    fun openGestureSettings() = startActivity(Intent(this, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("open", "gestures"))

    override fun onInterrupt() { stop() }
    override fun onDestroy() {
        instance = null
        if (::speech.isInitialized) speech.shutdown()
        EarconPlayer.release()
        super.onDestroy()
    }
}
