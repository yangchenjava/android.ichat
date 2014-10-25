package com.yangc.ichat.utils;

public class Constants {

	public static final String APP = "ichat";

	public static final String DEFAULT_CACHE_DIR = "portrait";

	public static String USER_ID;
	public static String USERNAME;
	public static String PASSWORD;

	/** 请求地址 */
	private static final String SERVER_URL = "http://192.168.1.4:8080/com.yangc.bridge";
	public static final String LOGIN = SERVER_URL + "/resource/interface/login";
	public static final String REGISTER = SERVER_URL + "/resource/interface/register";
	public static final String TEST = SERVER_URL + "/resource/interface/test";

	private Constants() {
	}

}
