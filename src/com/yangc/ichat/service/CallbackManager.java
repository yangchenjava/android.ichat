package com.yangc.ichat.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.database.bean.TIchatHistory;

public class CallbackManager {

	private static final List<OnChatListener> LISTENERS = new CopyOnWriteArrayList<OnChatListener>();

	public interface OnChatListener {
		public void onChatReceived(TIchatHistory history);

		public void onResultReceived(ResultBean result);

		public void onNetworkError();
	}

	/**
	 * @功能: 注册消息监听
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午6:36:47
	 * @param listener
	 */
	public static void registerChatListener(OnChatListener listener) {
		LISTENERS.add(listener);
	}

	/**
	 * @功能: 注销消息监听
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午6:37:09
	 * @param listener
	 */
	public static void unregisterChatListener(OnChatListener listener) {
		LISTENERS.remove(listener);
	}

	public static void notifyChatReceived(TIchatHistory history) {
		for (OnChatListener listener : LISTENERS) {
			listener.onChatReceived(history);
		}
	}

	public static void notifyResultReceived(ResultBean result) {
		for (OnChatListener listener : LISTENERS) {
			listener.onResultReceived(result);
		}
	}

	public static void notifyNetworkError() {
		for (OnChatListener listener : LISTENERS) {
			listener.onNetworkError();
		}
	}

}
