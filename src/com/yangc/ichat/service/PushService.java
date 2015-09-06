package com.yangc.ichat.service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.UUID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import com.yangc.ichat.R;
import com.yangc.ichat.comm.Client;
import com.yangc.ichat.comm.bean.ChatBean;
import com.yangc.ichat.comm.bean.FileBean;
import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.comm.bean.UserBean;
import com.yangc.ichat.database.bean.TIchatHistory;
import com.yangc.ichat.ui.activity.ChatActivity;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.utils.Md5Utils;

import de.greenrobot.event.EventBus;

public class PushService extends Service {

	private static final String TAG = PushService.class.getName();

	private Client client;

	private NotificationManager notificationManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		this.client = Client.getInstance(this);
		this.notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		this.registerReceiver(this.networkChangedReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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
				new Thread(this.reconnectAndLogin).start();
				break;
			}
			case Constants.ACTION_NETWORK_ERROR: {
				// CallbackManager.notifyNetworkError();
				EventBus.getDefault().post("");
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
				this.client.sendFile((FileBean) intent.getSerializableExtra(Constants.EXTRA_FILE));
				break;
			}
			case Constants.ACTION_RECEIVE_CHAT: {
				ChatBean chat = (ChatBean) intent.getSerializableExtra(Constants.EXTRA_CHAT);

				ResultBean result = new ResultBean();
				result.setUuid(chat.getUuid());
				result.setSuccess(true);
				result.setData("success");
				this.client.sendResult(result);

				// 判断当前activity是否为聊天页面
				boolean isChatActivity = AndroidUtils.getRunningActivityName(this).equals(ChatActivity.class.getName());

				TIchatHistory history = new TIchatHistory();
				history.setUuid(chat.getUuid());
				history.setUsername(chat.getFrom());
				history.setChat(chat.getData());
				// 0:文本,1:语音
				history.setType(0L);
				// 0:我接收的,1:我发送的
				history.setChatStatus(0L);
				// 0:发送中,1:发送失败,2:发送成功,3:未读,4:已读
				history.setTransmitStatus(isChatActivity ? 4L : 3L);
				history.setDate(new Date());
				DatabaseUtils.saveHistory(this, history);

				// CallbackManager.notifyChatReceived(history);
				EventBus.getDefault().post(history);

				if (!isChatActivity || !chat.getFrom().equals(Constants.CHATTING_USERNAME)) {
					this.showNotification(chat.getFrom(), DatabaseUtils.getAddressbookByUsername(this, chat.getFrom()).getNickname(), chat.getData());
				}
				break;
			}
			case Constants.ACTION_RECEIVE_FILE: {
				FileBean file = (FileBean) intent.getSerializableExtra(Constants.EXTRA_FILE);

				File dir = AndroidUtils.getStorageDir(this, Constants.APP + "/" + Constants.CACHE_VOICE + "/" + file.getFrom());
				File targetFile = new File(dir, file.getFileName());

				RandomAccessFile raf = null;
				try {
					if (!targetFile.exists() || !targetFile.isFile()) {
						targetFile.delete();
						targetFile.createNewFile();
					}

					raf = new RandomAccessFile(targetFile, "rw");
					raf.seek(raf.length());
					raf.write(file.getData(), 0, file.getOffset());
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (raf != null) raf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (targetFile.length() == file.getFileSize() && Md5Utils.getMD5String(targetFile).equals(file.getFileMd5())) {
					ResultBean result = new ResultBean();
					result.setUuid(file.getUuid());
					result.setSuccess(true);
					result.setData("success");
					this.client.sendResult(result);

					// 判断当前activity是否为聊天页面
					boolean isChatActivity = AndroidUtils.getRunningActivityName(this).equals(ChatActivity.class.getName());

					TIchatHistory history = new TIchatHistory();
					history.setUuid(file.getUuid());
					history.setUsername(file.getFrom());
					history.setChat(Constants.VOICE);
					// 0:文本,1:语音
					history.setType(1L);
					// 0:我接收的,1:我发送的
					history.setChatStatus(0L);
					// 0:发送中,1:发送失败,2:发送成功,3:未读,4:已读
					history.setTransmitStatus(3L);
					history.setDate(new Date());
					DatabaseUtils.saveHistory(this, history);

					// CallbackManager.notifyChatReceived(history);
					EventBus.getDefault().post(history);

					if (!isChatActivity || !file.getFrom().equals(Constants.CHATTING_USERNAME)) {
						this.showNotification(file.getFrom(), DatabaseUtils.getAddressbookByUsername(this, file.getFrom()).getNickname(), Constants.VOICE);
					}
				}
				break;
			}
			case Constants.ACTION_RECEIVE_RESULT: {
				ResultBean result = (ResultBean) intent.getSerializableExtra(Constants.EXTRA_RESULT);
				DatabaseUtils.updateHistoryByUuid(this, result.getUuid(), result.isSuccess() ? 2L : 1L);
				// CallbackManager.notifyResultReceived(result);
				EventBus.getDefault().post(result);
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
		this.unregisterReceiver(this.networkChangedReceiver);
		if (this.notificationManager != null) {
			this.notificationManager.cancelAll();
			this.notificationManager = null;
		}
		if (this.client != null) {
			this.client.destroy();
			this.client = null;
		}
	}

	private Runnable reconnectAndLogin = new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			client.reconnect();
			client.login(getUser());
		}
	};

	private UserBean getUser() {
		UserBean user = new UserBean();
		user.setUuid(UUID.randomUUID().toString());
		user.setUsername(Constants.USERNAME);
		user.setPassword(Constants.PASSWORD);
		return user;
	}

	/**
	 * @功能: 显示通知
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午7:06:44
	 * @param username
	 * @param nickname
	 * @param content
	 */
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

	/**
	 * @功能: 取消通知
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午7:06:59
	 */
	private void cancelNotification() {
		this.notificationManager.cancel(0);
	}

	/**
	 * 监听网络变化
	 */
	private BroadcastReceiver networkChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				Log.i(TAG, "networkChangedReceiver");
				if (AndroidUtils.checkNetwork(context)) {
					new Thread(reconnectAndLogin).start();
				} else {
					client.destroy();
					AndroidUtils.alertToast(context, R.string.error_network);
				}
			}
		}
	};

}
