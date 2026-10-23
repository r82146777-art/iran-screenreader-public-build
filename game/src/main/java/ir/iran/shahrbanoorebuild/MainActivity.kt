package ir.iran.shahrbanoorebuild
import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.graphics.Typeface
import android.speech.tts.TextToSpeech
import android.widget.*
import java.util.Locale

/** Native accessible word-puzzle reconstruction. */
class MainActivity : Activity(), TextToSpeech.OnInitListener {
 private lateinit var tts:TextToSpeech
 private var ttsReady=false
 private val game=GameEngine()
 private lateinit var root:LinearLayout
 private lateinit var live:TextView
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);window.decorView.layoutDirection=View.LAYOUT_DIRECTION_RTL;tts=TextToSpeech(this,this);home()}
 override fun onInit(status:Int){
  if(status==TextToSpeech.SUCCESS){
   val result=tts.setLanguage(Locale("fa","IR"))
   ttsReady=result!=TextToSpeech.LANG_MISSING_DATA&&result!=TextToSpeech.LANG_NOT_SUPPORTED
   tts.setSpeechRate(0.92f);tts.setPitch(1.02f)
   if(ttsReady){
    val persianVoice=tts.voices?.filter{it.locale.language=="fa"}?.maxByOrNull{it.quality}
    if(persianVoice!=null)tts.voice=persianVoice
   }
   speak("شهربانو دسترس‌پذیر آماده است. دکمه شروع بازی را انتخاب کنید.")
  }
 }
 private fun speak(s:String){live.text=s;live.announceForAccessibility(s);if(ttsReady)tts.speak(s,TextToSpeech.QUEUE_FLUSH,null,"game-${System.currentTimeMillis()}")}
 private fun layout(title:String){
  val scroll=ScrollView(this);root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER_HORIZONTAL;setPadding(24,24,24,24);layoutDirection=View.LAYOUT_DIRECTION_RTL}
  scroll.addView(root);setContentView(scroll)
  root.addView(TextView(this).apply{text=title;textSize=25f;typeface=Typeface.DEFAULT_BOLD;gravity=Gravity.CENTER;contentDescription=title;setPadding(0,12,0,20)},params())
  live=TextView(this).apply{textSize=18f;gravity=Gravity.CENTER;contentDescription="پیام بازی";setPadding(0,8,0,12)}
  root.addView(live,params())
 }
 private fun home(){
  layout("شهربانو — بازسازی دسترس‌پذیر")
  text("نسخه معمای کلمه با ۳۰ مرحله، صدا و افکت‌های بازی")
  button("شروع بازی"){GameSound.play(this,"start");stage()}
  button("راهنمای بازی"){GameSound.play(this,"menu");speak("سرنخ را بشنوید، حروف را انتخاب کنید و پاسخ را بررسی کنید. با دکمه راهنمایی یک حرف آشکار می‌شود. دکمه‌های بازی با صفحه‌خوان قابل دسترسی هستند.")}
  button("شروع دوباره"){game.restart();GameSound.play(this,"start");stage()}
 }
 private fun stage(){
  if(game.isComplete){
   layout("آفرین! همه مراحل تمام شد");text("پایان نسخه بازسازی‌شده")
   button("بازی دوباره"){game.restart();GameSound.play(this,"start");stage()}
   button("منوی اصلی"){GameSound.play(this,"menu");home()}
   GameSound.play(this,"win");speak("تبریک! همه سی مرحله این نسخه را تمام کردید.");return
  }
  val s=game.current()
  layout("مرحله ${s.number} از ${game.stageCount()}")
  text("دسته: ${s.category}");text("سرنخ: ${s.clue}")
  text("پاسخ فعلی: ${game.typed().ifEmpty{"هنوز حرفی انتخاب نشده"}}")
  text("راهنماهای باقی‌مانده: ${game.hints()}")
  val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER;layoutDirection=View.LAYOUT_DIRECTION_RTL}
  game.availableLetters().forEachIndexed{i,ch->
   row.addView(Button(this).apply{
    text=ch;textSize=21f;contentDescription="حرف ${ch}، گزینه ${i+1}" 
    isEnabled=!game.isSlotUsed(i)
    setOnClickListener{game.addLetterAt(i);GameSound.play(this@MainActivity,"tap");stage()}
   },LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f).apply{setMargins(2,2,2,2)})
  }
  root.addView(row,params())
  button("حذف آخرین حرف"){game.backspace();GameSound.play(this,"tap");stage()}
  button("پاک کردن پاسخ"){game.clear();GameSound.play(this,"tap");stage()}
  button("بررسی پاسخ"){
   if(game.submit()){
    GameSound.play(this,"correct");Toast.makeText(this,"پاسخ درست بود",Toast.LENGTH_SHORT).show();stage()
    if(!game.isComplete)speak("آفرین، پاسخ درست است. مرحله ${game.current().number}. ${game.current().clue}")
   }else{GameSound.play(this,"wrong");speak("پاسخ درست نیست. دوباره تلاش کنید.")}
  }
  button("راهنمایی"){if(game.useHint()){GameSound.play(this,"hint");stage()}else{GameSound.play(this,"wrong");speak("راهنمایی دیگری باقی نمانده است.")}}
  button("تکرار سرنخ"){GameSound.play(this,"focus");speak("مرحله ${s.number}. دسته ${s.category}. ${s.clue}")}
  button("منوی اصلی"){GameSound.play(this,"menu");home()}
  GameSound.play(this,"stage");speak("مرحله ${s.number}. دسته ${s.category}. سرنخ: ${s.clue}.")
 }
 private fun text(s:String){root.addView(TextView(this).apply{text=s;textSize=19f;gravity=Gravity.CENTER;contentDescription=s;setPadding(0,8,0,8)},params())}
 private fun button(s:String,action:()->Unit){root.addView(Button(this).apply{text=s;textSize=18f;contentDescription=s;importantForAccessibility=View.IMPORTANT_FOR_ACCESSIBILITY_YES;setOnClickListener{action()}},params().apply{setMargins(0,6,0,6)})}
 private fun params()=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
 override fun onDestroy(){if(::tts.isInitialized){tts.stop();tts.shutdown()};GameSound.release();super.onDestroy()}
}