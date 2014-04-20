package com.screem_control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import com.iflytek.ui.RecognizerDialog;

public class Screem_Receiver extends BroadcastReceiver{
	
	private String TAG = "ScreenObserverActivity";
	String text="";
	TextView tv;
	RecognizerDialog isrDialog;
	boolean flag1=false;

	@Override
	public void onReceive(Context context, Intent intent) {
		isrDialog = new RecognizerDialog(context,"appid=5231b672");
		if(intent.getAction().equals("android.intent.action.SCREEN_ON")){
			flag1=true;
			Log.i("MainActivity", "android.intent.action.SCREEN_ON");
			intent.addCategory(Intent.ACTION_MAIN);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(context, ScreenObserverActivity.class);
			context.startActivity(intent);
		}
		else if(intent.getAction().equals("android.intent.action.SCREEN_OFF")){
			Log.i("MainActivity", "android.intent.action.SCREEN_OFF");
			this.doSomethingOnScreenOff();
		}
		else if(intent.getAction().equals("android.intent.action.USER_PRESENT")){
			Log.i("MainActivity", "android.intent.action.USER_PRESENT");
			if(ScreenObserverActivity.soa != null && !ScreenObserverActivity.soa.isFinishing()){
				ScreenObserverActivity.soa.onDestroy();
				flag1=false;
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void doSomethingOnScreenOff() {
		Log.i(TAG, "Screen is off-off");
		if(ScreenObserverActivity.soa != null && !ScreenObserverActivity.soa.isFinishing()){
			ScreenObserverActivity.soa.onDestroy();
		}
		if(Screen_Server.flag == false){
			Screen_Server.mKeyguardLock.reenableKeyguard();
			Screen_Server.wakeLock.release();
			Screen_Server.flag=true;
		}
		flag1=false;
	}
}
