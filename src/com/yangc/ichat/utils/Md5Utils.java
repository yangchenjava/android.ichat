package com.yangc.ichat.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.text.TextUtils;

public class Md5Utils {

	private Md5Utils() {
	}

	/**
	 * @功能: 向getMD5方法传入一个你需要转换的原始字符串,将返回字符串的MD5码
	 * @作者: yangc
	 * @创建日期: 2013-11-19 下午03:25:30
	 * @param str 原始字符串
	 * @return 返回字符串的MD5码
	 */
	public static String getMD5(String str) {
		if (!TextUtils.isEmpty(str)) {
			MessageDigest digest = null;
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				System.err.println(Md5Utils.class.getName() + "初始化失败, MessageDigest不支持Md5Utils");
				e.printStackTrace();
			}

			byte[] bytes = str.getBytes();
			byte[] results = digest.digest(bytes);
			StringBuilder sb = new StringBuilder();
			for (byte result : results) {
				// 将byte数组转化为16进制字符存入StringBuilder中
				sb.append(String.format("%02x", result));
			}
			return sb.toString();
		}
		return null;
	}

}
