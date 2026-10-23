package ir.iran.shahrbanoorebuild

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.graphics.Typeface
import android.speech.tts.TextToSpeech
import android.widget.*
import android.content.SharedPreferences
import java.util.Locale

/** Accessible native word puzzle. Keep the letter keyboard stable while typing so TalkBack focus does not jump to the top. */
class MainActivity : Activity(), TextToSpeech.OnInitListener {
 private lateinit var tts:TextToSpeech
 private var ttsReady=false
 private lateinit var prefs:SharedPreferences
 private val game=GameEngine()
 private lateinit var root:LinearLayout
 private lateinit var live:TextView
 private lateinit var answerView:TextView
 private lateinit var hintsView:TextView
 private val letterButtons= mutableListOf<Button>()
 private fun soundEnabled()=prefs.getBoolean("sound_enabled",true)
 private fun highContrast()=prefs.getBoolean("high_contrast",false)
 private fun largeText()=prefs.getBoolean("large_text",false)
 override fun onCreate(savedInstanceState:Bundle?){
  super.onCreate(savedInstanceState)
  prefs=getSharedPreferences("shahrbanoo_settings",MODE_PRIVATE)
  window.decorView.layoutDirection=View.LAYOUT_DIRECTION_RTL
  tts=TextToSpeech(this,this)
  home()
 }
 override fun onInit(status:Int){
  if(status==TextToSpeech.SUCCESS){
   val result=tts.setLanguage(Locale("fa","IR"))
   ttsReady=result!=TextToSpeech.LANG_MISSING_DATA&&result!=TextToSpeech.LANG_NOT_SUPPORTED
   applyVoiceSettings()
   if(ttsReady){
    val persianVoice=tts.voices?.filter{it.locale.language=="fa"}?.maxByOrNull{it.quality}
    if(persianVoice!=null)tts.voice=persianVoice
    speak("شهربانو آماده است. دکمه شروع بازی را انتخاب کنید.")
   }
  }
 }
 private fun applyVoiceSettings(){
  if(!::tts.isInitialized)return
  tts.setSpeechRate(prefs.getFloat("speech_rate",0.92f))
  tts.setPitch(1.02f)
 }
 private fun speak(s:String){
  live.text=s
  live.announceForAccessibility(s)
  if(ttsReady)tts.speak(s,TextToSpeech.QUEUE_FLUSH,null,"game-${System.currentTimeMillis()}")
 }
 private fun play(kind:String){if(soundEnabled())GameSound.play(this,kind)}
 private fun layout(title:String){
  val scroll=ScrollView(this)
  root=LinearLayout(this).apply{
   orientation=LinearLayout.VERTICAL
   gravity=Gravity.CENTER_HORIZONTAL
   setPadding(20,20,20,24)
   layoutDirection=View.LAYOUT_DIRECTION_RTL
   setBackgroundColor(if(highContrast())Color.BLACK else Color.rgb(244,247,252))
  }
  scroll.addView(root)
  setContentView(scroll)
  root.addView(TextView(this).apply{
   text=title;textSize=if(largeText())30f else 25f;typeface=Typeface.DEFAULT_BOLD;gravity=Gravity.CENTER
   setTextColor(if(highContrast())Color.YELLOW else Color.rgb(27,52,91))
   contentDescription=title;setPadding(0,12,0,20)
  },params())
  live=TextView(this).apply{
   textSize=if(largeText())22f else 18f;gravity=Gravity.CENTER
   setTextColor(if(highContrast())Color.WHITE else Color.rgb(35,45,60))
   contentDescription="پیام بازی";setPadding(0,8,0,12)
   importantForAccessibility=View.IMPORTANT_FOR_ACCESSIBILITY_YES
  }
  root.addView(live,params())
 }
 private fun home(){
  layout("شهربانو")
  text("بازی معمای کلمه — نسخه دسترس‌پذیر")
  text("برای نابینایان و افراد بینا؛ حروف را انتخاب کنید و پاسخ را پیدا کنید.")
  button("شروع بازی"){play("start");stage()}
  button("راهنمای بازی"){
   play("menu")
   speak("سرنخ و دسته را بشنوید. سپس حروف کیبورد را یکی یکی انتخاب کنید. بعد از انتخاب هر حرف، تمرکز صفحه‌خوان روی همان کیبورد باقی می‌ماند. با حذف آخرین حرف یا پاک کردن پاسخ می‌توانید اصلاح کنید. از راهنمایی برای آشکار شدن حرف استفاده کنید.")
  }
  button("تنظیمات"){play("menu");settings()}
  button("شروع دوباره"){game.restart();play("start");stage()}
 }
 private fun settings(){
  layout("تنظیمات بازی")
  text("تغییرات بلافاصله ذخیره می‌شوند.")
  val sound=CheckBox(this).apply{
   text="فعال بودن افکت‌های صوتی";textSize=19f;isChecked=soundEnabled()
   setTextColor(if(highContrast())Color.WHITE else Color.BLACK)
   contentDescription=text.toString()
   setOnCheckedChangeListener{_,checked->prefs.edit().putBoolean("sound_enabled",checked).apply();if(checked)GameSound.play(this@MainActivity,"correct")}
  }
  root.addView(sound,params())
  val contrast=CheckBox(this).apply{
   text="حالت کنتراست بالا";textSize=19f;isChecked=highContrast()
   setTextColor(if(highContrast())Color.WHITE else Color.BLACK)
   setOnCheckedChangeListener{_,checked->prefs.edit().putBoolean("high_contrast",checked).apply();settings()}
  }
  root.addView(contrast,params())
  val large=CheckBox(this).apply{
   text="متن و دکمه‌های درشت‌تر";textSize=19f;isChecked=largeText()
   setTextColor(if(highContrast())Color.WHITE else Color.BLACK)
   setOnCheckedChangeListener{_,checked->prefs.edit().putBoolean("large_text",checked).apply();settings()}
  }
  root.addView(large,params())
  text("سرعت گوینده")
  val rates=listOf("آهسته" to 0.75f,"معمولی" to 0.92f,"سریع" to 1.10f,"خیلی سریع" to 1.25f)
  val rateGroup=RadioGroup(this).apply{orientation=RadioGroup.VERTICAL}
  rates.forEach{(label,rate)->
   rateGroup.addView(RadioButton(this).apply{
    text=label;textSize=18f;tag=rate
    setTextColor(if(highContrast())Color.WHITE else Color.BLACK)
    isChecked=kotlin.math.abs(prefs.getFloat("speech_rate",0.92f)-rate)<0.01f
   })
  }
  rateGroup.setOnCheckedChangeListener{group,id->
   val selected=group.findViewById<RadioButton>(id)
   val rate=selected?.tag as? Float
   if(rate!=null){prefs.edit().putFloat("speech_rate",rate).apply();applyVoiceSettings();speak("سرعت گوینده تغییر کرد.")}
  }
  root.addView(rateGroup,params())
  button("آزمایش گوینده"){speak("سلام! این صدای راهنمای بازی شهربانو است.")}
  button("بازگشت به منو"){play("menu");home()}
 }
 private fun stage(){
  letterButtons.clear()
  if(game.isComplete){
   layout("آفرین! همه مراحل تمام شد")
   text("پایان نسخه بازسازی‌شده")
   button("بازی دوباره"){game.restart();play("start");stage()}
   button("منوی اصلی"){play("menu");home()}
   play("win");speak("تبریک! همه مراحل این نسخه را تمام کردید.");return
  }
  val s=game.current()
  layout("مرحله ${s.number} از ${game.stageCount()}")
  text("دسته: ${s.category}")
  text("سرنخ: ${s.clue}")
  answerView=text("پاسخ فعلی: ${game.typed().ifEmpty{"هنوز حرفی انتخاب نشده"}}")
  hintsView=text("راهنماهای باقی‌مانده: ${game.hints()}")
  text("کیبورد حروف — با انتخاب هر حرف، فوکوس صفحه‌خوان در همین بخش می‌ماند.")
  val row=LinearLayout(this).apply{
   orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER;layoutDirection=View.LAYOUT_DIRECTION_RTL
   isFocusable=false
  }
  game.availableLetters().forEachIndexed{i,ch->
   val letterButton=Button(this).apply{
    text=ch.toString();textSize=if(largeText())26f else 21f
    contentDescription="حرف $ch، گزینه ${i+1}"
    minHeight=if(largeText())64 else 48
    setOnClickListener{
     if(!game.isSlotUsed(i)){
      game.addLetterAt(i)
      play("tap")
      answerView.text="پاسخ فعلی: ${game.typed().ifEmpty{"هنوز حرفی انتخاب نشده"}}"
      contentDescription="حرف $ch، انتخاب شد. گزینه ${i+1}"
      // Do not rebuild the screen or disable this view here: both actions can make TalkBack lose its current focus.
      announceForAccessibility("حرف $ch انتخاب شد")
     }else{
      speak("این حرف قبلاً انتخاب شده است.")
     }
    }
   }
   letterButtons.add(letterButton)
   row.addView(letterButton,LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f).apply{setMargins(2,2,2,2)})
  }
  root.addView(row,params())
  button("حذف آخرین حرف"){game.backspace();play("tap");stage()}
  button("پاک کردن پاسخ"){game.clear();play("tap");stage()}
  button("بررسی پاسخ"){
   if(game.submit()){
    play("correct");Toast.makeText(this,"پاسخ درست بود",Toast.LENGTH_SHORT).show()
    stage()
    if(!game.isComplete)speak("آفرین، پاسخ درست است. مرحله ${game.current().number}. ${game.current().clue}")
   }else{play("wrong");speak("پاسخ درست نیست. دوباره تلاش کنید.")}
  }
  button("راهنمایی"){if(game.useHint()){play("hint");stage()}else{play("wrong");speak("راهنمایی دیگری باقی نمانده است.")}}
  button("تکرار سرنخ"){play("focus");speak("مرحله ${s.number}. دسته ${s.category}. ${s.clue}")}
  button("تنظیمات"){play("menu");settings()}
  button("منوی اصلی"){play("menu");home()}
  play("stage");speak("مرحله ${s.number}. دسته ${s.category}. سرنخ: ${s.clue}.")
 }
 private fun text(s:String):TextView{
  val view=TextView(this).apply{
   text=s;textSize=if(largeText())23f else 19f;gravity=Gravity.CENTER
   setTextColor(if(highContrast())Color.WHITE else Color.rgb(35,45,60))
   contentDescription=s;setPadding(8,10,8,10)
  }
  root.addView(view,params());return view
 }
 private fun button(s:String,action:()->Unit){
  root.addView(Button(this).apply{
   text=s;textSize=if(largeText())22f else 18f
   contentDescription=s;importantForAccessibility=View.IMPORTANT_FOR_ACCESSIBILITY_YES
   minHeight=if(largeText())64 else 48
   setOnClickListener{action()}
  },params().apply{setMargins(0,6,0,6)})
 }
 private fun params()=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
 override fun onDestroy(){if(::tts.isInitialized){tts.stop();tts.shutdown()};GameSound.release();super.onDestroy()}
}
