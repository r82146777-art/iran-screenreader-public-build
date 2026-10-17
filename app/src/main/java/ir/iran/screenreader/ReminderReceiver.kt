package ir.iran.screenreader
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        IranAccessibilityService.instance?.speak(intent.getStringExtra("msg") ?: "\u06cc\u0627\u062f\u0622\u0648\u0631")
    }
}
