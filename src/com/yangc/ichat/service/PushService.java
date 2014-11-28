package com.yangc.ichat.service;

import java.util.UUID;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.yangc.ichat.comm.Client;
import com.yangc.ichat.comm.bean.ChatBean;
import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.comm.bean.UserBean;
import com.yangc.ichat.utils.Constants;

public class PushService extends Service {

	private Client client;

	@Override
	public void onCreate() {
		this.client = Client.getInstance();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			int action = intent.getIntExtra(Constants.EXTRA_ACTION, -1);
			switch (action) {
			case Constants.ACTION_DESTROY:
				this.stopSelf();
				android.os.Process.killProcess(android.os.Process.myPid());
				break;
			case Constants.ACTION_RECONNECT:
				this.client.reconnect();
				break;
			case Constants.ACTION_LOGIN:
				UserBean user = new UserBean();
				user.setUuid(UUID.randomUUID().toString());
				user.setUsername(Constants.USERNAME);
				user.setPassword(Constants.PASSWORD);
				this.client.login(user);
				break;
			case Constants.ACTION_CHAT:
				this.client.sendChat((ChatBean) intent.getSerializableExtra(Constants.EXTRA_CHAT));
				break;
			case Constants.ACTION_FILE:
				break;
			case Constants.ACTION_RESULT:
				this.client.sendResult((ResultBean) intent.getSerializableExtra(Constants.EXTRA_RESULT));
				break;
			default:
				break;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		if (this.client != null) {
			this.client.destroy();
			this.client = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
