package ir.iran.screenreader
object MainMenu {
    private val items = listOf(
        "\u062a\u0646\u0638\u06cc\u0645\u0627\u062a" to { s: IranAccessibilityService ->
            val i = android.content.Intent(s, MainActivity::class.java).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            s.startActivity(i); "ok"
        },
        "\u062e\u0648\u0627\u0646\u062f\u0646 \u0635\u0641\u062d\u0647" to { s: IranAccessibilityService ->
            GestureManager.rebuildTree(s); FocusNavigator.first(); FocusNavigator.speakCurrent { s.speak(it, false) }; "ok"
        },
        "\u062a\u0648\u0642\u0641 \u06af\u0641\u062a\u0627\u0631" to { s: IranAccessibilityService -> s.stop(); s.speak(EarconFeedback.STOP); "ok" },
        "\u0631\u0627\u0647\u0646\u0645\u0627" to { s: IranAccessibilityService ->
            s.speak("\u06a9\u0634\u06cc\u062f\u0646 \u0628\u0647 \u0686\u067e: \u0628\u0639\u062f\u06cc. \u0628\u0647 \u0631\u0627\u0633\u062a: \u0642\u0628\u0644\u06cc. \u0628\u0647 \u0628\u0627\u0644\u0627: \u062e\u0648\u0627\u0646\u062f\u0646. \u062f\u0648 \u0627\u0646\u06af\u0634\u062a \u0628\u0647 \u067e\u0627\u06cc\u06cc\u0646: \u0645\u0646\u0648."); "ok"
        },
        "\u0628\u0633\u062a\u0646 \u0645\u0646\u0648" to { s: IranAccessibilityService -> s.speak("\u0645\u0646\u0648 \u0628\u0633\u062a\u0647 \u0634\u062f"); "ok" }
    )
    private var menuIndex = 0
    private var open = false
    fun isOpen(): Boolean = open
    fun open(s: IranAccessibilityService) { open = true; menuIndex = 0; s.speak(EarconFeedback.MENU); speakCurrent(s) }
    fun close(s: IranAccessibilityService) { open = false; s.speak("\u0645\u0646\u0648 \u0628\u0633\u062a\u0647 \u0634\u062f") }
    fun next(s: IranAccessibilityService) { if (!open) return; menuIndex = (menuIndex + 1) % items.size; speakCurrent(s) }
    fun prev(s: IranAccessibilityService) { if (!open) return; menuIndex = if (menuIndex <= 0) items.lastIndex else menuIndex - 1; speakCurrent(s) }
    fun activate(s: IranAccessibilityService) { if (!open) return; val a = items[menuIndex].second; open = false; a(s) }
    private fun speakCurrent(s: IranAccessibilityService) { s.speak("${'$'}{menuIndex + 1} \u0627\u0632 ${'$'}{items.size}. ${'$'}{items[menuIndex].first}") }
    fun handleWhileOpen(s: IranAccessibilityService, gestureId: Int): Boolean {
        if (!open) return false
        return when (gestureId) {
            android.accessibilityservice.AccessibilityService.GESTURE_SWIPE_LEFT -> { next(s); true }
            android.accessibilityservice.AccessibilityService.GESTURE_SWIPE_RIGHT -> { prev(s); true }
            android.accessibilityservice.AccessibilityService.GESTURE_SWIPE_UP -> { activate(s); true }
            android.accessibilityservice.AccessibilityService.GESTURE_SWIPE_DOWN -> { close(s); true }
            android.accessibilityservice.AccessibilityService.GESTURE_2_FINGER_SWIPE_DOWN -> { close(s); true }
            else -> false
        }
    }
}
