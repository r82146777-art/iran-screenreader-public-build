package ir.iran.shahrbanoorebuild;
import java.util.*;
/** Independent sample game logic; not the original Unity source. */
public final class GameEngine {
 public static final class Stage {
  public final int number; public final String category, answer, clue;
  Stage(int n,String c,String a,String h){number=n;category=c;answer=a;clue=h;}
 }
 private final List<Stage> stages=Arrays.asList(
  new Stage(1,"واژه‌های ساده","سیب","میوه‌ای گرد که ممکن است قرمز یا سبز باشد"),
  new Stage(2,"خانه","در","برای وارد شدن به اتاق از آن عبور می‌کنیم"),
  new Stage(3,"طبیعت","باران","قطره‌های آبی که از ابر می‌بارند"),
  new Stage(4,"خوراکی‌ها","نان","خوراکی‌ای که معمولاً از آرد تهیه می‌شود"),
  new Stage(5,"حیوانات","گربه","حیوانی خانگی که میو می‌کند"),
  new Stage(6,"وسایل","کتاب","چیزی که آن را می‌خوانیم"),
  new Stage(7,"طبیعت","دریا","پهنه بزرگی از آب شور"),
  new Stage(8,"شهر","خیابان","مسیر عبور خودروها در شهر"));
 private int index=0,hints=3; private final StringBuilder typed=new StringBuilder();
 public Stage current(){return stages.get(index);} public int stageCount(){return stages.size();}
 public int index(){return index;} public String typed(){return typed.toString();}
 public int hints(){return hints;} public boolean isComplete(){return index>=stages.size();}
 public List<String> availableLetters(){
  List<String> a=new ArrayList<>(); for(int i=0;i<current().answer.length();i++)a.add(String.valueOf(current().answer.charAt(i)));
  String[] extras={"م","ر","س","ا","ن","و","ت","ی"};
  for(int i=0;a.size()<current().answer.length()+3;i++)a.add(extras[(index+i)%extras.length]);
  Collections.shuffle(a); return a;
 }
 public void addLetter(String s){if(!isComplete())typed.append(s);}
 public void backspace(){if(typed.length()>0)typed.deleteCharAt(typed.length()-1);}
 public boolean useHint(){if(hints<=0||isComplete())return false;hints--;if(typed.length()<current().answer.length())typed.append(current().answer.charAt(typed.length()));return true;}
 public boolean submit(){if(isComplete()||!typed.toString().equals(current().answer))return false;index++;typed.setLength(0);return true;}
 public void clear(){typed.setLength(0);} public void restart(){index=0;hints=3;typed.setLength(0);}
}