package ir.iran.screenreader
import android.accessibilityservice.AccessibilityService
object GestureManager {
    fun handle(s: AccessibilityService, id: Int): Boolean {
        if (!FeatureFlags.get(s, "gesture_mode")) return false
        val svc = s as? IranAccessibilityService ?: IranAccessibilityService.instance ?: return false
        if (MainMenu.isOpen() && MainMenu.handleWhileOpen(svc, id)) return true
        val key = GestureSettings.keyForGestureId(id) ?: return false
        return perform(svc, GestureSettings.getAction(svc, key))
    }
    fun perform(svc: IranAccessibilityService, action: GestureSettings.Action): Boolean {
        when (action) {
            GestureSettings.Action.NEXT -> {
                ensureTree(svc); FocusNavigator.next()
                EarconPlayer.play(svc, "next"); svc.speak(EarconFeedback.NEXT)
                FocusNavigator.speakCurrent { svc.speak(it, false) }
            }
            GestureSettings.Action.PREV -> {
                ensureTree(svc); FocusNavigator.prev()
                EarconPlayer.play(svc, "prev"); svc.speak(EarconFeedback.PREV)
                FocusNavigator.speakCurrent { svc.speak(it, false) }
            }
            GestureSettings.Action.READ -> {
                ensureTree(svc); if (FocusNavigator.current() == null) FocusNavigator.first()
                EarconPlayer.play(svc, "read"); svc.speak(EarconFeedback.READ_START)
                FocusNavigator.speakCurrent { svc.speak(it, false) }
            }
            GestureSettings.Action.ACTIVATE -> {
                ensureTree(svc)
                val ok = FocusNavigator.activate()
                EarconPlayer.play(svc, if (ok) "activate" else "empty")
                svc.speak(if (ok) EarconFeedback.ACTIVATE else EarconFeedback.EMPTY)
            }
            GestureSettings.Action.MENU -> MainMenu.open(svc)
            GestureSettings.Action.STOP -> { EarconPlayer.play(svc, "stop"); svc.stop(); svc.speak(EarconFeedback.STOP) }
            GestureSettings.Action.HOME -> { EarconPlayer.play(svc, "home"); svc.speak(EarconFeedback.HOME); svc.goHome() }
            GestureSettings.Action.BACK -> { EarconPlayer.play(svc, "back"); svc.speak(EarconFeedback.BACK); svc.goBack() }
            GestureSettings.Action.NOTIFICATIONS -> { EarconPlayer.play(svc, "focus"); svc.speak(EarconFeedback.NOTIFICATIONS); svc.openNotifications() }
            GestureSettings.Action.RECENTS -> { EarconPlayer.play(svc, "focus"); svc.speak(EarconFeedback.RECENTS); svc.openRecents() }
            GestureSettings.Action.GRANULARITY -> {
                svc.stop()
                val g = FocusNavigator.cycleGranularity()
                val name = when (g) {
                    FocusNavigator.Granularity.CHARACTER -> EarconFeedback.GRANULARITY_CHAR
                    FocusNavigator.Granularity.WORD -> EarconFeedback.GRANULARITY_WORD
                    FocusNavigator.Granularity.LINE -> EarconFeedback.GRANULARITY_LINE
                    FocusNavigator.Granularity.PARAGRAPH -> EarconFeedback.GRANULARITY_PARA
                    FocusNavigator.Granularity.CONTROL -> EarconFeedback.GRANULARITY_CONTROL
                }
                EarconPlayer.play(svc, "granularity"); svc.speak("دانه‌بندی $name")
            }
            GestureSettings.Action.NONE -> return false
        }
        return true
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
