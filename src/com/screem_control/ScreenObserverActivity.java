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
    	    // ʶ�����ص��ӿ�
    	    public void onResults(ArrayList<RecognizerResult> results,boolean isLast){
    		    Log.i(TAG,results.get(0).text);
    		    if(results.get(0).text.compareTo("����")==0 || results.get(0).text.compareTo("������")==0 ||
    		    		results.get(0).text.compareTo("������")==0 || results.get(0).text.compareTo("����")==0 ||
    		    		results.get(0).text.compareTo("��˵")==0 || results.get(0).text.compareTo("����")==0 ||
    		    		results.get(0).text.compareTo("��˵")==0 || results.get(0).text.compareTo("����")==0 ||
    		    		results.get(0).text.compareTo("��˵��")==0 || results.get(0).text.compareTo("������")==0 ||
    		    		results.get(0).text.compareTo("��Ϣ")==0 || results.get(0).text.compareTo("ͨѶ¼")==0 ||
    		    		results.get(0).text.compareTo("���ܰ�")==0 || results.get(0).text.compareTo("����Ŷ")==0 ||
    		    		results.get(0).text.compareTo("����")==0 || results.get(0).text.compareTo("��ϵ��")==0 ||
    		    		results.get(0).text.compareTo("���š�")==0 || results.get(0).text.compareTo("����")==0 ||
    		    		results.get(0).text.compareTo("������")==0 || results.get(0).text.compareTo("��ɫ")==0 ||
    		    		results.get(0).text.compareTo("����")==0 || results.get(0).text.compareTo("��ѵ��")==0 ||
    		    		results.get(0).text.compareTo("������")==0 || results.get(0).text.compareTo("��")==0 ||
    		    		results.get(0).text.compareTo("ѱ¹")==0 || results.get(0).text.compareTo("��Ϭ")==0 ||
    		    		results.get(0).text.compareTo("����")==0 || results.get(0).text.compareTo("���ԡ�")==0 ||
    		    		results.get(0).text.compareTo("����")==0 || results.get(0).text.compareTo("��Ϭ")==0 ||
    		    		results.get(0).text.compareTo("��¼")==0 || results.get(0).text.compareTo("�����ˡ�")==0 ||
    		    		results.get(0).text.compareTo("��˵ ��")==0 || results.get(0).text.compareTo("������")==0 ||
    		    		results.get(0).text.compareTo("����Ŷ")==0 || results.get(0).text.compareTo("����Ŷ")==0 ||
    		    		results.get(0).text.compareTo("��¼")==0 || results.get(0).text.compareTo("�����ˡ�")==0 ||
    		    		results.get(0).text.compareTo("������")==0 || results.get(0).text.compareTo("����")==0 ||
    		    		results.get(0).text.compareTo("��˵")==0 || results.get(0).text.compareTo("������")==0 ||
    		    		results.get(0).text.compareTo("����")==0 || results.get(0).text.compareTo("ͨѶ¼��")==0 ||
    		    		results.get(0).text.compareTo("����Ŷ")==0 || results.get(0).text.compareTo("����")==0 || 
    		    		results.get(0).text.compareTo("��Ϣ��")==0 || results.get(0).text.compareTo("��ϵ�ˡ�")==0){
    		    	if(Screen_Server.flag) {
    					//��������
    		    		Screen_Server.wakeLock = Screen_Server.pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
    		    		Screen_Server.wakeLock.acquire();
    					//��ʼ��������������������⿪������
    					Screen_Server.mKeyguardLock = Screen_Server.mKeyguardManager.newKeyguardLock(""); 
    					//������ʾ��������
    					//Screen_Server.wakeLock.release();
    					Screen_Server.mKeyguardLock.disableKeyguard(); 
    					Screen_Server.flag=false;
    					if(!(results.get(0).text.compareTo("����")==0 || results.get(0).text.compareTo("���ԡ�")==0 ||
    							results.get(0).text.compareTo("����")==0 || results.get(0).text.compareTo("��Ϭ")==0 ||
    							results.get(0).text.compareTo("��Ϣ")==0 || results.get(0).text.compareTo("ͨѶ¼")==0 ||
    							results.get(0).text.compareTo("ͨѶ¼��")==0  || results.get(0).text.compareTo("����")==0||
    							results.get(0).text.compareTo("��ϵ��")==0 || results.get(0).text.compareTo("���š�")==0 ||
    							results.get(0).text.compareTo("����")==0 || results.get(0).text.compareTo("ͨѶ¼��")==0 ||
    							results.get(0).text.compareTo("��ѵ��")==0 || results.get(0).text.compareTo("����")==0 ||
    							results.get(0).text.compareTo("ѱ¹")==0 || results.get(0).text.compareTo("��Ϣ��")==0 || 
    							results.get(0).text.compareTo("��ϵ�ˡ�")==0)){
    						Intent home = new Intent(Intent.ACTION_MAIN);  
        	    		    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	    		    home.addCategory(Intent.CATEGORY_HOME);  
        	    		    startActivity(home);
    					}
    				}
    		    	
    		    	//��Ϣ���е���
    		    	if(results.get(0).text.compareTo("����")==0 || results.get(0).text.compareTo("���ԡ�")==0 ||
    		    			results.get(0).text.compareTo("����")==0 || results.get(0).text.compareTo("��Ϭ")==0 ||
    		    			results.get(0).text.compareTo("��Ϣ")==0 || results.get(0).text.compareTo("����")==0  ||
    		    			results.get(0).text.compareTo("����")==0 || results.get(0).text.compareTo("���š�")==0 || 
    		    			results.get(0).text.compareTo("����")==0 || results.get(0).text.compareTo("��Ϣ��")==0){
        		    	Intent intent = new Intent(Intent.ACTION_MAIN);
        		    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		    	intent.setType("vnd.android-dir/mms-sms");
        		    	startActivity(intent);
    		    	}
    		    	
    		    	//ͨѶ¼���е���
    		    	if(results.get(0).text.compareTo("ͨѶ¼")==0 || results.get(0).text.compareTo("ͨѶ¼��")==0||
    		    			results.get(0).text.compareTo("��ϵ��")==0 || results.get(0).text.compareTo("��ѵ��")==0 ||
    		    			results.get(0).text.compareTo("ѱ¹")==0 || results.get(0).text.compareTo("��ϵ�ˡ�")==0){
    		            Intent intent1 = new Intent(Intent.ACTION_MAIN);
        		    	intent1.setClass(ScreenObserverActivity.this, CallActivity.class);
    		    		intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		    		startActivity(intent1);
    		    	}
    		    }
    		    onDestroy();
    	    }
    	    // �Ự�����ص��ӿ�.
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