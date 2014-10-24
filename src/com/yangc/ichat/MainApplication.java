package com.yangc.ichat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.VolleyUtils;

public class MainApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		VolleyUtils.init(this);
		// 模式: Context.MODE_PRIVATE只有本应用可以使用, Context.MODE_WORLD_READABLE其他应用可以读, Context.MODE_WORLD_WRITEABLE其他应用可以写
		SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.APP, Context.MODE_PRIVATE);
		Constants.USERNAME = sharedPreferences.getString("username", "");
		Constants.PASSWORD = sharedPreferences.getString("password", "");
	}

}
