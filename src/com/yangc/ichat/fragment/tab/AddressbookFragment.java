package com.yangc.ichat.fragment.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yangc.ichat.R;
import com.yangc.ichat.activity.MainActivity;
import com.yangc.ichat.database.bean.TIchatAddressbook;
import com.yangc.ichat.fragment.tab.adapter.AddressbookFragmentAdapter;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.widget.IndexScroller;

public class AddressbookFragment extends Fragment {

	private MainActivity mainActivity;

	private ListView lvAddressbook;
	private TextView tvIndexWord;
	private AddressbookFragmentAdapter adapter;

	private List<TIchatAddressbook> list;
	private Map<String, Integer> map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mainActivity = (MainActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_tab_addressbook, container, false);
		this.lvAddressbook = (ListView) view.findViewById(R.id.lv_addressbook);
		this.lvAddressbook.setOnItemClickListener(this.itemClickListener);
		IndexScroller isIndex = (IndexScroller) view.findViewById(R.id.is_index);
		isIndex.setOnTouchWordChangedListener(this.touchWordChangedListener);
		this.tvIndexWord = (TextView) view.findViewById(R.id.tv_index_word);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.list = new ArrayList<TIchatAddressbook>();
		this.map = new HashMap<String, Integer>();
		this.loadData();
		this.adapter = new AddressbookFragmentAdapter(this.mainActivity, list);
		this.lvAddressbook.setAdapter(this.adapter);
	}

	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		}
	};

	private IndexScroller.OnTouchWordChangedListener touchWordChangedListener = new IndexScroller.OnTouchWordChangedListener() {
		@Override
		public void onTouchWordChanged(String word) {
			if (map != null && map.get(word) != null) {
				lvAddressbook.setSelection(map.get(word));
			}
			tvIndexWord.setText(word);
			tvIndexWord.setVisibility(View.VISIBLE);
		}

		@Override
		public void onTouchWordLeft() {
			tvIndexWord.setVisibility(View.GONE);
		}
	};

	private void loadData() {
		this.list.clear();
		this.map.clear();
		List<TIchatAddressbook> temp = new ArrayList<TIchatAddressbook>();
		List<TIchatAddressbook> addressbookList = DatabaseUtils.getAddressbookList(this.mainActivity);
		for (TIchatAddressbook addressbook : addressbookList) {
			String sortKey = addressbook.getSpell().substring(0, 1).toUpperCase(Locale.getDefault());
			if (sortKey.matches("^[A-Z]$")) {
				if (!this.map.containsKey(sortKey)) {
					this.map.put(sortKey, this.list.size());
					TIchatAddressbook word = new TIchatAddressbook();
					word.setNickname(sortKey);
					this.list.add(word);
				}
				this.list.add(addressbook);
			} else {
				temp.add(addressbook);
			}
		}
		if (!temp.isEmpty()) {
			this.map.put("#", this.list.size());
			TIchatAddressbook word = new TIchatAddressbook();
			word.setNickname("#");
			this.list.add(word);
			this.list.addAll(temp);
		}
		TIchatAddressbook total = new TIchatAddressbook();
		total.setNickname(addressbookList.size() + this.getResources().getString(R.string.addressbook_total));
		this.list.add(total);
	}

}
