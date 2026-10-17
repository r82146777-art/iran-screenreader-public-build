package ir.iran.screenreader
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
class NotificationReaderService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null || !FeatureFlags.get(this, "speak_notifications")) return
        val n = sbn.notification ?: return
        val title = n.extras?.getCharSequence("android.title")?.toString().orEmpty()
        val text = n.extras?.getCharSequence("android.text")?.toString().orEmpty()
        val msg = listOf(title, text).filter { it.isNotBlank() }.joinToString(". ")
        if (msg.isNotBlank()) IranAccessibilityService.instance?.speak("\u0627\u0639\u0644\u0627\u0646. $msg")
    }
}
