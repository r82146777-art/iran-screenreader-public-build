package ir.iran.screenreader
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        IranAccessibilityService.instance?.speak(intent.getStringExtra("msg") ?: "یادآور")
    }
}
