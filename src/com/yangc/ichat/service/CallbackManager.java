package com.yangc.ichat.service;

import java.util.ArrayList;
import java.util.List;

import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.database.bean.TIchatHistory;

public class CallbackManager {

	private static final List<OnChatListener> LISTENERS = new ArrayList<OnChatListener>();

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
		String name = listener.getClass().getName();
		for (int i = 0; i < LISTENERS.size(); i++) {
			if (LISTENERS.get(i).getClass().getName().equals(name)) {
				LISTENERS.remove(i);
				break;
			}
		}
	}

	public static List<OnChatListener> getChatListeners() {
		return new ArrayList<OnChatListener>(LISTENERS);
	}

}
