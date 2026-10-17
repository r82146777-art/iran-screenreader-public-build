package ir.iran.screenreader
import android.app.Service
import android.content.Intent
import android.os.IBinder
class VoiceCommandService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}
