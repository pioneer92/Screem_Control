package com.screem_control;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;

@SuppressWarnings("deprecation")
public class Screen_Server extends Service {

	public static PowerManager pm;
	public static KeyguardLock mKeyguardLock = null; 
	public static KeyguardManager mKeyguardManager = null;
	public static PowerManager.WakeLock wakeLock;
	public static boolean flag=true;
	public static boolean flag1=true;
	Screem_Receiver sr=new Screem_Receiver();
	IntentFilter filter =new IntentFilter();
	@Override
	public void onCreate() {
		super.onCreate();
		flag1=false;
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
//		if(flag1){
//			Intent i=new Intent();
//			i.addCategory(Intent.ACTION_MAIN);
//			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.setClass(Screen_Server.this, ScreenObserverActivity.class);
//			startActivity(intent);
//		}
		filter.addAction("android.intent.action.SCREEN_ON");
		filter.addAction("android.intent.action.SCREEN_OFF");
		Screen_Server.this.registerReceiver(sr, filter);
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		Screen_Server.this.unregisterReceiver(sr);
		super.onDestroy();
	}

}
