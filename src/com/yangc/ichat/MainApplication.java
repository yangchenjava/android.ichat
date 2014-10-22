package com.yangc.ichat;

import android.app.Application;

import com.yangc.ichat.utils.VolleyUtils;

public class MainApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		VolleyUtils.init(this);
	}

}
