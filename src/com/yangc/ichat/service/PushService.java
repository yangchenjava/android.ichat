package com.yangc.ichat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PushService extends Service {

	@Override
	public void onCreate() {

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
