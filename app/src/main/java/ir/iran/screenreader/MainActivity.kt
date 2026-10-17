package ir.iran.screenreader

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {
    private lateinit var speech: SpeechManager

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        speech = SpeechManager(this)
        when (intent?.getStringExtra("open")) {
            "feedback" -> { build(); showFeedbackSettings(); return }
            "tts" -> { build(); showTts(); return }
            "sounds" -> { build(); showSoundPackages(); return }
            "edit_sound" -> { build(); showEditSound(); return }
            "gestures" -> { build(); showGestureSettings(); return }
        }
        build()
    }

    private fun build() {
        val scroll = ScrollView(this)
        val l = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 28, 28, 28)
            gravity = Gravity.CENTER_HORIZONTAL
        }
        scroll.addView(l)
        setContentView(scroll)

        l.addView(TextView(this).apply {
            text = "صفحه‌خوان ایران\nنسخه ۵ — بازخورد صوتی و لرزش"
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 16)
        })

        btn(l, "فعال‌سازی صفحه‌خوان") {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        btn(l, "تست گفتار + افکت صوتی") {
            speech.speak("سلام. صفحه‌خوان ایران نسخه پنج. بازخورد صوتی و لرزش فعال است.")
            EarconPlayer.play(this, "read")
        }
        btn(l, "تنظیمات بازخورد صوتی") { showFeedbackSettings() }
        btn(l, "موتور و صدای گفتار (TTS)") { showTts() }
        btn(l, "پوسته‌های صوتی") { showSoundPackages() }
        btn(l, "ویرایش پوسته صوتی فعلی") { showEditSound() }
        btn(l, "تنظیمات ژست‌های حرکتی") { showGestureSettings() }
        btn(l, "راهنمای ژست‌ها") {
            speech.speak(
                "از راست به چپ: بعدی. از چپ به راست: قبلی. کشیدن به بالا: خواندن. " +
                "دو انگشت به پایین یا بالا سپس پایین: منوی اصلی."
            )
        }
    }

    private fun btn(parent: LinearLayout, title: String, action: () -> Unit) {
        parent.addView(Button(this).apply {
            text = title
            setOnClickListener { action() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 6, 0, 6) }
        })
    }

    private fun showFeedbackSettings() {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val audioCheck = CheckBox(this).apply {
            text = "بازخورد صوتی (افکت‌ها و بوق‌ها)"
            isChecked = FeedbackSettings.audioEnabled(this@MainActivity)
            setOnCheckedChangeListener { _, on ->
                FeedbackSettings.setAudioEnabled(this@MainActivity, on)
                if (on) EarconPlayer.play(this@MainActivity, "focus")
                speech.speak(if (on) "بازخورد صوتی روشن" else "بازخورد صوتی خاموش")
            }
        }
        box.addView(audioCheck)

        box.addView(TextView(this).apply {
            text = "میزان صدای بازخورد صوتی (مستقل از گوینده)"
            setPadding(0, 16, 0, 4)
        })
        val audioSeek = SeekBar(this).apply {
            max = 100
            progress = (FeedbackSettings.audioVolume(this@MainActivity) * 100).toInt()
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) {
                    FeedbackSettings.setAudioVolume(this@MainActivity, p / 100f)
                    EarconPlayer.refreshVolume(this@MainActivity)
                }
                override fun onStartTrackingTouch(s: SeekBar?) {}
                override fun onStopTrackingTouch(s: SeekBar?) {
                    EarconPlayer.play(this@MainActivity, "focus")
                    speech.speak("صدای بازخورد ${(s?.progress ?: 0)}")
                }
            })
        }
        box.addView(audioSeek)

        val vibCheck = CheckBox(this).apply {
            text = "لرزش هنگام کار با صفحه‌خوان"
            isChecked = FeedbackSettings.vibrationEnabled(this@MainActivity)
            setOnCheckedChangeListener { _, on ->
                FeedbackSettings.setVibrationEnabled(this@MainActivity, on)
                if (on) EarconPlayer.vibrate(this@MainActivity)
                speech.speak(if (on) "لرزش روشن" else "لرزش خاموش")
            }
        }
        box.addView(vibCheck)

        box.addView(TextView(this).apply {
            text = "شدت لرزش"
            setPadding(0, 16, 0, 4)
        })
        val vibSeek = SeekBar(this).apply {
            max = 100
            progress = (FeedbackSettings.vibrationIntensity(this@MainActivity) * 100).toInt()
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) {
                    FeedbackSettings.setVibrationIntensity(this@MainActivity, p / 100f)
                }
                override fun onStartTrackingTouch(s: SeekBar?) {}
                override fun onStopTrackingTouch(s: SeekBar?) {
                    EarconPlayer.vibrate(this@MainActivity)
                    speech.speak("شدت لرزش ${(s?.progress ?: 0)}")
                }
            })
        }
        box.addView(vibSeek)

        box.addView(Button(this).apply {
            text = "انتخاب پوسته صوتی"
            setOnClickListener { showSoundPackages() }
        })
        box.addView(Button(this).apply {
            text = "ویرایش پوسته صوتی فعلی"
            setOnClickListener { showEditSound() }
        })

        AlertDialog.Builder(this)
            .setTitle("تنظیمات بازخورد صوتی")
            .setView(box)
            .setPositiveButton("بستن", null)
            .show()
    }

    private fun showTts() {
        val engines = SpeechManager.engines(this)
        if (engines.isEmpty()) {
            speech.speak("هیچ موتور متن به گفتاری پیدا نشد. موتور پیش‌فرض سیستم استفاده می‌شود.")
            AlertDialog.Builder(this)
                .setTitle("موتور TTS")
                .setMessage("موتوری نصب نیست. از موتور پیش‌فرض اندروید استفاده می‌شود.\nبرای صدای فارسی موتورهایی مثل گویا یا Speak را نصب کنید.")
                .setPositiveButton("باشه", null)
                .show()
            return
        }
        val names = engines.map { e ->
            val label = e.label.ifBlank { e.name }
            val mark = if (e.name == SettingsStore.selectedEngine(this)) " ✓" else ""
            label + mark
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("موتورهای متن به گفتار نصب‌شده")
            .setItems(names) { _, i ->
                val eng = engines[i]
                speech.setEngine(eng.name)
                speech.speak("موتور ${eng.label.ifBlank { eng.name }} انتخاب شد. سلام، این صدای موتور انتخابی است.")
                EarconPlayer.play(this, "activate")
            }
            .setNeutralButton("صداهای این موتور") { _, _ ->
                showVoices()
            }
            .setNegativeButton("بستن", null)
            .show()
    }

    private fun showVoices() {
        val voices = speech.voices()
        if (voices.isEmpty()) {
            speech.speak("صدایی در این موتور پیدا نشد")
            return
        }
        val fa = voices.filter {
            it.locale.language == "fa" ||
                it.name.contains("fa", true) ||
                it.name.contains("persian", true) ||
                it.name.contains("farsi", true) ||
                it.name.contains("iran", true)
        }
        val list = if (fa.isNotEmpty()) fa else voices
        val names = list.map { v ->
            "${v.name} (${v.locale})" + if (v.name == SettingsStore.selectedVoice(this)) " ✓" else ""
        }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle(if (fa.isNotEmpty()) "صداهای فارسی" else "همه صداها")
            .setItems(names) { _, i ->
                speech.setVoice(list[i])
                speech.speak("صدای ${list[i].name} انتخاب شد")
            }
            .setNegativeButton("بستن", null)
            .show()
    }

    private fun showSoundPackages() {
        val pkgs = SoundPackageManager.listPackages()
        val current = FeedbackSettings.soundPackageId(this)
        val names = pkgs.map { p ->
            p.name + if (p.id == current) " ✓" else ""
        }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("پوسته‌های صوتی")
            .setItems(names) { _, i ->
                FeedbackSettings.setSoundPackageId(this, pkgs[i].id)
                EarconPlayer.play(this, "menu")
                speech.speak("پوسته ${pkgs[i].name} فعال شد")
            }
            .setNeutralButton("ویرایش فعلی") { _, _ -> showEditSound() }
            .setNegativeButton("بستن", null)
            .show()
    }

    private fun showEditSound() {
        val actions = SoundPackageManager.actions()
        val names = actions.map { it.second }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("ویرایش پوسته صوتی — انتخاب عمل")
            .setItems(names) { _, i ->
                val action = actions[i].first
                val actionTitle = actions[i].second
                val tones = SoundPackageManager.availableTones()
                val toneNames = tones.map { it.second }.toTypedArray()
                AlertDialog.Builder(this)
                    .setTitle("صوت برای «$actionTitle»")
                    .setItems(toneNames) { _, j ->
                        SoundPackageManager.setCustomTone(this, action, tones[j].first)
                        EarconPlayer.play(this, action)
                        speech.speak("صوت ${tones[j].second} برای $actionTitle تنظیم شد")
                    }
                    .setNegativeButton("بستن", null)
                    .show()
            }
            .setNegativeButton("بستن", null)
            .show()
    }

    private fun showGestureSettings() {
        val gestures = GestureSettings.gestureLabels()
        val names = gestures.map { (key, label) ->
            val act = GestureSettings.getAction(this, key)
            "$label → ${act.title}"
        }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("تنظیمات ژست‌های حرکتی")
            .setItems(names) { _, i ->
                val key = gestures[i].first
                val label = gestures[i].second
                val actions = GestureSettings.Action.entries.toTypedArray()
                val actionNames = actions.map { it.title }.toTypedArray()
                AlertDialog.Builder(this)
                    .setTitle("عمل برای «$label»")
                    .setItems(actionNames) { _, j ->
                        GestureSettings.setAction(this, key, actions[j])
                        EarconPlayer.play(this, "activate")
                        speech.speak("$label تنظیم شد به ${actions[j].title}")
                    }
                    .setNegativeButton("بستن", null)
                    .show()
            }
            .setNegativeButton("بستن", null)
            .show()
    }

    override fun onDestroy() {
        speech.shutdown()
        super.onDestroy()
    }
}
