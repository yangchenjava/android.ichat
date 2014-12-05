package com.yangc.ichat.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yangc.ichat.R;
import com.yangc.ichat.bean.EmojiBean;

public class ChatActivityEmojiAdapter extends BaseAdapter {

	private Context context;
	private List<EmojiBean> list;

	public ChatActivityEmojiAdapter(Context context, List<EmojiBean> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.list.get(position);
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

		viewHolder.ivChatEmoji.setImageResource(this.list.get(position).getResId());
		return convertView;
	}

	private class ViewHolder {
		ImageView ivChatEmoji;
	}

}
