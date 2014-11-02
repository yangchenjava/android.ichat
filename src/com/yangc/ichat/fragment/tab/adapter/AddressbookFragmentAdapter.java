package com.yangc.ichat.fragment.tab.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yangc.ichat.R;
import com.yangc.ichat.database.bean.TIchatAddressbook;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.UILUtils;

public class AddressbookFragmentAdapter extends BaseAdapter {

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
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = View.inflate(this.context, R.layout.fragment_tab_addressbook_item, null);
			viewHolder = new ViewHolder();
			viewHolder.rlAddressbookItem = (RelativeLayout) view.findViewById(R.id.rl_addressbook_item);
			viewHolder.tvAddressbookItemWord = (TextView) view.findViewById(R.id.tv_addressbook_item_word);
			viewHolder.ivAddressbookItemPhoto = (ImageView) view.findViewById(R.id.iv_addressbook_item_photo);
			viewHolder.tvAddressbookItemNickname = (TextView) view.findViewById(R.id.tv_addressbook_item_nickname);
			viewHolder.tvAddressbookItemSignature = (TextView) view.findViewById(R.id.tv_addressbook_item_signature);
			viewHolder.tvAddressbookItemTotal = (TextView) view.findViewById(R.id.tv_addressbook_item_total);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}

		TIchatAddressbook addressbook = this.list.get(position);
		if (addressbook.getId() != null) {
			viewHolder.rlAddressbookItem.setBackgroundResource(R.drawable.selector_main);
			viewHolder.tvAddressbookItemWord.setVisibility(View.GONE);
			viewHolder.ivAddressbookItemPhoto.setVisibility(View.VISIBLE);
			if (TextUtils.isEmpty(addressbook.getPhoto())) {
				viewHolder.ivAddressbookItemPhoto.setImageResource(R.drawable.me_info);
			} else {
				ImageLoader.getInstance().displayImage(Constants.SERVER_URL + addressbook.getPhoto(), viewHolder.ivAddressbookItemPhoto, this.options);
			}
			viewHolder.tvAddressbookItemNickname.setVisibility(View.VISIBLE);
			viewHolder.tvAddressbookItemNickname.setText(addressbook.getNickname());
			if (TextUtils.isEmpty(addressbook.getSignature())) {
				viewHolder.tvAddressbookItemSignature.setVisibility(View.GONE);
			} else {
				viewHolder.tvAddressbookItemSignature.setVisibility(View.VISIBLE);
				viewHolder.tvAddressbookItemSignature.setText(addressbook.getSignature());
			}
			viewHolder.tvAddressbookItemTotal.setVisibility(View.GONE);
		} else {
			if (position == this.list.size() - 1) {
				viewHolder.rlAddressbookItem.setBackgroundResource(android.R.color.white);
				viewHolder.tvAddressbookItemWord.setVisibility(View.GONE);
				viewHolder.tvAddressbookItemTotal.setVisibility(View.VISIBLE);
				viewHolder.tvAddressbookItemTotal.setText(addressbook.getNickname());
			} else {
				viewHolder.rlAddressbookItem.setBackgroundResource(R.drawable.layer_dividing_line);
				viewHolder.tvAddressbookItemWord.setVisibility(View.VISIBLE);
				viewHolder.tvAddressbookItemWord.setText(addressbook.getNickname());
				viewHolder.tvAddressbookItemTotal.setVisibility(View.GONE);
			}
			viewHolder.ivAddressbookItemPhoto.setVisibility(View.GONE);
			viewHolder.tvAddressbookItemNickname.setVisibility(View.GONE);
			viewHolder.tvAddressbookItemSignature.setVisibility(View.GONE);
		}

		return view;
	}

	private class ViewHolder {
		RelativeLayout rlAddressbookItem;
		TextView tvAddressbookItemWord;
		ImageView ivAddressbookItemPhoto;
		TextView tvAddressbookItemNickname;
		TextView tvAddressbookItemSignature;
		TextView tvAddressbookItemTotal;
	}

}
