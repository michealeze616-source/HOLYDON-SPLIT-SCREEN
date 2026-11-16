package com.holydon.splitscreen;

import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.provider.Settings;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    LinearLayout root;
    int bg=Color.rgb(10,16,31), cyan=Color.rgb(0,225,255);
    TextView text(String s,int size,int color){ TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); t.setPadding(0,8,0,8); return t; }
    Button btn(String s){ Button b=new Button(this); b.setText(s); b.setTextColor(Color.WHITE); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(22,45,68))); return b; }
    public void onCreate(Bundle b){ super.onCreate(b); getWindow().setStatusBarColor(bg); getWindow().setNavigationBarColor(bg);
        ScrollView scroll=new ScrollView(this); root=new LinearLayout(this); root.setOrientation(1); root.setPadding(24,28,24,24); root.setBackgroundColor(bg); scroll.addView(root); setContentView(scroll);
        root.addView(text("HØLYDØÑ",14,cyan)); root.addView(text("SPLIT SCREEN",28,Color.WHITE)); root.addView(text("Multitasking control centre",14,0xff9bb2c8));
        root.addView(text("SIDE-BY-SIDE MODE",16,cyan));
        root.addView(text("Choose two apps and use Android's built-in split-screen mode when your phone and those apps support it.",14,Color.WHITE));
        Button split=btn("CHOOSE TWO APPS"); root.addView(split); split.setOnClickListener(v->chooseSplit());
        root.addView(text("FLOATING QUICK LAUNCHER",16,cyan));
        root.addView(text("Keep a movable HOLYDON button above your game. Tap it to open the app launcher.",14,Color.WHITE));
        Button start=btn("ENABLE FLOATING BUTTON"); root.addView(start); start.setOnClickListener(v->enableFloat());
        Button stop=btn("TURN OFF FLOATING BUTTON"); root.addView(stop); stop.setOnClickListener(v->stopService(new Intent(this,FloatingService.class)));
        root.addView(text("IMPORTANT: Android does not allow this app to force WhatsApp, TikTok or any other full app into an unrestricted mini-window over eFootball. The floating launcher stays on top; opening another app normally switches to it. Split-screen depends on Android/device support.",12,0xffffcc80));
    }
    void enableFloat(){ if(!Settings.canDrawOverlays(this)){ Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName())); startActivity(i); return; } startService(new Intent(this,FloatingService.class)); Toast.makeText(this,"Floating button enabled",0).show(); }
    void chooseSplit(){ java.util.List<android.content.pm.ResolveInfo> apps=getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),0);
        java.util.ArrayList<String> names=new java.util.ArrayList<>(); java.util.ArrayList<String> pkgs=new java.util.ArrayList<>();
        for(android.content.pm.ResolveInfo r:apps){String p=r.activityInfo.packageName; if(!p.equals(getPackageName())){names.add(r.loadLabel(getPackageManager()).toString());pkgs.add(p);}}
        if(names.isEmpty()) return;
        final String[] selected=new String[2]; final String[] labels=names.toArray(new String[0]);
        new AlertDialog.Builder(this).setTitle("Choose LEFT app").setItems(labels,(d,w)->{selected[0]=pkgs.get(w); new AlertDialog.Builder(this).setTitle("Choose RIGHT app").setItems(labels,(d2,w2)->{selected[1]=pkgs.get(w2); launchSplit(selected[0],selected[1]);}).show();}).show();
    }
    void launchSplit(String a,String b){ try { Intent first=getPackageManager().getLaunchIntentForPackage(a); Intent second=getPackageManager().getLaunchIntentForPackage(b); if(first==null||second==null)throw new Exception(); first.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT|Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(first); second.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT|Intent.FLAG_ACTIVITY_NEW_TASK); new Handler().postDelayed(()->{try{startActivity(second);}catch(Exception e){Toast.makeText(this,"Your phone may not support launching both apps side by side automatically. Use Recents > Split screen.",1).show();}},500); }catch(Exception e){Toast.makeText(this,"Could not start split-screen. Try Android Recents > Split screen.",1).show();} }
}
