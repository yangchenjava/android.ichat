package com.yangc.ichat.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.text.TextUtils;

public class Md5Utils {

	/**
	 * 默认的密码字符串组合，apache校验下载的文件的正确性用的就是默认的这个组合
	 */
	private static final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

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
			if (digest != null) {
				byte[] bytes = str.getBytes();
				byte[] results = digest.digest(bytes);
				StringBuilder sb = new StringBuilder();
				for (byte result : results) {
					// 将byte数组转化为16进制字符存入StringBuilder中
					sb.append(String.format("%02x", result));
				}
				return sb.toString();
			}
		}
		return null;
	}

	/**
	 * @功能: 适用于小文件,不要大于512M
	 * @作者: yangc
	 * @创建日期: 2013-11-19 下午03:25:30
	 * @param file
	 * @return 返回文件的MD5码
	 */
	public static String getMD5String(File file) {
		if (file != null) {
			MessageDigest digest = null;
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				System.err.println(Md5Utils.class.getName() + "初始化失败, MessageDigest不支持Md5Utils");
				e.printStackTrace();
			}
			if (digest != null) {
				int len = -1;
				byte[] buffer = new byte[8192];
				BufferedInputStream bis = null;
				try {
					bis = new BufferedInputStream(new FileInputStream(file));
					while ((len = bis.read(buffer)) != -1) {
						digest.update(buffer, 0, len);
					}
					bis.close();
					bis = null;
					return bufferToHex(digest.digest());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (bis != null) bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	private static String bufferToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(2 * bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			appendHexPair(bytes[i], sb);
		}
		return sb.toString();
	}

	private static void appendHexPair(byte b, StringBuilder sb) {
		char c0 = hexDigits[(b & 0xf0) >> 4];
		char c1 = hexDigits[b & 0xf];
		sb.append(c0).append(c1);
	}

	public static String getMD5String(String str) {
		if (!TextUtils.isEmpty(str)) {
			return getMD5String(str.getBytes());
		}
		return null;
	}

	public static String getMD5String(byte[] bytes) {
		if (bytes != null && bytes.length != 0) {
			MessageDigest digest = null;
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				System.err.println(Md5Utils.class.getName() + "初始化失败, MessageDigest不支持Md5Utils");
				e.printStackTrace();
			}
			if (digest != null) {
				digest.update(bytes);
				return bufferToHex(digest.digest());
			}
		}
		return null;
	}

}
