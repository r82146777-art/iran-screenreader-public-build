package ir.iran.screenreader
object MainMenu {
    private val items = listOf(
        "تنظیمات بازخورد صوتی" to { s: IranAccessibilityService -> s.openFeedbackSettings(); "ok" },
        "موتور و صدای گفتار" to { s: IranAccessibilityService -> s.openTtsSettings(); "ok" },
        "پوسته‌های صوتی" to { s: IranAccessibilityService -> s.openSoundPackages(); "ok" },
        "ویرایش پوسته صوتی فعلی" to { s: IranAccessibilityService -> s.openEditSoundPackage(); "ok" },
        "تنظیمات ژست‌های حرکتی" to { s: IranAccessibilityService -> s.openGestureSettings(); "ok" },
        "خواندن صفحه" to { s: IranAccessibilityService ->
            GestureManager.rebuildTree(s); FocusNavigator.first()
            EarconPlayer.play(s, "read"); FocusNavigator.speakCurrent { s.speak(it, false) }; "ok"
        },
        "توقف گفتار" to { s: IranAccessibilityService -> EarconPlayer.play(s, "stop"); s.stop(); s.speak(EarconFeedback.STOP); "ok" },
        "راهنما" to { s: IranAccessibilityService ->
            s.speak("کشیدن از راست به چپ: بعدی. از چپ به راست: قبلی. دو انگشت به پایین یا بالا سپس پایین: منوی اصلی."); "ok"
        },
        "بستن منو" to { s: IranAccessibilityService -> s.speak("منو بسته شد"); "ok" }
    )
    private var menuIndex = 0
    private var open = false
    fun isOpen() = open
    fun open(s: IranAccessibilityService) {
        open = true; menuIndex = 0
        EarconPlayer.play(s, "menu"); s.speak(EarconFeedback.MENU); speakCurrent(s)
    }
    fun close(s: IranAccessibilityService) { open = false; s.speak("منو بسته شد") }
    fun next(s: IranAccessibilityService) { if (!open) return; menuIndex = (menuIndex + 1) % items.size; EarconPlayer.play(s, "focus"); speakCurrent(s) }
    fun prev(s: IranAccessibilityService) { if (!open) return; menuIndex = if (menuIndex <= 0) items.lastIndex else menuIndex - 1; EarconPlayer.play(s, "focus"); speakCurrent(s) }
    fun activate(s: IranAccessibilityService) { if (!open) return; val a = items[menuIndex].second; open = false; EarconPlayer.play(s, "activate"); a(s) }
    private fun speakCurrent(s: IranAccessibilityService) { s.speak("${menuIndex + 1} از ${items.size}. ${items[menuIndex].first}") }
    fun handleWhileOpen(s: IranAccessibilityService, gestureId: Int): Boolean {
        if (!open) return false
        return when (gestureId) {
            android.accessibilityservice.AccessibilityService.GESTURE_SWIPE_LEFT -> { next(s); true }
            android.accessibilityservice.AccessibilityService.GESTURE_SWIPE_RIGHT -> { prev(s); true }
            android.accessibilityservice.AccessibilityService.GESTURE_SWIPE_UP -> { activate(s); true }
            android.accessibilityservice.AccessibilityService.GESTURE_SWIPE_DOWN,
            android.accessibilityservice.AccessibilityService.GESTURE_2_FINGER_SWIPE_DOWN -> { close(s); true }
            else -> false
        }
    }
}
