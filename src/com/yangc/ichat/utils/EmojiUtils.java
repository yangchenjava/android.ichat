package com.yangc.ichat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.yangc.ichat.bean.EmojiBean;

public class EmojiUtils {

	public static final int PAGE_COUNT = 7;
	public static final int PAGE_SIZE = 20;

	private static final List<EmojiBean> EMOJI_LIST = new ArrayList<EmojiBean>();

	private static final Map<String, Integer> EMOJI_MAP = new HashMap<String, Integer>();

	private EmojiUtils() {
	}

	public static void loadEmoji(Context context) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(context.getAssets().open("emoji"), "UTF-8"));
			String emojiStr = null;
			while ((emojiStr = br.readLine()) != null) {
				String[] emojiArray = emojiStr.split(",");
				int resId = context.getResources().getIdentifier(emojiArray[0], "drawable", context.getPackageName());

				EMOJI_LIST.add(new EmojiBean(resId, emojiArray[0], emojiArray[1]));
				EMOJI_MAP.put(emojiArray[1], resId);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<EmojiBean> getEmojiList(int pageNum) {
		return new ArrayList<EmojiBean>(EMOJI_LIST.subList(pageNum * PAGE_SIZE, (pageNum + 1) * PAGE_SIZE));
	}

	public static int getEmojiMap(String content) {
		return EMOJI_MAP.get(content);
	}

}
