package ir.iran.shahrbanoorebuild;
import java.util.*;
/** Independent native word-puzzle content; not original Unity source. */
public final class GameEngine {
 public static final class Stage {
  public final int number; public final String category, answer, clue;
  Stage(int n,String c,String a,String h){number=n;category=c;answer=a;clue=h;}
 }
 private final List<Stage> stages=Arrays.asList(
  new Stage(1,"میوه‌ها","سیب","میوه‌ای گرد که ممکن است قرمز یا سبز باشد"),
  new Stage(2,"خانه","در","برای وارد شدن به اتاق از آن عبور می‌کنیم"),
  new Stage(3,"طبیعت","باران","قطره‌های آبی که از ابر می‌بارند"),
  new Stage(4,"خوراکی‌ها","نان","خوراکی‌ای که معمولاً از آرد تهیه می‌شود"),
  new Stage(5,"حیوانات","گربه","حیوان خانگی که میو می‌کند"),
  new Stage(6,"وسایل","کتاب","چیزی که آن را می‌خوانیم"),
  new Stage(7,"طبیعت","دریا","پهنه بزرگی از آب شور"),
  new Stage(8,"شهر","خیابان","مسیر عبور خودروها در شهر"),
  new Stage(9,"میوه‌ها","موز","میوه‌ای زرد و کشیده"),
  new Stage(10,"آشپزخانه","قاشق","با آن سوپ می‌خوریم"),
  new Stage(11,"حیوانات","اسب","حیوانی که می‌تواند انسان را حمل کند"),
  new Stage(12,"آسمان","ستاره","شب‌ها در آسمان دیده می‌شود"),
  new Stage(13,"خانه","پنجره","از آن بیرون را نگاه می‌کنیم"),
  new Stage(14,"طبیعت","جنگل","جایی پر از درختان"),
  new Stage(15,"خوراکی‌ها","پنیر","از شیر تهیه می‌شود و کنار نان خورده می‌شود"),
  new Stage(16,"وسایل","مداد","با آن می‌توان نوشت"),
  new Stage(17,"حیوانات","ماهی","در آب زندگی می‌کند"),
  new Stage(18,"آب‌وهوا","برف","در هوای سرد از ابر می‌بارد"),
  new Stage(19,"باغ","گل","گیاهی رنگارنگ و خوش‌بو"),
  new Stage(20,"زمان","ساعت","زمان را نشان می‌دهد"),
  new Stage(21,"پوشاک","کفش","هنگام راه رفتن می‌پوشیم"),
  new Stage(22,"حمل‌ونقل","قطار","روی ریل حرکت می‌کند"),
  new Stage(23,"طبیعت","کوه","بلندی بزرگی از سنگ و خاک"),
  new Stage(24,"بدن","دست","با آن چیزها را می‌گیریم"),
  new Stage(25,"خوراکی‌ها","عسل","شیرین است و زنبور آن را می‌سازد"),
  new Stage(26,"مدرسه","معلم","به دانش‌آموزان آموزش می‌دهد"),
  new Stage(27,"حیوانات","خرگوش","گوش‌های بلند دارد و می‌جهد"),
  new Stage(28,"آسمان","خورشید","روزها زمین را روشن می‌کند"),
  new Stage(29,"شهر","بازار","جایی برای خرید کالاها"),
  new Stage(30,"طبیعت","رودخانه","آب در آن جاری است")
  ,new Stage(31,"خوراکی‌ها","هندوانه","میوه‌ای بزرگ با پوست سبز و گوشت قرمز")
  ,new Stage(32,"حیوانات","فیل","حیوان بزرگی با خرطوم بلند")
  ,new Stage(33,"خانه","یخچال","خوراکی‌ها را سرد نگه می‌دارد")
  ,new Stage(34,"پوشاک","پیراهن","لباسی که بالاتنه را می‌پوشاند")
  ,new Stage(35,"حمل‌ونقل","هواپیما","در آسمان پرواز می‌کند و مسافر می‌برد")
  ,new Stage(36,"طبیعت","آبشار","آب از بلندی به پایین می‌ریزد")
  ,new Stage(37,"مدرسه","دفترچه","برای نوشتن یادداشت از آن استفاده می‌کنیم")
  ,new Stage(38,"شهر","بیمارستان","بیماران برای درمان به آنجا می‌روند")
  ,new Stage(39,"آشپزخانه","قابلمه","در آن غذا می‌پزند")
  ,new Stage(40,"آسمان","رنگین‌کمان","پس از باران گاهی با چند رنگ دیده می‌شود")
  ,new Stage(41,"حیوانات","لاک‌پشت","جانوری با لاک سخت روی بدن")
  ,new Stage(42,"طبیعت","کوهستان","منطقه‌ای گسترده با قله‌های بلند")
  ,new Stage(43,"وسایل","چراغ‌قوه","برای روشن کردن مسیر در تاریکی به کار می‌رود")
  ,new Stage(44,"زمان","تقویم","روزها و ماه‌های سال را نشان می‌دهد")
  ,new Stage(45,"خوراکی‌ها","شیرینی","خوراکی شیرین که اغلب با آرد درست می‌شود")
  ,new Stage(46,"حمل‌ونقل","دوچرخه","وسیله‌ای دوچرخ که با رکاب حرکت می‌کند")
  ,new Stage(47,"باغ","درختچه","گیاهی چوبی که از درخت کوچک‌تر است")
  ,new Stage(48,"بدن","انگشت","یکی از بخش‌های دست یا پا")
  ,new Stage(49,"مدرسه","کتابخانه","جایی برای نگهداری و خواندن کتاب‌ها")
  ,new Stage(50,"شهر","چهارراه","محل برخورد چند خیابان")
  ,new Stage(51,"طبیعت","چشمه","آب از زمین در آن بیرون می‌آید")
  ,new Stage(52,"حیوانات","پروانه","حشره‌ای رنگارنگ با بال‌های ظریف")
  ,new Stage(53,"آشپزخانه","سماور","برای جوش آوردن آب چای استفاده می‌شود")
  ,new Stage(54,"پوشاک","دستکش","دست‌ها را گرم نگه می‌دارد")
  ,new Stage(55,"آسمان","کهکشان","مجموعه‌ای بسیار بزرگ از ستاره‌ها")
  ,new Stage(56,"وسایل","جاروبرقی","گردوغبار را با مکش جمع می‌کند")
  ,new Stage(57,"خوراکی‌ها","بادمجان","سبزی بنفشی که در غذاهای گوناگون استفاده می‌شود")
  ,new Stage(58,"حمل‌ونقل","موتورسیکلت","وسیله نقلیه‌ای دوچرخ با موتور")
  ,new Stage(59,"خانه","آبگرمکن","آب مصرفی خانه را گرم می‌کند")
  ,new Stage(60,"طبیعت","اقیانوس","پهنه‌ای بسیار بزرگ از آب شور")
  ,new Stage(61,"حیوانات","تمساح","خزنده‌ای بزرگ که در آب و خشکی زندگی می‌کند")
  ,new Stage(62,"مدرسه","دانش‌آموز","کسی که در مدرسه درس می‌خواند")
  ,new Stage(63,"شهر","آتش‌نشانی","برای خاموش کردن آتش و نجات مردم کمک می‌کند")
  ,new Stage(64,"بدن","آرنج","مفصلی میان بازو و ساعد")
  ,new Stage(65,"خوراکی‌ها","قارچ","خوراکی‌ای که در غذاهای مختلف به کار می‌رود")
  ,new Stage(66,"طبیعت","زمین‌لرزه","لرزش ناگهانی سطح زمین")
  ,new Stage(67,"وسایل","دوربین","برای ثبت عکس و فیلم استفاده می‌شود")
  ,new Stage(68,"زمان","نیمه‌شب","میانه شب، نزدیک ساعت دوازده")
  ,new Stage(69,"حمل‌ونقل","زیردریایی","وسیله‌ای که زیر سطح آب حرکت می‌کند")
  ,new Stage(70,"باغ","گل‌آفتابگردان","گلی بزرگ که معمولاً رو به نور خورشید است")
  ,new Stage(71,"آسمان","صورت‌فلکی","گروهی از ستاره‌ها که الگویی خیالی می‌سازند")
  ,new Stage(72,"خانه","ماشین‌لباسشویی","لباس‌ها را به‌صورت خودکار می‌شوید")
  ,new Stage(73,"خوراکی‌ها","آب‌پرتقال","نوشیدنی‌ای که از فشردن پرتقال به دست می‌آید")
  ,new Stage(74,"طبیعت","جنگل‌بارانی","جنگلی گرم و مرطوب با بارندگی فراوان")
  ,new Stage(75,"حیوانات","اسب‌آبی","پستانداری بزرگ که بیشتر وقتش را در آب می‌گذراند")
  ,new Stage(76,"شهر","ایستگاه‌قطار","محل توقف و سوار شدن به قطار")
  ,new Stage(77,"وسایل","ماشین‌حساب","برای انجام محاسبات عددی به کار می‌رود")
  ,new Stage(78,"مدرسه","آزمایشگاه","جایی برای انجام آزمایش‌های علمی")
  ,new Stage(79,"طبیعت","شفق‌قطبی","نورهای رنگی که نزدیک قطب‌ها در آسمان دیده می‌شوند")
  ,new Stage(80,"حمل‌ونقل","چراغ‌راهنما","با رنگ‌های خود عبور و توقف خودروها را کنترل می‌کند")
  ,new Stage(81,"خوراکی‌ها","ته‌چین","غذای ایرانی برنجی که معمولاً لایه‌ای و قالبی است")
  ,new Stage(82,"حیوانات","زرافه","جانوری با گردن بسیار بلند")
  ,new Stage(83,"شهر","فرودگاه","محل برخاستن و فرود آمدن هواپیماها")
  ,new Stage(84,"خانه","جارو‌دستی","وسیله‌ای برای جمع کردن گردوخاک با دست")
  ,new Stage(85,"طبیعت","آتشفشان","کوهی که ممکن است مواد داغ از آن بیرون بیاید")
  ,new Stage(86,"وسایل","چترنجات","وسیله‌ای که سرعت سقوط انسان را کم می‌کند")
  ,new Stage(87,"بدن","استخوان‌بندی","چارچوب استخوانی بدن انسان")
  ,new Stage(88,"آسمان","ستاره‌شناسی","دانش بررسی ستاره‌ها و اجرام آسمانی")
  ,new Stage(89,"مدرسه","هندسه‌دان","کسی که هندسه را مطالعه می‌کند")
  ,new Stage(90,"طبیعت","زیست‌بوم","محیط زندگی جانداران و ارتباط آن‌ها")
  ,new Stage(91,"خوراکی‌ها","قرمه‌سبزی","خورش ایرانی با سبزی و لوبیا")
  ,new Stage(92,"حمل‌ونقل","کشتی‌بخاری","کشتی‌ای که با نیروی بخار حرکت می‌کند")
  ,new Stage(93,"وسایل","قطب‌نما","جهت‌ها را با کمک میدان مغناطیسی نشان می‌دهد")
  ,new Stage(94,"شهر","کتاب‌فروشی","مغازه‌ای که کتاب می‌فروشد")
  ,new Stage(95,"طبیعت","زمین‌شناسی","دانش بررسی ساختار و تاریخ زمین")
  ,new Stage(96,"حیوانات","زنبورعسل","حشره‌ای اجتماعی که عسل تولید می‌کند")
  ,new Stage(97,"مدرسه","دانش‌پژوهی","فرایند جست‌وجوی دانش و یادگیری")
  ,new Stage(98,"آسمان","منظومه‌شمسی","خورشید و اجرامی که به دور آن می‌گردند")
  ,new Stage(99,"طبیعت","تنوع‌زیستی","گوناگونی گونه‌های جاندار در یک منطقه")
  ,new Stage(100,"دانش","هوش‌مصنوعی","فناوری‌ای برای انجام کارهایی که به هوش نیاز دارند")

 );
 private int index=0,hints=3; private final StringBuilder typed=new StringBuilder();
 private List<String> choices=new ArrayList<>();
 private final Set<Integer> usedSlots=new HashSet<>(); private final List<Integer> selectedSlots=new ArrayList<>();
 public GameEngine(){makeChoices();}
 public Stage current(){return stages.get(index);} public int stageCount(){return stages.size();}
 public int index(){return index;} public String typed(){return typed.toString();}
 public int hints(){return hints;} public boolean isComplete(){return index>=stages.size();}
 private void makeChoices(){
  choices.clear(); if(isComplete())return;
  String answer=current().answer;
  for(int i=0;i<answer.length();i++)choices.add(String.valueOf(answer.charAt(i)));
  String[] extras={"م","ر","س","ا","ن","و","ت","ی","ک","ل","ه","ب","د","گ"};
  int k=0;while(choices.size()<answer.length()+3){String x=extras[(index*3+k++)%extras.length];if(!choices.contains(x)||k>extras.length)choices.add(x);}
  Collections.shuffle(choices,new Random(7301L+index));
 }
 public List<String> availableLetters(){return new ArrayList<>(choices);}
 public boolean isSlotUsed(int slot){return usedSlots.contains(slot);}
 public void addLetter(String s){if(!isComplete())typed.append(s);}
 public void addLetterAt(int slot){if(isComplete()||slot<0||slot>=choices.size()||usedSlots.contains(slot))return;usedSlots.add(slot);selectedSlots.add(slot);typed.append(choices.get(slot));}
 public void backspace(){if(typed.length()>0){typed.deleteCharAt(typed.length()-1);if(!selectedSlots.isEmpty()){int last=selectedSlots.remove(selectedSlots.size()-1);if(last>=0)usedSlots.remove(last);}}}
 public boolean useHint(){
  if(hints<=0||isComplete()||typed.length()>=current().answer.length())return false;
  hints--;char expected=current().answer.charAt(typed.length());
  for(int i=0;i<choices.size();i++){
   if(!usedSlots.contains(i)&&choices.get(i).charAt(0)==expected){
    usedSlots.add(i);selectedSlots.add(i);typed.append(expected);return true;
   }
  }
  selectedSlots.add(-1);typed.append(expected);return true;
 }
 public boolean submit(){if(isComplete()||!typed.toString().equals(current().answer))return false;index++;typed.setLength(0);usedSlots.clear();selectedSlots.clear();makeChoices();return true;}
 public void clear(){typed.setLength(0);usedSlots.clear();selectedSlots.clear();}
 public void restart(){index=0;hints=3;typed.setLength(0);usedSlots.clear();selectedSlots.clear();makeChoices();}

}