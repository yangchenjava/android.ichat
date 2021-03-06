package com.yangc.ichat.http.volley;

import java.util.Map;

import android.text.TextUtils;
import android.util.Log;

public class CookieHelper {

	private static final String COOKIE_KEY = "Cookie";
	private static final String SET_COOKIE_KEY = "Set-Cookie";
	public static final String SESSION_COOKIE = "SSOcookie";

	public static String SESSION_ID;

	public static void addSessionCookie(Map<String, String> headers) {
		if (!TextUtils.isEmpty(SESSION_ID)) {
			StringBuilder sb = new StringBuilder();
			sb.append(SESSION_COOKIE).append("=").append(SESSION_ID);
			if (headers.containsKey(COOKIE_KEY)) {
				sb.append("; ").append(headers.get(COOKIE_KEY));
			}
			headers.put(COOKIE_KEY, sb.toString());
			Log.i("ichat", "addSessionCookie - " + sb.toString());
		}
	}

	public static void saveSessionCookie(Map<String, String> headers) {
		if (headers.containsKey(SET_COOKIE_KEY) && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
			SESSION_ID = headers.get(SET_COOKIE_KEY).split(";")[0].split("=")[1];
			Log.i("ichat", "saveSessionCookie - " + SESSION_ID);
		}
	}

}
