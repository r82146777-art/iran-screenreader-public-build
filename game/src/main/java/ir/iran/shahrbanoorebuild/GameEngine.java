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
 );
 private int index=0,hints=3; private final StringBuilder typed=new StringBuilder();
 private List<String> choices=new ArrayList<>();
 private final Set<Integer> usedSlots=new HashSet<>();
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
 public void addLetterAt(int slot){if(isComplete()||slot<0||slot>=choices.size()||usedSlots.contains(slot))return;usedSlots.add(slot);typed.append(choices.get(slot));}
 public void backspace(){if(typed.length()>0){typed.deleteCharAt(typed.length()-1);if(!usedSlots.isEmpty()){int last=-1;for(int n:usedSlots)if(n>last)last=n;usedSlots.remove(last);}}}
 public boolean useHint(){
  if(hints<=0||isComplete()||typed.length()>=current().answer.length())return false;
  hints--;char expected=current().answer.charAt(typed.length());
  for(int i=0;i<choices.size();i++)if(!usedSlots.contains(i)&&choices.get(i).charAt(0)==expected){usedSlots.add(i);typed.append(expected);return true;}
  typed.append(expected);return true;
 }
 public boolean submit(){if(isComplete()||!typed.toString().equals(current().answer))return false;index++;typed.setLength(0);usedSlots.clear();makeChoices();return true;}
 public void clear(){typed.setLength(0);usedSlots.clear();}
 public void restart(){index=0;hints=3;typed.setLength(0);usedSlots.clear();makeChoices();}
}