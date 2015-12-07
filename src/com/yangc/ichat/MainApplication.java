package com.yangc.ichat;

import android.app.Application;

import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.PreferenceUtils;
import com.yangc.ichat.utils.UILUtils;
import com.yangc.ichat.utils.VolleyUtils;

public class MainApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		UILUtils.init(this);
		VolleyUtils.init(this);
		Constants.USER_ID = PreferenceUtils.getString(this, "userId", "");
		Constants.USERNAME = PreferenceUtils.getString(this, "username", "");
		Constants.PASSWORD = PreferenceUtils.getString(this, "password", "");
	}

}
