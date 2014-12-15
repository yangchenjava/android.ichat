package com.yangc.ichat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.yangc.ichat.bean.EmojiBean;

public class EmojiUtils {

	public static final int PAGE_COUNT = 7;
	public static final int PAGE_SIZE = 20;

	private static final List<EmojiBean> EMOJI_LIST = new ArrayList<EmojiBean>();

	private static final Map<String, Integer> EMOJI_MAP = new HashMap<String, Integer>();

	private EmojiUtils() {
	}

	/**
	 * @功能: 加载表情到内存
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午7:09:06
	 * @param context
	 */
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

	/**
	 * @功能: 根据分页获取表情集合
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午7:09:29
	 * @param pageNum
	 * @return
	 */
	public static List<EmojiBean> getEmojiList(int pageNum) {
		return new ArrayList<EmojiBean>(EMOJI_LIST.subList(pageNum * PAGE_SIZE, (pageNum + 1) * PAGE_SIZE));
	}

	/**
	 * @功能: 根据内容获取表情资源id
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午8:15:03
	 * @param content
	 * @return
	 */
	public static Integer getEmojiResId(String content) {
		return EMOJI_MAP.get(content);
	}

	/**
	 * @功能: 转义带表情的内容
	 * @作者: yangc
	 * @创建日期: 2014年12月7日 下午7:10:09
	 * @param context
	 * @param source
	 * @return
	 */
	public static SpannableString escapeEmoji(Context context, String source, int size) {
		SpannableString spannableString = new SpannableString(source);
		Matcher matcher = Pattern.compile("\\[[\u4e00-\u9fa5]+?\\]").matcher(spannableString);
		while (matcher.find()) {
			Integer resId = EMOJI_MAP.get(matcher.group());
			if (resId != null) {
				Drawable drawable = context.getResources().getDrawable(resId);
				drawable.setBounds(0, 0, size, size);
				spannableString.setSpan(new ImageSpan(drawable), matcher.start(), matcher.end(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
			}
		}
		return spannableString;
	}
}
