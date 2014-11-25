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
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.UILUtils;

public class AddressbookFragmentAdapter extends BaseAdapter {

	private static final int INDEX = 0;
	private static final int ITEM = 1;

	private int swipePosition = -1;

	private Context context;
	private ListView lvAddressbook;
	private List<TIchatAddressbook> list;
	private OnItemListener onItemListener;
	private int screenWidth;
	private DisplayImageOptions options = UILUtils.getDisplayImageOptions();

	public interface OnItemListener {
		public void onItemClick(int position);

		public void onItemLongClick(int position);

		public void onRemoveClick(int position);
	}

	public AddressbookFragmentAdapter(Context context, ListView lvAddressbook, List<TIchatAddressbook> list, OnItemListener onItemListener, int screenWidth) {
		this.context = context;
		this.lvAddressbook = lvAddressbook;
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
	public int getItemViewType(int position) {
		if (this.list.get(position).getId() == null) {
			return INDEX;
		} else {
			return ITEM;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	@SuppressLint("ClickableViewAccessibility")
	public View getView(final int position, View convertView, ViewGroup parent) {
		TIchatAddressbook addressbook = this.list.get(position);
		switch (this.getItemViewType(position)) {
		case INDEX: {
			IndexViewHolder viewHolder;
			if (convertView == null) {
				convertView = View.inflate(this.context, R.layout.fragment_tab_addressbook_index, null);
				viewHolder = new IndexViewHolder();
				viewHolder.tvAddressbookItemWord = (TextView) convertView.findViewById(R.id.tv_addressbook_item_word);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (IndexViewHolder) convertView.getTag();
			}

			viewHolder.tvAddressbookItemWord.setText(addressbook.getNickname());
			break;
		}
		case ITEM: {
			ItemViewHolder viewHolder;
			if (convertView == null) {
				convertView = View.inflate(this.context, R.layout.fragment_tab_addressbook_item, null);
				viewHolder = new ItemViewHolder();
				viewHolder.hsvAddressbookItem = (HorizontalScrollView) convertView.findViewById(R.id.hsv_addressbook_item);
				viewHolder.rlAddressbookItemLeft = (RelativeLayout) convertView.findViewById(R.id.rl_addressbook_item_left);
				viewHolder.rlAddressbookItemLeft.getLayoutParams().width = this.screenWidth;
				viewHolder.ivAddressbookItemPhoto = (ImageView) convertView.findViewById(R.id.iv_addressbook_item_photo);
				viewHolder.tvAddressbookItemNickname = (TextView) convertView.findViewById(R.id.tv_addressbook_item_nickname);
				viewHolder.tvAddressbookItemSignature = (TextView) convertView.findViewById(R.id.tv_addressbook_item_signature);
				viewHolder.llAddressbookItemRight = (LinearLayout) convertView.findViewById(R.id.ll_addressbook_item_right);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ItemViewHolder) convertView.getTag();
			}

			convertView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						ItemViewHolder holder = (ItemViewHolder) v.getTag();
						int scrollX = holder.hsvAddressbookItem.getScrollX();
						int width = holder.llAddressbookItemRight.getWidth();
						// 滑动长度大于控件的1/2则显示,否则复原
						if (scrollX > width / 2) {
							holder.hsvAddressbookItem.smoothScrollTo(width, 0);
							if (swipePosition != -1 && swipePosition != position) {
								View view = lvAddressbook.getChildAt(swipePosition - lvAddressbook.getFirstVisiblePosition());
								if (view != null) {
									((ItemViewHolder) view.getTag()).hsvAddressbookItem.smoothScrollTo(0, 0);
								}
							}
							swipePosition = position;
						} else {
							holder.hsvAddressbookItem.smoothScrollTo(0, 0);
						}
						return true;
					}
					return false;
				}
			});

			viewHolder.rlAddressbookItemLeft.setOnTouchListener(new View.OnTouchListener() {
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
			viewHolder.rlAddressbookItemLeft.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (swipePosition == position) {
						closeSwipe();
					} else {
						onItemListener.onItemClick(position);
					}
				}
			});
			viewHolder.rlAddressbookItemLeft.setOnLongClickListener(new View.OnLongClickListener() {
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
			if (TextUtils.isEmpty(addressbook.getPhoto())) {
				viewHolder.ivAddressbookItemPhoto.setImageResource(R.drawable.me_info);
			} else {
				ImageLoader.getInstance().displayImage(Constants.SERVER_URL + addressbook.getPhoto(), viewHolder.ivAddressbookItemPhoto, this.options);
			}
			viewHolder.tvAddressbookItemNickname.setText(addressbook.getNickname());
			if (TextUtils.isEmpty(addressbook.getSignature())) {
				viewHolder.tvAddressbookItemSignature.setVisibility(View.GONE);
			} else {
				viewHolder.tvAddressbookItemSignature.setVisibility(View.VISIBLE);
				viewHolder.tvAddressbookItemSignature.setText(addressbook.getSignature());
			}
			viewHolder.llAddressbookItemRight.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					closeSwipe();
					onItemListener.onRemoveClick(position);
				}
			});
			break;
		}
		}
		return convertView;
	}

	private void closeSwipe() {
		View view = this.lvAddressbook.getChildAt(this.swipePosition - this.lvAddressbook.getFirstVisiblePosition());
		if (view != null) {
			((ItemViewHolder) view.getTag()).hsvAddressbookItem.smoothScrollTo(0, 0);
			this.swipePosition = -1;
		}
	}

	private class IndexViewHolder {
		TextView tvAddressbookItemWord;
	}

	private class ItemViewHolder {
		HorizontalScrollView hsvAddressbookItem;
		RelativeLayout rlAddressbookItemLeft;
		ImageView ivAddressbookItemPhoto;
		TextView tvAddressbookItemNickname;
		TextView tvAddressbookItemSignature;
		LinearLayout llAddressbookItemRight;
	}

}
