package com.yangc.ichat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.yangc.ichat.R;
import com.yangc.ichat.utils.Constants;

public class BootloaderActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Constants.IS_STARTUP) {
			this.startup();
		} else {
			this.setContentView(R.layout.activity_bootloader);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Constants.IS_STARTUP = true;
					startup();
				}
			}, 2000);
		}
	}

	private void startup() {
		SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.APP, Context.MODE_PRIVATE);
		if (TextUtils.isEmpty(sharedPreferences.getString("username", ""))) {
			this.startActivity(new Intent(this, AuthActivity.class));
		} else {
			this.startActivity(new Intent(this, MainActivity.class));
			// 启动TCP服务
		}
		this.finish();
	}

}
