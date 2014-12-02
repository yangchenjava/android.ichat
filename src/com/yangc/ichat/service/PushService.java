package com.yangc.ichat.service;

import java.util.Date;
import java.util.UUID;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.yangc.ichat.R;
import com.yangc.ichat.activity.ChatActivity;
import com.yangc.ichat.comm.Client;
import com.yangc.ichat.comm.bean.ChatBean;
import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.comm.bean.UserBean;
import com.yangc.ichat.database.bean.TIchatHistory;
import com.yangc.ichat.receiver.KeepAliveReceiver;
import com.yangc.ichat.service.CallbackManager.OnChatListener;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;

public class PushService extends Service {

	private static final String TAG = PushService.class.getName();

	private Client client;

	private PendingIntent pendingIntent;
	private AlarmManager alarmManager;
	private NotificationManager notificationManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		this.client = Client.getInstance(this);
		this.registerReceiver(this.networkChangedReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		this.pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, KeepAliveReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);
		this.alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		this.alarmManager.setRepeating(AlarmManager.RTC, 30000 + System.currentTimeMillis(), 30000, this.pendingIntent);
		this.notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			int action = intent.getIntExtra(Constants.EXTRA_ACTION, -1);
			switch (action) {
			case Constants.ACTION_DESTROY: {
				this.stopSelf();
				android.os.Process.killProcess(android.os.Process.myPid());
				break;
			}
			case Constants.ACTION_CONNECT: {
				this.client.connect();
				break;
			}
			case Constants.ACTION_RECONNECT: {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						client.reconnect();
						client.login(getUser());
					}
				}, 5000);
				break;
			}
			case Constants.ACTION_NETWORK_ERROR: {
				for (OnChatListener listener : CallbackManager.getChatListeners()) {
					listener.onNetworkError();
				}
				break;
			}
			case Constants.ACTION_CANCEL_NOTIFICATION: {
				this.cancelNotification();
				break;
			}
			case Constants.ACTION_LOGIN: {
				this.client.login(this.getUser());
				break;
			}
			case Constants.ACTION_SEND_CHAT: {
				this.client.sendChat((ChatBean) intent.getSerializableExtra(Constants.EXTRA_CHAT));
				break;
			}
			case Constants.ACTION_SEND_FILE: {
				break;
			}
			case Constants.ACTION_SEND_HEART: {
				this.client.sendHeart();
				break;
			}
			case Constants.ACTION_RECEIVE_CHAT: {
				ChatBean chat = (ChatBean) intent.getSerializableExtra(Constants.EXTRA_CHAT);

				ResultBean result = new ResultBean();
				result.setUuid(chat.getUuid());
				result.setSuccess(true);
				result.setData("success");
				this.client.sendResult(result);

				boolean isChatActivity = AndroidUtils.getRunningActivityName(this).equals(ChatActivity.class.getSimpleName());

				TIchatHistory history = new TIchatHistory();
				history.setUuid(chat.getUuid());
				history.setUsername(chat.getFrom());
				history.setChat(chat.getData());
				history.setChatStatus(0L);
				history.setTransmitStatus(isChatActivity ? 4L : 3L);
				history.setDate(new Date());
				DatabaseUtils.saveHistory(this, history);

				for (OnChatListener listener : CallbackManager.getChatListeners()) {
					listener.onChatReceived(history);
				}

				if (!isChatActivity) this.showNotification(chat.getFrom(), DatabaseUtils.getAddressbookByUsername(this, chat.getFrom()).getNickname(), chat.getData());
				break;
			}
			case Constants.ACTION_RECEIVE_FILE: {
				break;
			}
			case Constants.ACTION_RECEIVE_RESULT: {
				ResultBean result = (ResultBean) intent.getSerializableExtra(Constants.EXTRA_RESULT);
				DatabaseUtils.updateHistoryByUuid(this, result.getUuid(), result.isSuccess() ? 2L : 1L);
				for (OnChatListener listener : CallbackManager.getChatListeners()) {
					listener.onResultReceived(result);
				}
				break;
			}
			default: {
				break;
			}
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
		this.unregisterReceiver(this.networkChangedReceiver);
		if (this.alarmManager != null) {
			this.alarmManager.cancel(this.pendingIntent);
			this.alarmManager = null;
			this.pendingIntent.cancel();
		}
		if (this.notificationManager != null) {
			this.notificationManager.cancelAll();
			this.notificationManager = null;
		}
	}

	private UserBean getUser() {
		UserBean user = new UserBean();
		user.setUuid(UUID.randomUUID().toString());
		user.setUsername(Constants.USERNAME);
		user.setPassword(Constants.PASSWORD);
		return user;
	}

	@SuppressWarnings("deprecation")
	private void showNotification(String username, String nickname, String content) {
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra("username", username);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

		Notification notification = new Notification(R.drawable.notification, nickname, System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.setLatestEventInfo(this, nickname, content, pendingIntent);

		this.notificationManager.notify(0, notification);
	}

	private void cancelNotification() {
		this.notificationManager.cancel(0);
	}

	private BroadcastReceiver networkChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				Log.i(TAG, "networkChangedReceiver");
				if (AndroidUtils.checkNetwork(context)) {
					client.reconnect();
					client.login(getUser());
				} else {
					client.destroy();
				}
			}
		}
	};

}
