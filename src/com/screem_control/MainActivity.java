package com.screem_control;

import com.example.screem_control.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	public TextView tv;
	Button stop_bt;
	Intent intent=new Intent();
//	boolean flag=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		if(com.screem_control.Screen_Server.flag1){
			Intent i=new Intent();
			i.addCategory(Intent.ACTION_MAIN);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(MainActivity.this, ScreenObserverActivity.class);
			startActivity(intent);
		}
		stop_bt=(Button)findViewById(R.id.button2);
		stop_bt.setOnClickListener(new StopService());
		intent.setClass(MainActivity.this, Screen_Server.class);
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    
    class StopService implements OnClickListener
    {
		@Override
		public void onClick(View arg0) {			
			stopService(intent);
			System.exit(0);
		}
    }
}
