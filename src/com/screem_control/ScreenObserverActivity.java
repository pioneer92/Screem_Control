package com.screem_control;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;

import com.example.screem_control.R;
import com.iflytek.speech.*;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;

@SuppressWarnings("deprecation")
public class ScreenObserverActivity extends Activity {
	static ScreenObserverActivity soa;
	private String TAG = "ScreenObserverActivity";
	String text="";
	RecognizerDialog isrDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		soa=this;
		Log.i(TAG, "ScreenObserverActivity onCreate");
		isrDialog = new RecognizerDialog(ScreenObserverActivity.this,"appid=5231b672");
		this.doSomethingOnScreenOn();
	}

	private void doSomethingOnScreenOn() {		
		RecognizerDialogListener recognizeListener=new RecognizerDialogListener(){
    	    // 识别结果回调接口
    	    public void onResults(ArrayList<RecognizerResult> results,boolean isLast){
    		    Log.i(TAG,results.get(0).text);
    		    if(results.get(0).text.compareTo("杰索")==0 || results.get(0).text.compareTo("杰索。")==0 ||
    		    		results.get(0).text.compareTo("解锁。")==0 || results.get(0).text.compareTo("杰作")==0 ||
    		    		results.get(0).text.compareTo("杰说")==0 || results.get(0).text.compareTo("解锁")==0 ||
    		    		results.get(0).text.compareTo("解说")==0 || results.get(0).text.compareTo("戒所")==0 ||
    		    		results.get(0).text.compareTo("解说。")==0 || results.get(0).text.compareTo("戒所。")==0 ||
    		    		results.get(0).text.compareTo("信息")==0 || results.get(0).text.compareTo("通讯录")==0 ||
    		    		results.get(0).text.compareTo("接受啊")==0 || results.get(0).text.compareTo("接受哦")==0 ||
    		    		results.get(0).text.compareTo("短信")==0 || results.get(0).text.compareTo("联系人")==0 ||
    		    		results.get(0).text.compareTo("短信。")==0 || results.get(0).text.compareTo("尽心")==0 ||
    		    		results.get(0).text.compareTo("杰作啊")==0 || results.get(0).text.compareTo("劫色")==0 ||
    		    		results.get(0).text.compareTo("介绍")==0 || results.get(0).text.compareTo("刚训落")==0 ||
    		    		results.get(0).text.compareTo("杰作。")==0 || results.get(0).text.compareTo("径")==0 ||
    		    		results.get(0).text.compareTo("驯鹿")==0 || results.get(0).text.compareTo("灵犀")==0 ||
    		    		results.get(0).text.compareTo("信心")==0 || results.get(0).text.compareTo("德性。")==0 ||
    		    		results.get(0).text.compareTo("但信")==0 || results.get(0).text.compareTo("灵犀")==0 ||
    		    		results.get(0).text.compareTo("记录")==0 || results.get(0).text.compareTo("接受了。")==0 ||
    		    		results.get(0).text.compareTo("别说 了")==0 || results.get(0).text.compareTo("杰作啊")==0 ||
    		    		results.get(0).text.compareTo("接送哦")==0 || results.get(0).text.compareTo("结束哦")==0 ||
    		    		results.get(0).text.compareTo("记录")==0 || results.get(0).text.compareTo("接受了。")==0 ||
    		    		results.get(0).text.compareTo("厕所。")==0 || results.get(0).text.compareTo("你所")==0 ||
    		    		results.get(0).text.compareTo("姐说")==0 || results.get(0).text.compareTo("接受我")==0 ||
    		    		results.get(0).text.compareTo("感性")==0 || results.get(0).text.compareTo("通讯录。")==0 ||
    		    		results.get(0).text.compareTo("解锁哦")==0 || results.get(0).text.compareTo("矍铄")==0 || 
    		    		results.get(0).text.compareTo("信息。")==0 || results.get(0).text.compareTo("联系人。")==0){
    		    	if(Screen_Server.flag) {
    					//点亮亮屏
    		    		Screen_Server.wakeLock = Screen_Server.pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
    		    		Screen_Server.wakeLock.acquire();
    					//初始化键盘锁，可以锁定或解开键盘锁
    					Screen_Server.mKeyguardLock = Screen_Server.mKeyguardManager.newKeyguardLock(""); 
    					//禁用显示键盘锁定
    					//Screen_Server.wakeLock.release();
    					Screen_Server.mKeyguardLock.disableKeyguard(); 
    					Screen_Server.flag=false;
    					if(!(results.get(0).text.compareTo("信心")==0 || results.get(0).text.compareTo("德性。")==0 ||
    							results.get(0).text.compareTo("但信")==0 || results.get(0).text.compareTo("灵犀")==0 ||
    							results.get(0).text.compareTo("信息")==0 || results.get(0).text.compareTo("通讯录")==0 ||
    							results.get(0).text.compareTo("通讯录。")==0  || results.get(0).text.compareTo("短信")==0||
    							results.get(0).text.compareTo("联系人")==0 || results.get(0).text.compareTo("短信。")==0 ||
    							results.get(0).text.compareTo("感性")==0 || results.get(0).text.compareTo("通讯录。")==0 ||
    							results.get(0).text.compareTo("刚训落")==0 || results.get(0).text.compareTo("尽心")==0 ||
    							results.get(0).text.compareTo("驯鹿")==0 || results.get(0).text.compareTo("信息。")==0 || 
    							results.get(0).text.compareTo("联系人。")==0)){
    						Intent home = new Intent(Intent.ACTION_MAIN);  
        	    		    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	    		    home.addCategory(Intent.CATEGORY_HOME);  
        	    		    startActivity(home);
    					}
    				}
    		    	
    		    	//信息队列调用
    		    	if(results.get(0).text.compareTo("信心")==0 || results.get(0).text.compareTo("德性。")==0 ||
    		    			results.get(0).text.compareTo("但信")==0 || results.get(0).text.compareTo("灵犀")==0 ||
    		    			results.get(0).text.compareTo("信息")==0 || results.get(0).text.compareTo("短信")==0  ||
    		    			results.get(0).text.compareTo("感性")==0 || results.get(0).text.compareTo("短信。")==0 || 
    		    			results.get(0).text.compareTo("尽心")==0 || results.get(0).text.compareTo("信息。")==0){
        		    	Intent intent = new Intent(Intent.ACTION_MAIN);
        		    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		    	intent.setType("vnd.android-dir/mms-sms");
        		    	startActivity(intent);
    		    	}
    		    	
    		    	//通讯录队列调用
    		    	if(results.get(0).text.compareTo("通讯录")==0 || results.get(0).text.compareTo("通讯录。")==0||
    		    			results.get(0).text.compareTo("联系人")==0 || results.get(0).text.compareTo("刚训落")==0 ||
    		    			results.get(0).text.compareTo("驯鹿")==0 || results.get(0).text.compareTo("联系人。")==0){
    		            Intent intent1 = new Intent(Intent.ACTION_MAIN);
        		    	intent1.setClass(ScreenObserverActivity.this, CallActivity.class);
    		    		intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		    		startActivity(intent1);
    		    	}
    		    }
    		    onDestroy();
    	    }
    	    // 会话结束回调接口.
    	    public void onEnd(SpeechError error){
    	    }
        };
        isrDialog.setEngine("sms", null, null);
        isrDialog.setListener(recognizeListener);
        isrDialog.show();
        Log.i(TAG, "Screen is on-on");
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "ScreenObserverActivity is onDestroy");
	    isrDialog.dismiss();
	    finish();
		super.onDestroy();
	}
}