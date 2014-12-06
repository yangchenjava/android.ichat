package com.yangc.ichat.activity.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yangc.ichat.R;
import com.yangc.ichat.database.bean.TIchatHistory;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.EmojiUtils;
import com.yangc.ichat.utils.UILUtils;

public class ChatActivityChatAdapter extends BaseAdapter {

	private static final int RECEIVE = 0;
	private static final int SEND = 1;

	private Context context;
	private List<TIchatHistory> list;
	private String mePhoto;
	private String friendPhoto;
	private DisplayImageOptions options = UILUtils.getDisplayImageOptions();

	public ChatActivityChatAdapter(Context context, List<TIchatHistory> list, String mePhoto, String friendPhoto) {
		this.context = context;
		this.list = list;
		this.mePhoto = mePhoto;
		this.friendPhoto = friendPhoto;
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
	public int getItemViewType(int position) {
		if (this.list.get(position).getChatStatus() == 0) {
			return RECEIVE;
		} else {
			return SEND;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TIchatHistory history = this.list.get(position);
		switch (this.getItemViewType(position)) {
		case RECEIVE: {
			ReceiveViewHolder viewHolder;
			if (convertView == null) {
				convertView = View.inflate(this.context, R.layout.activity_chat_receive, null);
				viewHolder = new ReceiveViewHolder();
				viewHolder.tvChatReceiveTime = (TextView) convertView.findViewById(R.id.tv_chat_receive_time);
				viewHolder.ivChatReceivePhoto = (ImageView) convertView.findViewById(R.id.iv_chat_receive_photo);
				viewHolder.tvChatReceive = (TextView) convertView.findViewById(R.id.tv_chat_receive);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ReceiveViewHolder) convertView.getTag();
			}

			if (position == 0 || history.getDate().getTime() - this.list.get(position - 1).getDate().getTime() > 120000) {
				viewHolder.tvChatReceiveTime.setVisibility(View.VISIBLE);
				viewHolder.tvChatReceiveTime.setText(DateFormat.format("akk:mm", history.getDate()));
			} else {
				viewHolder.tvChatReceiveTime.setVisibility(View.GONE);
			}
			if (TextUtils.isEmpty(this.friendPhoto)) {
				viewHolder.ivChatReceivePhoto.setImageResource(R.drawable.me_info);
			} else {
				ImageLoader.getInstance().displayImage(Constants.SERVER_URL + this.friendPhoto, viewHolder.ivChatReceivePhoto, this.options);
			}
			viewHolder.tvChatReceive.setText(EmojiUtils.escapeEmoji(this.context, history.getChat()));
			break;
		}
		case SEND: {
			SendViewHolder viewHolder;
			if (convertView == null) {
				convertView = View.inflate(this.context, R.layout.activity_chat_send, null);
				viewHolder = new SendViewHolder();
				viewHolder.tvChatSendTime = (TextView) convertView.findViewById(R.id.tv_chat_send_time);
				viewHolder.ivChatSendPhoto = (ImageView) convertView.findViewById(R.id.iv_chat_send_photo);
				viewHolder.tvChatSend = (TextView) convertView.findViewById(R.id.tv_chat_send);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (SendViewHolder) convertView.getTag();
			}

			if (position == 0 || history.getDate().getTime() - this.list.get(position - 1).getDate().getTime() > 120000) {
				viewHolder.tvChatSendTime.setVisibility(View.VISIBLE);
				viewHolder.tvChatSendTime.setText(DateFormat.format("akk:mm", history.getDate()));
			} else {
				viewHolder.tvChatSendTime.setVisibility(View.GONE);
			}
			if (TextUtils.isEmpty(this.mePhoto)) {
				viewHolder.ivChatSendPhoto.setImageResource(R.drawable.me_info);
			} else {
				ImageLoader.getInstance().displayImage(Constants.SERVER_URL + this.mePhoto, viewHolder.ivChatSendPhoto, this.options);
			}
			viewHolder.tvChatSend.setText(EmojiUtils.escapeEmoji(this.context, history.getChat()));
			break;
		}
		}
		return convertView;
	}

	private class ReceiveViewHolder {
		TextView tvChatReceiveTime;
		ImageView ivChatReceivePhoto;
		TextView tvChatReceive;
	}

	private class SendViewHolder {
		TextView tvChatSendTime;
		ImageView ivChatSendPhoto;
		TextView tvChatSend;
	}

}
