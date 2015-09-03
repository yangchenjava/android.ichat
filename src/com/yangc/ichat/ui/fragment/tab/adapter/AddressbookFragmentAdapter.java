package com.yangc.ichat.ui.fragment.tab.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
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
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.UILUtils;

public class AddressbookFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int INDEX = 0;
	private static final int TOTAL = 1;
	private static final int ITEM = 2;

	private int swipePosition = -1;

	private RecyclerView rvAddressbook;
	private List<TIchatAddressbook> list;
	private OnItemListener onItemListener;
	private int screenWidth;
	private DisplayImageOptions options = UILUtils.getDisplayImageOptions();

	public interface OnItemListener {
		public void onItemClick(int position);

		public void onItemLongClick(int position);

		public void onRemoveClick(int position);
	}

	public AddressbookFragmentAdapter(RecyclerView rvAddressbook, List<TIchatAddressbook> list, OnItemListener onItemListener, int screenWidth) {
		this.rvAddressbook = rvAddressbook;
		this.list = list;
		this.onItemListener = onItemListener;
		this.screenWidth = screenWidth;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = null;
		ViewHolder viewHolder = null;
		switch (viewType) {
		case INDEX: {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_tab_addressbook_index, parent, false);
			viewHolder = new IndexViewHolder(view);
			break;
		}
		case TOTAL: {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_tab_addressbook_total, parent, false);
			viewHolder = new TotalViewHolder(view);
			break;
		}
		case ITEM: {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_tab_addressbook_item, parent, false);
			viewHolder = new ItemViewHolder(view);
			break;
		}
		}
		return viewHolder;
	}

	@Override
	@SuppressLint("ClickableViewAccessibility")
	public void onBindViewHolder(ViewHolder holder, final int position) {
		TIchatAddressbook addressbook = this.list.get(position);
		switch (this.getItemViewType(position)) {
		case INDEX: {
			IndexViewHolder viewHolder = (IndexViewHolder) holder;
			viewHolder.tvAddressbookItemWord.setText(addressbook.getNickname());
			viewHolder.tvAddressbookItemWord.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						if (swipePosition != -1) closeSwipe();
					}
					return false;
				}
			});
			break;
		}
		case TOTAL: {
			TotalViewHolder viewHolder = (TotalViewHolder) holder;
			viewHolder.tvAddressbookItemTotal.setText(addressbook.getNickname());
			viewHolder.tvAddressbookItemTotal.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						if (swipePosition != -1) closeSwipe();
					}
					return false;
				}
			});
			break;
		}
		case ITEM: {
			ItemViewHolder viewHolder = (ItemViewHolder) holder;
			viewHolder.hsvAddressbookItem.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						ItemViewHolder holder = (ItemViewHolder) rvAddressbook.getChildViewHolder(v);
						int scrollX = holder.hsvAddressbookItem.getScrollX();
						int width = holder.llAddressbookItemRight.getWidth();
						// 滑动长度大于控件的1/2则显示,否则复原
						if (scrollX > width / 2) {
							holder.hsvAddressbookItem.smoothScrollTo(width, 0);
							if (swipePosition != -1 && swipePosition != position) {
								View view = rvAddressbook.getChildAt(swipePosition - rvAddressbook.getChildPosition(rvAddressbook.getChildAt(0)));
								if (view != null) {
									((ItemViewHolder) rvAddressbook.getChildViewHolder(view)).hsvAddressbookItem.smoothScrollTo(0, 0);
								}
							}
							swipePosition = position;
						} else {
							holder.hsvAddressbookItem.smoothScrollTo(0, 0);
							swipePosition = -1;
						}
						return true;
					}
					return false;
				}
			});
			viewHolder.rlAddressbookItemLeft.getLayoutParams().width = this.screenWidth;
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
	}

	@Override
	public int getItemViewType(int position) {
		TIchatAddressbook addressbook = this.list.get(position);
		if (addressbook.getId() != null) {
			return ITEM;
		} else if (addressbook.getNickname().matches("^([A-Z]|#)?$")) {
			return INDEX;
		} else {
			return TOTAL;
		}
	}

	@Override
	public int getItemCount() {
		return this.list.size();
	}

	private void closeSwipe() {
		View view = this.rvAddressbook.getChildAt(this.swipePosition - this.rvAddressbook.getChildPosition(this.rvAddressbook.getChildAt(0)));
		if (view != null) {
			((ItemViewHolder) this.rvAddressbook.getChildViewHolder(view)).hsvAddressbookItem.smoothScrollTo(0, 0);
			this.swipePosition = -1;
		}
	}

	class IndexViewHolder extends RecyclerView.ViewHolder {
		TextView tvAddressbookItemWord;

		public IndexViewHolder(View itemView) {
			super(itemView);
			this.tvAddressbookItemWord = (TextView) itemView.findViewById(R.id.tv_addressbook_item_word);
		}
	}

	class TotalViewHolder extends RecyclerView.ViewHolder {
		TextView tvAddressbookItemTotal;

		public TotalViewHolder(View itemView) {
			super(itemView);
			this.tvAddressbookItemTotal = (TextView) itemView.findViewById(R.id.tv_addressbook_item_total);
		}
	}

	class ItemViewHolder extends RecyclerView.ViewHolder {
		HorizontalScrollView hsvAddressbookItem;
		RelativeLayout rlAddressbookItemLeft;
		ImageView ivAddressbookItemPhoto;
		TextView tvAddressbookItemNickname;
		TextView tvAddressbookItemSignature;
		LinearLayout llAddressbookItemRight;

		public ItemViewHolder(View itemView) {
			super(itemView);
			this.hsvAddressbookItem = (HorizontalScrollView) itemView.findViewById(R.id.hsv_addressbook_item);
			this.rlAddressbookItemLeft = (RelativeLayout) itemView.findViewById(R.id.rl_addressbook_item_left);
			this.ivAddressbookItemPhoto = (ImageView) itemView.findViewById(R.id.iv_addressbook_item_photo);
			this.tvAddressbookItemNickname = (TextView) itemView.findViewById(R.id.tv_addressbook_item_nickname);
			this.tvAddressbookItemSignature = (TextView) itemView.findViewById(R.id.tv_addressbook_item_signature);
			this.llAddressbookItemRight = (LinearLayout) itemView.findViewById(R.id.ll_addressbook_item_right);
		}
	}

}
