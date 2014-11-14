package com.yangc.ichat.fragment.tab.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
	private static final int TOTAL = 2;

	private Context context;
	private List<TIchatAddressbook> list;
	private DisplayImageOptions options = UILUtils.getDisplayImageOptions();

	public AddressbookFragmentAdapter(Context context, List<TIchatAddressbook> list) {
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
	public int getItemViewType(int position) {
		if (this.list.get(position).getId() != null) {
			return ITEM;
		} else if (position == this.list.size() - 1) {
			return TOTAL;
		} else {
			return INDEX;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
				viewHolder.ivAddressbookItemPhoto = (ImageView) convertView.findViewById(R.id.iv_addressbook_item_photo);
				viewHolder.tvAddressbookItemNickname = (TextView) convertView.findViewById(R.id.tv_addressbook_item_nickname);
				viewHolder.tvAddressbookItemSignature = (TextView) convertView.findViewById(R.id.tv_addressbook_item_signature);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ItemViewHolder) convertView.getTag();
			}

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
			break;
		}
		case TOTAL: {
			TotalViewHolder viewHolder;
			if (convertView == null) {
				convertView = View.inflate(this.context, R.layout.fragment_tab_addressbook_total, null);
				viewHolder = new TotalViewHolder();
				viewHolder.tvAddressbookItemTotal = (TextView) convertView.findViewById(R.id.tv_addressbook_item_total);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (TotalViewHolder) convertView.getTag();
			}

			viewHolder.tvAddressbookItemTotal.setText(addressbook.getNickname());
			break;
		}
		}
		return convertView;
	}

	private class IndexViewHolder {
		TextView tvAddressbookItemWord;
	}

	private class ItemViewHolder {
		ImageView ivAddressbookItemPhoto;
		TextView tvAddressbookItemNickname;
		TextView tvAddressbookItemSignature;
	}

	private class TotalViewHolder {
		TextView tvAddressbookItemTotal;
	}

}
