package com.holydon.splitscreen;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.PixelFormat;
import android.view.*;
import android.widget.*;
import android.graphics.Color;

public class FloatingService extends Service {
    WindowManager wm; View bubble; int x=20,y=260;
    public void onCreate(){super.onCreate(); wm=(WindowManager)getSystemService(WINDOW_SERVICE); showBubble();}
    int type(){return Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE;}
    void showBubble(){
        TextView b=new TextView(this); bubble=b; b.setText("HD"); b.setTextColor(Color.WHITE); b.setTextSize(16); b.setGravity(Gravity.CENTER); b.setBackgroundTintList(null); b.setBackground(new android.graphics.drawable.GradientDrawable(){ {setColor(Color.rgb(0,170,210));setShape(1);setStroke(3,Color.WHITE);} });
        WindowManager.LayoutParams p=new WindowManager.LayoutParams(58,58,type(),WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT); p.gravity=Gravity.TOP|Gravity.LEFT; p.x=x;p.y=y;
        b.setOnTouchListener(new View.OnTouchListener(){float sx,sy;int ox,oy; public boolean onTouch(View v,android.view.MotionEvent e){if(e.getAction()==0){sx=e.getRawX();sy=e.getRawY();ox=p.x;oy=p.y;return true;} if(e.getAction()==1){if(Math.abs(e.getRawX()-sx)<10&&Math.abs(e.getRawY()-sy)<10)showApps();return true;} if(e.getAction()==2){p.x=ox+(int)(e.getRawX()-sx);p.y=oy+(int)(e.getRawY()-sy);wm.updateViewLayout(b,p);return true;}return true;}});
        wm.addView(b,p);
    }
    void showApps(){
        java.util.List<android.content.pm.ResolveInfo> apps=getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),0);
        java.util.ArrayList<String> names=new java.util.ArrayList<>(); java.util.ArrayList<String> pkgs=new java.util.ArrayList<>();
        for(android.content.pm.ResolveInfo r:apps){String pkg=r.activityInfo.packageName;if(!pkg.equals(getPackageName())){names.add(r.loadLabel(getPackageManager()).toString());pkgs.add(pkg);}}
        android.widget.LinearLayout panel=new android.widget.LinearLayout(this); panel.setOrientation(1); panel.setPadding(18,18,18,18); panel.setBackgroundColor(Color.rgb(12,22,40));
        TextView title=new TextView(this);title.setText("HOLYDON • QUICK LAUNCH");title.setTextColor(Color.CYAN);title.setTextSize(17);panel.addView(title);
        ScrollView sc=new ScrollView(this); LinearLayout list=new LinearLayout(this);list.setOrientation(1);sc.addView(list);panel.addView(sc);
        for(int i=0;i<names.size();i++){final String pkg=pkgs.get(i);Button item=new Button(this);item.setText(names.get(i));item.setTextColor(Color.WHITE);item.setOnClickListener(v->{try{Intent in=getPackageManager().getLaunchIntentForPackage(pkg);if(in!=null){in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(in);} }catch(Exception ignored){} removePanel();});list.addView(item);}
        Button close=new Button(this);close.setText("CLOSE");close.setOnClickListener(v->removePanel());panel.addView(close);
        WindowManager.LayoutParams p=new WindowManager.LayoutParams((int)(getResources().getDisplayMetrics().widthPixels*.82), (int)(getResources().getDisplayMetrics().heightPixels*.65),type(),WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_DIM_BEHIND,PixelFormat.TRANSLUCENT);p.gravity=Gravity.CENTER;p.dimAmount=.35f;wm.addView(panel,p); panel.setTag(p); activePanel=panel;
    }
    View activePanel;
    void removePanel(){if(activePanel!=null){try{wm.removeView(activePanel);}catch(Exception e){}activePanel=null;}}
    public int onStartCommand(Intent i,int f,int id){return START_STICKY;}
    public void onDestroy(){removePanel();if(bubble!=null)wm.removeView(bubble);super.onDestroy();}
    public android.os.IBinder onBind(Intent i){return null;}
}
