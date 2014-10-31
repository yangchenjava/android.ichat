package com.yangc.ichat.fragment.tab.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yangc.ichat.database.bean.TIchatAddressbook;

public class AddressbookFragmentAdapter extends BaseAdapter {

	private Context context;
	private List<TIchatAddressbook> list;

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
		return null;
	}

}
