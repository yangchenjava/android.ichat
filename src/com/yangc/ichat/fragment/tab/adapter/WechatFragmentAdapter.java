package com.yangc.ichat.fragment.tab.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yangc.ichat.R;
import com.yangc.ichat.database.bean.TIchatAddressbook;
import com.yangc.ichat.database.bean.TIchatHistory;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.utils.EmojiUtils;
import com.yangc.ichat.utils.UILUtils;

public class WechatFragmentAdapter extends BaseAdapter {

	private int swipePosition = -1;

	private Context context;
	private ListView lvWechat;
	private List<TIchatHistory> list;
	private OnItemListener onItemListener;
	private int screenWidth;
	private DisplayImageOptions options = UILUtils.getDisplayImageOptions();

	public interface OnItemListener {
		public void onItemClick(int position);

		public void onItemLongClick(int position);

		public void onRemoveClick(int position);
	}

	public WechatFragmentAdapter(Context context, ListView lvWechat, List<TIchatHistory> list, OnItemListener onItemListener, int screenWidth) {
		this.context = context;
		this.lvWechat = lvWechat;
		this.list = list;
		this.onItemListener = onItemListener;
		this.screenWidth = screenWidth;
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
	@SuppressLint("ClickableViewAccessibility")
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(this.context, R.layout.fragment_tab_wechat_item, null);
			viewHolder = new ViewHolder();
			viewHolder.hsvWechatItem = (HorizontalScrollView) convertView.findViewById(R.id.hsv_wechat_item);
			viewHolder.rlWechatItemLeft = (RelativeLayout) convertView.findViewById(R.id.rl_wechat_item_left);
			viewHolder.rlWechatItemLeft.getLayoutParams().width = this.screenWidth;
			viewHolder.ivWechatItemPhoto = (ImageView) convertView.findViewById(R.id.iv_wechat_item_photo);
			viewHolder.tvWechatItemNickname = (TextView) convertView.findViewById(R.id.tv_wechat_item_nickname);
			viewHolder.ivWechatItemStatus = (ImageView) convertView.findViewById(R.id.iv_wechat_item_status);
			viewHolder.tvWechatItemChat = (TextView) convertView.findViewById(R.id.tv_wechat_item_chat);
			viewHolder.tvWechatItemTime = (TextView) convertView.findViewById(R.id.tv_wechat_item_time);
			viewHolder.llWechatItemRight = (LinearLayout) convertView.findViewById(R.id.ll_wechat_item_right);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		convertView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					ViewHolder holder = (ViewHolder) v.getTag();
					int scrollX = holder.hsvWechatItem.getScrollX();
					int width = holder.llWechatItemRight.getWidth();
					// 滑动长度大于控件的1/2则显示,否则复原
					if (scrollX > width / 2) {
						holder.hsvWechatItem.smoothScrollTo(width, 0);
						if (swipePosition != -1 && swipePosition != position) {
							View view = lvWechat.getChildAt(swipePosition - lvWechat.getFirstVisiblePosition());
							if (view != null) {
								((ViewHolder) view.getTag()).hsvWechatItem.smoothScrollTo(0, 0);
							}
						}
						swipePosition = position;
					} else {
						holder.hsvWechatItem.smoothScrollTo(0, 0);
					}
					return true;
				}
				return false;
			}
		});

		viewHolder.rlWechatItemLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (swipePosition != -1 && swipePosition != position) {
						closeSwipe();
					}
				}
				return false;
			}
		});
		viewHolder.rlWechatItemLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (swipePosition == position) {
					closeSwipe();
				} else {
					onItemListener.onItemClick(position);
				}
			}
		});
		viewHolder.rlWechatItemLeft.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (swipePosition == position) {
					closeSwipe();
				} else {
					onItemListener.onItemLongClick(position);
				}
				return true;
			}
		});

		TIchatHistory history = this.list.get(position);
		TIchatAddressbook addressbook = DatabaseUtils.getAddressbookByUsername(this.context, history.getUsername());
		if (addressbook == null || TextUtils.isEmpty(addressbook.getPhoto())) {
			viewHolder.ivWechatItemPhoto.setImageResource(R.drawable.me_info);
		} else {
			ImageLoader.getInstance().displayImage(Constants.SERVER_URL + addressbook.getPhoto(), viewHolder.ivWechatItemPhoto, this.options);
		}
		if (addressbook != null && !TextUtils.isEmpty(addressbook.getNickname())) {
			viewHolder.tvWechatItemNickname.setText(addressbook.getNickname());
		}
		long current = System.currentTimeMillis();
		if (history.getTransmitStatus() == 0 && current - history.getDate().getTime() <= 8000) {
			viewHolder.ivWechatItemStatus.setVisibility(View.VISIBLE);
			viewHolder.ivWechatItemStatus.setImageResource(R.drawable.chat_status_sending_1);
		} else if (history.getTransmitStatus() == 1 || (history.getTransmitStatus() == 0 && current - history.getDate().getTime() > 8000)) {
			viewHolder.ivWechatItemStatus.setVisibility(View.VISIBLE);
			viewHolder.ivWechatItemStatus.setImageResource(R.drawable.chat_status_unsend_1);
		} else {
			viewHolder.ivWechatItemStatus.setVisibility(View.GONE);
		}
		viewHolder.tvWechatItemChat.setText(EmojiUtils.escapeEmoji(this.context, history.getChat(), 15));
		viewHolder.tvWechatItemTime.setText(AndroidUtils.getLocalDate(this.context, history.getDate()));
		viewHolder.llWechatItemRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeSwipe();
				onItemListener.onRemoveClick(position);
			}
		});
		return convertView;
	}

	private void closeSwipe() {
		View view = this.lvWechat.getChildAt(this.swipePosition - this.lvWechat.getFirstVisiblePosition());
		if (view != null) {
			((ViewHolder) view.getTag()).hsvWechatItem.smoothScrollTo(0, 0);
			this.swipePosition = -1;
		}
	}

	private class ViewHolder {
		HorizontalScrollView hsvWechatItem;
		RelativeLayout rlWechatItemLeft;
		ImageView ivWechatItemPhoto;
		TextView tvWechatItemNickname;
		ImageView ivWechatItemStatus;
		TextView tvWechatItemChat;
		TextView tvWechatItemTime;
		LinearLayout llWechatItemRight;
	}

}
