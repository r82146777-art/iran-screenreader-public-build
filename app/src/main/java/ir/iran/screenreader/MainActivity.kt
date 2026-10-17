package ir.iran.screenreader
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.*
class MainActivity : Activity() {
    private lateinit var speech: SpeechManager
    override fun onCreate(b: Bundle?) {
        super.onCreate(b); speech = SpeechManager(this); build()
    }
    private fun build() {
        val scroll = ScrollView(this)
        val l = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(28, 28, 28, 28); gravity = Gravity.CENTER_HORIZONTAL
        }
        scroll.addView(l); setContentView(scroll)
        l.addView(TextView(this).apply {
            text = "\u0635\u0641\u062d\u0647\u200c\u062e\u0648\u0627\u0646 \u0627\u06cc\u0631\u0627\u0646\n\u0646\u0633\u062e\u0647 \u06f4"; textSize = 22f; gravity = Gravity.CENTER
        })
        btn(l, "\u0641\u0639\u0627\u0644\u200c\u0633\u0627\u0632\u06cc \u0635\u0641\u062d\u0647\u200c\u062e\u0648\u0627\u0646") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        btn(l, "\u062a\u0633\u062a \u06af\u0641\u062a\u0627\u0631") { speech.speak("\u0633\u0644\u0627\u0645. \u0635\u0641\u062d\u0647\u200c\u062e\u0648\u0627\u0646 \u0627\u06cc\u0631\u0627\u0646 \u0622\u0645\u0627\u062f\u0647 \u0627\u0633\u062a.") }
        btn(l, "\u0645\u0648\u062a\u0648\u0631\u0647\u0627\u06cc TTS") {
            val engines = SpeechManager.engines(this)
            val names = engines.map { it.label.ifBlank { it.name } }.toTypedArray()
            android.app.AlertDialog.Builder(this).setTitle("TTS").setItems(names) { _, i ->
                speech.setEngine(engines[i].name); speech.speak(engines[i].label)
            }.show()
        }
    }
    private fun btn(parent: LinearLayout, title: String, action: () -> Unit) {
        parent.addView(Button(this).apply { text = title; setOnClickListener { action() } })
    }
    override fun onDestroy() { speech.shutdown(); super.onDestroy() }
}
