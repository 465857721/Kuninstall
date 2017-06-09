package com.kt.kuninstall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.kingsoft.kuninstall.R;
import com.umeng.analytics.MobclickAgent;

public class LoadingActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent i = new Intent(LoadingActivity.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		}, 2000);
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		MobclickAgent.onResume(this);
		super.onResume();
	}
}
