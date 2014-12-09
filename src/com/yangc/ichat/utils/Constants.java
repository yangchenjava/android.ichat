package com.yangc.ichat.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Constants {

	public static final String APP = "ichat";

	public static final String CACHE_HTTP = "http";
	public static final String CACHE_PORTRAIT = "portrait";
	public static final String ORIGINAL_IMAGE = "_original";

	public static String USER_ID;
	public static String USERNAME;
	public static String PASSWORD;

	public static boolean IS_REFRESH_ADDRESSBOOK;
	public static String CHATTING_USERNAME = "";

	public static final String EXTRA_ACTION = "action";
	public static final String EXTRA_CHAT = "chat";
	public static final String EXTRA_FILE = "file";
	public static final String EXTRA_RESULT = "result";
	public static final int ACTION_DESTROY = 0;
	public static final int ACTION_CONNECT = 1;
	public static final int ACTION_RECONNECT = 2;
	public static final int ACTION_NETWORK_ERROR = 3;
	public static final int ACTION_CANCEL_NOTIFICATION = 4;
	public static final int ACTION_LOGIN = 10;
	public static final int ACTION_SEND_CHAT = 11;
	public static final int ACTION_SEND_FILE = 12;
	public static final int ACTION_RECEIVE_CHAT = 13;
	public static final int ACTION_RECEIVE_FILE = 14;
	public static final int ACTION_RECEIVE_RESULT = 15;

	/** TCP地址 */
	public static final String IP = "10.23.6.12";
	public static final int PORT = 8811;
	public static final int TIMEOUT = 60;
	public static final String CHARSET_NAME = "UTF-8";

	/** HTTP地址 */
	public static final String SERVER_URL = "http://10.23.6.12:81/com.yangc.bridge/";
	public static final String LOGIN = SERVER_URL + "resource/interface/login";
	public static final String REGISTER = SERVER_URL + "resource/interface/register";
	public static final String USER_INFO = SERVER_URL + "resource/interface/userInfo";
	public static final String UPDATE_PERSON = SERVER_URL + "resource/interface/updatePerson";
	public static final String UPDATE_PERSON_PHOTO = SERVER_URL + "resource/interface/updatePersonPhoto";
	public static final String FRIENDS = SERVER_URL + "resource/interface/friends";
	public static final String DELETE_FRIEND = SERVER_URL + "resource/interface/deleteFriend";
	public static final String TEST = SERVER_URL + "resource/interface/test";

	private Constants() {
	}

	public static void saveConstants(Context context, String userId, String username, String password) {
		SharedPreferences.Editor editor = context.getSharedPreferences(Constants.APP, Context.MODE_PRIVATE).edit();
		editor.putString("userId", userId).putString("username", username).putString("password", password).commit();
		USER_ID = userId;
		USERNAME = username;
		PASSWORD = password;
	}

}
