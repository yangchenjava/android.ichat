package com.yangc.ichat.activity.adapter;

import java.io.File;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yangc.ichat.R;
import com.yangc.ichat.comm.bean.ChatBean;
import com.yangc.ichat.database.bean.TIchatHistory;
import com.yangc.ichat.service.PushService;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.utils.EmojiUtils;
import com.yangc.ichat.utils.UILUtils;
import com.yangc.ichat.utils.VoiceUtils;

public class ChatActivityChatAdapter extends BaseAdapter {

	private static final int RECEIVE = 0;
	private static final int SEND = 1;

	private Context context;
	private SoundPool soundPool;
	private int playCompleted;
	private List<TIchatHistory> list;
	private String username;
	private String mePhoto;
	private String friendPhoto;
	private DisplayImageOptions options = UILUtils.getDisplayImageOptions();

	private int playingPosition = -1;
	private String playingFileName = null;

	public ChatActivityChatAdapter(Context context, SoundPool soundPool, int playCompleted, List<TIchatHistory> list, String username, String mePhoto, String friendPhoto) {
		this.context = context;
		this.soundPool = soundPool;
		this.playCompleted = playCompleted;
		this.list = list;
		this.username = username;
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
				viewHolder.ivChatReceiveVoice = (ImageView) convertView.findViewById(R.id.iv_chat_receive_voice);
				viewHolder.tvChatReceiveVoiceDuration = (TextView) convertView.findViewById(R.id.tv_chat_receive_voice_duration);
				viewHolder.ivChatReceiveStatus = (ImageView) convertView.findViewById(R.id.iv_chat_receive_status);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ReceiveViewHolder) convertView.getTag();
			}

			// 两条消息间隔时间大于5分钟,则显示时间
			if (position == 0 || history.getDate().getTime() - this.list.get(position - 1).getDate().getTime() > 300000) {
				viewHolder.tvChatReceiveTime.setVisibility(View.VISIBLE);
				viewHolder.tvChatReceiveTime.setText(AndroidUtils.getLocalDate(this.context, history.getDate()));
			} else {
				viewHolder.tvChatReceiveTime.setVisibility(View.GONE);
			}
			if (TextUtils.isEmpty(this.friendPhoto)) {
				viewHolder.ivChatReceivePhoto.setImageResource(R.drawable.me_info);
			} else {
				ImageLoader.getInstance().displayImage(Constants.SERVER_URL + this.friendPhoto, viewHolder.ivChatReceivePhoto, this.options);
			}
			if (TextUtils.equals(history.getChat(), Constants.VOICE)) {
				String duration = history.getUuid().split("_")[1];
				viewHolder.tvChatReceive.getLayoutParams().width = AndroidUtils.dp2px(this.context, 80 + 2 * Integer.parseInt(duration));
				viewHolder.tvChatReceive.setText("");
				viewHolder.tvChatReceive.setOnClickListener(new PlayVoiceClickListener(position, history));
				viewHolder.ivChatReceiveVoice.setVisibility(View.VISIBLE);
				if (this.playingPosition == position) {
					viewHolder.ivChatReceiveVoice.setBackgroundResource(R.anim.frame_receive_voice);
					AnimationDrawable animationDrawable = (AnimationDrawable) viewHolder.ivChatReceiveVoice.getBackground();
					if (!animationDrawable.isRunning()) {
						animationDrawable.start();
					}
				} else {
					viewHolder.ivChatReceiveVoice.setBackgroundResource(R.drawable.chat_receive_voice);
				}
				viewHolder.tvChatReceiveVoiceDuration.setVisibility(View.VISIBLE);
				viewHolder.tvChatReceiveVoiceDuration.setText(duration + "″");
				viewHolder.ivChatReceiveStatus.setVisibility(history.getTransmitStatus().longValue() == 3L ? View.VISIBLE : View.GONE);
			} else {
				viewHolder.tvChatReceive.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
				viewHolder.tvChatReceive.setText(EmojiUtils.escapeEmoji(this.context, history.getChat(), 20));
				viewHolder.tvChatReceive.setOnClickListener(null);
				viewHolder.ivChatReceiveVoice.setVisibility(View.GONE);
				viewHolder.tvChatReceiveVoiceDuration.setVisibility(View.GONE);
				viewHolder.ivChatReceiveStatus.setVisibility(View.GONE);
			}
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
				viewHolder.ivChatSendVoice = (ImageView) convertView.findViewById(R.id.iv_chat_send_voice);
				viewHolder.tvChatSendVoiceDuration = (TextView) convertView.findViewById(R.id.tv_chat_send_voice_duration);
				viewHolder.ivChatSendStatus = (ImageView) convertView.findViewById(R.id.iv_chat_send_status);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (SendViewHolder) convertView.getTag();
			}

			// 两条消息间隔时间大于5分钟,则显示时间
			if (position == 0 || history.getDate().getTime() - this.list.get(position - 1).getDate().getTime() > 300000) {
				viewHolder.tvChatSendTime.setVisibility(View.VISIBLE);
				viewHolder.tvChatSendTime.setText(AndroidUtils.getLocalDate(this.context, history.getDate()));
			} else {
				viewHolder.tvChatSendTime.setVisibility(View.GONE);
			}
			if (TextUtils.isEmpty(this.mePhoto)) {
				viewHolder.ivChatSendPhoto.setImageResource(R.drawable.me_info);
			} else {
				ImageLoader.getInstance().displayImage(Constants.SERVER_URL + this.mePhoto, viewHolder.ivChatSendPhoto, this.options);
			}
			if (TextUtils.equals(history.getChat(), Constants.VOICE)) {
				String duration = history.getUuid().split("_")[1];
				viewHolder.tvChatSend.getLayoutParams().width = AndroidUtils.dp2px(this.context, 80 + 2 * Integer.parseInt(duration));
				viewHolder.tvChatSend.setText("");
				viewHolder.tvChatSend.setOnClickListener(new PlayVoiceClickListener(position, history));
				viewHolder.ivChatSendVoice.setVisibility(View.VISIBLE);
				if (this.playingPosition == position) {
					viewHolder.ivChatSendVoice.setBackgroundResource(R.anim.frame_send_voice);
					AnimationDrawable animationDrawable = (AnimationDrawable) viewHolder.ivChatSendVoice.getBackground();
					if (!animationDrawable.isRunning()) {
						animationDrawable.start();
					}
				} else {
					viewHolder.ivChatSendVoice.setBackgroundResource(R.drawable.chat_send_voice);
				}
				viewHolder.tvChatSendVoiceDuration.setVisibility(View.VISIBLE);
				viewHolder.tvChatSendVoiceDuration.setText(duration + "″");
			} else {
				viewHolder.tvChatSend.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
				viewHolder.tvChatSend.setText(EmojiUtils.escapeEmoji(this.context, history.getChat(), 20));
				viewHolder.tvChatSend.setOnClickListener(null);
				viewHolder.ivChatSendVoice.setVisibility(View.GONE);
				viewHolder.tvChatSendVoiceDuration.setVisibility(View.GONE);
			}
			long current = System.currentTimeMillis();
			if (history.getTransmitStatus() == 0 && current - history.getDate().getTime() <= 8000) {
				viewHolder.ivChatSendStatus.setVisibility(View.VISIBLE);
				viewHolder.ivChatSendStatus.setImageResource(R.drawable.chat_status_sending_2);
				viewHolder.ivChatSendStatus.startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.rotate_loading));
				viewHolder.ivChatSendStatus.setOnClickListener(null);
			} else if (history.getTransmitStatus() == 1 || (history.getTransmitStatus() == 0 && current - history.getDate().getTime() > 8000)) {
				viewHolder.ivChatSendStatus.setVisibility(View.VISIBLE);
				viewHolder.ivChatSendStatus.setImageResource(R.drawable.chat_status_unsend_2);
				viewHolder.ivChatSendStatus.clearAnimation();
				viewHolder.ivChatSendStatus.setOnClickListener(new ResendClickListener(history));
			} else {
				viewHolder.ivChatSendStatus.setVisibility(View.GONE);
				viewHolder.ivChatSendStatus.clearAnimation();
				viewHolder.ivChatSendStatus.setOnClickListener(null);
			}
			break;
		}
		}
		return convertView;
	}

	private class PlayVoiceClickListener implements View.OnClickListener {
		private int position;
		private TIchatHistory history;

		public PlayVoiceClickListener(int position, TIchatHistory history) {
			this.position = position;
			this.history = history;
		}

		@Override
		public void onClick(View v) {
			String fileName = this.history.getUuid().split("_")[0];

			VoiceUtils voice = VoiceUtils.getInstance();
			voice.stopPlay();
			if (voice.isPlaying() && TextUtils.equals(fileName, playingFileName)) {
				playingPosition = -1;
				playingFileName = null;
			} else {
				if (this.history.getTransmitStatus().longValue() == 3L) {
					this.history.setTransmitStatus(4L);
					DatabaseUtils.updateHistoryByUuid(context, this.history.getUuid(), 4L);
				}

				File dir = AndroidUtils.getStorageDir(context, Constants.APP + "/" + Constants.CACHE_VOICE + "/" + username);
				File file = new File(dir, fileName);
				voice.startPlay(file, new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						soundPool.play(playCompleted, 1, 1, 0, 0, 1);
						VoiceUtils.getInstance().stopPlay();
						playingPosition = -1;
						playingFileName = null;
						notifyDataSetChanged();
					}
				});
				playingPosition = this.position;
				playingFileName = fileName;
			}
			notifyDataSetChanged();
		}
	}

	// 重新发送消息
	private class ResendClickListener implements View.OnClickListener {
		private TIchatHistory history;

		public ResendClickListener(TIchatHistory history) {
			this.history = history;
		}

		@Override
		public void onClick(View v) {
			this.history.setTransmitStatus(0L);
			this.history.setDate(new Date());
			notifyDataSetChanged();

			ChatBean chat = new ChatBean();
			chat.setUuid(this.history.getUuid());
			chat.setFrom(Constants.USERNAME);
			chat.setTo(this.history.getUsername());
			chat.setData(this.history.getChat());

			Intent intent = new Intent(context, PushService.class);
			intent.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_SEND_CHAT);
			intent.putExtra(Constants.EXTRA_CHAT, chat);
			context.startService(intent);
		}
	};

	private class ReceiveViewHolder {
		TextView tvChatReceiveTime;
		ImageView ivChatReceivePhoto;
		TextView tvChatReceive;
		ImageView ivChatReceiveVoice;
		TextView tvChatReceiveVoiceDuration;
		ImageView ivChatReceiveStatus;
	}

	private class SendViewHolder {
		TextView tvChatSendTime;
		ImageView ivChatSendPhoto;
		TextView tvChatSend;
		ImageView ivChatSendVoice;
		TextView tvChatSendVoiceDuration;
		ImageView ivChatSendStatus;
	}

}
