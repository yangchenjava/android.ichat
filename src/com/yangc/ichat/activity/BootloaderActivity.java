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
import com.yangc.ichat.utils.VolleyUtils;

public class BootloaderActivity extends Activity {

	private static final String TAG = BootloaderActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_bootloader);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startup();
			}
		}, 2000);
	}

	@Override
	protected void onStop() {
		super.onStop();
		VolleyUtils.cancelAllRequest(TAG);
	}

	private void startup() {
		// 模式: Context.MODE_PRIVATE只有本应用可以使用, Context.MODE_WORLD_READABLE其他应用可以读, Context.MODE_WORLD_WRITEABLE其他应用可以写
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
