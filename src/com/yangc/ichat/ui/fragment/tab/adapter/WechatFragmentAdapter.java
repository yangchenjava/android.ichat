package com.yangc.ichat.ui.fragment.tab.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class WechatFragmentAdapter extends RecyclerView.Adapter<WechatFragmentAdapter.ViewHolder> {

	private int swipePosition = -1;

	private Context context;
	private RecyclerView rvWechat;
	private List<TIchatHistory> list;
	private OnItemListener onItemListener;
	private int screenWidth;
	private DisplayImageOptions options = UILUtils.getDisplayImageOptions();

	public interface OnItemListener {
		public void onItemClick(int position);

		public void onItemLongClick(int position);

		public void onRemoveClick(int position);
	}

	public WechatFragmentAdapter(Context context, RecyclerView rvWechat, List<TIchatHistory> list, OnItemListener onItemListener, int screenWidth) {
		this.context = context;
		this.rvWechat = rvWechat;
		this.list = list;
		this.onItemListener = onItemListener;
		this.screenWidth = screenWidth;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_tab_wechat_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	@SuppressLint("ClickableViewAccessibility")
	public void onBindViewHolder(ViewHolder holder, final int position) {
		holder.hsvWechatItem.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					ViewHolder holder = (ViewHolder) rvWechat.getChildViewHolder(v);
					int scrollX = holder.hsvWechatItem.getScrollX();
					int width = holder.llWechatItemRight.getWidth();
					// 滑动长度大于控件的1/2则显示,否则复原
					if (scrollX > width / 2) {
						holder.hsvWechatItem.smoothScrollTo(width, 0);
						if (swipePosition != -1 && swipePosition != position) {
							View view = rvWechat.getChildAt(swipePosition - rvWechat.getChildAdapterPosition(rvWechat.getChildAt(0)));
							if (view != null) {
								((ViewHolder) rvWechat.getChildViewHolder(view)).hsvWechatItem.smoothScrollTo(0, 0);
							}
						}
						swipePosition = position;
					} else {
						holder.hsvWechatItem.smoothScrollTo(0, 0);
						swipePosition = -1;
					}
					return true;
				}
				return false;
			}
		});
		holder.rlWechatItemLeft.getLayoutParams().width = this.screenWidth;
		holder.rlWechatItemLeft.setOnTouchListener(new View.OnTouchListener() {
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
		holder.rlWechatItemLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (swipePosition == position) {
					closeSwipe();
				} else {
					onItemListener.onItemClick(position);
				}
			}
		});
		holder.rlWechatItemLeft.setOnLongClickListener(new View.OnLongClickListener() {
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
			holder.ivWechatItemPhoto.setImageResource(R.drawable.me_info);
		} else {
			ImageLoader.getInstance().displayImage(Constants.SERVER_URL + addressbook.getPhoto(), holder.ivWechatItemPhoto, this.options);
		}
		if (addressbook != null && !TextUtils.isEmpty(addressbook.getNickname())) {
			holder.tvWechatItemNickname.setText(addressbook.getNickname());
		}
		long current = System.currentTimeMillis();
		if (history.getTransmitStatus() == 0 && current - history.getDate().getTime() <= 8000) {
			holder.ivWechatItemStatus.setVisibility(View.VISIBLE);
			holder.ivWechatItemStatus.setImageResource(R.drawable.chat_status_sending_1);
		} else if (history.getTransmitStatus() == 1 || (history.getTransmitStatus() == 0 && current - history.getDate().getTime() > 8000)) {
			holder.ivWechatItemStatus.setVisibility(View.VISIBLE);
			holder.ivWechatItemStatus.setImageResource(R.drawable.chat_status_unsend_1);
		} else {
			holder.ivWechatItemStatus.setVisibility(View.GONE);
		}
		holder.tvWechatItemChat.setText(EmojiUtils.escapeEmoji(this.context, history.getChat(), 15));
		holder.tvWechatItemTime.setText(AndroidUtils.getLocalDate(this.context, history.getDate()));
		holder.llWechatItemRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeSwipe();
				onItemListener.onRemoveClick(position);
			}
		});
	}

	@Override
	public int getItemCount() {
		return this.list.size();
	}

	private void closeSwipe() {
		View view = this.rvWechat.getChildAt(this.swipePosition - this.rvWechat.getChildAdapterPosition(this.rvWechat.getChildAt(0)));
		if (view != null) {
			((ViewHolder) this.rvWechat.getChildViewHolder(view)).hsvWechatItem.smoothScrollTo(0, 0);
			this.swipePosition = -1;
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		HorizontalScrollView hsvWechatItem;
		RelativeLayout rlWechatItemLeft;
		ImageView ivWechatItemPhoto;
		TextView tvWechatItemNickname;
		ImageView ivWechatItemStatus;
		TextView tvWechatItemChat;
		TextView tvWechatItemTime;
		LinearLayout llWechatItemRight;

		public ViewHolder(View itemView) {
			super(itemView);
			this.hsvWechatItem = (HorizontalScrollView) itemView.findViewById(R.id.hsv_wechat_item);
			this.rlWechatItemLeft = (RelativeLayout) itemView.findViewById(R.id.rl_wechat_item_left);
			this.ivWechatItemPhoto = (ImageView) itemView.findViewById(R.id.iv_wechat_item_photo);
			this.tvWechatItemNickname = (TextView) itemView.findViewById(R.id.tv_wechat_item_nickname);
			this.ivWechatItemStatus = (ImageView) itemView.findViewById(R.id.iv_wechat_item_status);
			this.tvWechatItemChat = (TextView) itemView.findViewById(R.id.tv_wechat_item_chat);
			this.tvWechatItemTime = (TextView) itemView.findViewById(R.id.tv_wechat_item_time);
			this.llWechatItemRight = (LinearLayout) itemView.findViewById(R.id.ll_wechat_item_right);
		}
	}

}
