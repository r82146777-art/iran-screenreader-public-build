package ir.iran.screenreader
import android.accessibilityservice.AccessibilityService
object GestureManager {
    fun handle(s: AccessibilityService, id: Int): Boolean {
        if (!FeatureFlags.get(s, "gesture_mode")) return false
        val svc = s as? IranAccessibilityService ?: IranAccessibilityService.instance ?: return false
        if (MainMenu.isOpen() && MainMenu.handleWhileOpen(svc, id)) return true
        return when (id) {
            AccessibilityService.GESTURE_SWIPE_LEFT -> {
                ensureTree(svc); FocusNavigator.next(); svc.speak(EarconFeedback.NEXT)
                FocusNavigator.speakCurrent { svc.speak(it, false) }; true
            }
            AccessibilityService.GESTURE_SWIPE_RIGHT -> {
                ensureTree(svc); FocusNavigator.prev(); svc.speak(EarconFeedback.PREV)
                FocusNavigator.speakCurrent { svc.speak(it, false) }; true
            }
            AccessibilityService.GESTURE_SWIPE_UP -> {
                ensureTree(svc); if (FocusNavigator.current() == null) FocusNavigator.first()
                svc.speak(EarconFeedback.READ_START); FocusNavigator.speakCurrent { svc.speak(it, false) }; true
            }
            AccessibilityService.GESTURE_SWIPE_DOWN -> {
                svc.stop(); val g = FocusNavigator.cycleGranularity()
                val name = when (g) {
                    FocusNavigator.Granularity.CHARACTER -> EarconFeedback.GRANULARITY_CHAR
                    FocusNavigator.Granularity.WORD -> EarconFeedback.GRANULARITY_WORD
                    FocusNavigator.Granularity.LINE -> EarconFeedback.GRANULARITY_LINE
                    FocusNavigator.Granularity.PARAGRAPH -> EarconFeedback.GRANULARITY_PARA
                    FocusNavigator.Granularity.CONTROL -> EarconFeedback.GRANULARITY_CONTROL
                }
                svc.speak("\u062f\u0627\u0646\u0647\u200c\u0628\u0646\u062f\u06cc $name"); true
            }
            AccessibilityService.GESTURE_2_FINGER_SWIPE_DOWN -> { MainMenu.open(svc); true }
            AccessibilityService.GESTURE_2_FINGER_SWIPE_UP -> {
                svc.speak("\u0635\u0641\u062d\u0647\u200c\u062e\u0648\u0627\u0646 \u0627\u06cc\u0631\u0627\u0646 \u0641\u0639\u0627\u0644. \u0639\u0646\u0627\u0635\u0631 ${'$'}{FocusNavigator.size()}"); true
            }
            AccessibilityService.GESTURE_3_FINGER_SWIPE_DOWN -> { svc.speak(EarconFeedback.NOTIFICATIONS); svc.openNotifications(); true }
            AccessibilityService.GESTURE_3_FINGER_SWIPE_UP -> { svc.speak(EarconFeedback.HOME); svc.goHome(); true }
            AccessibilityService.GESTURE_2_FINGER_SWIPE_LEFT -> { svc.speak(EarconFeedback.BACK); svc.goBack(); true }
            AccessibilityService.GESTURE_2_FINGER_SWIPE_RIGHT -> { svc.speak(EarconFeedback.RECENTS); svc.openRecents(); true }
            else -> false
        }
    }
    fun ensureTree(svc: IranAccessibilityService) {
        if (FocusNavigator.size() == 0) {
            val root = svc.rootInActiveWindow; FocusNavigator.rebuild(root); root?.recycle()
        }
    }
    fun rebuildTree(svc: IranAccessibilityService) {
        val root = svc.rootInActiveWindow; FocusNavigator.rebuild(root); root?.recycle()
    }
}
