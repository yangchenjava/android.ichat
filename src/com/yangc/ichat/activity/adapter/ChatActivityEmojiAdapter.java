package com.yangc.ichat.activity.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yangc.ichat.R;

public class ChatActivityEmojiAdapter extends BaseAdapter {

	private Context context;

	public ChatActivityEmojiAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return 0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.activity_chat_emoji_item, null);
			viewHolder = new ViewHolder();
			viewHolder.ivChatEmoji = (ImageView) convertView.findViewById(R.id.iv_chat_emoji);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		return convertView;
	}

	private class ViewHolder {
		ImageView ivChatEmoji;
	}

}
