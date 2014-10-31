package com.yangc.ichat.fragment.tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yangc.ichat.R;
import com.yangc.ichat.activity.MainActivity;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.widget.IndexScroller;

public class AddressbookFragment extends Fragment {

	private MainActivity mainActivity;

	private ListView lvAddressbook;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mainActivity = (MainActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_tab_addressbook, container, false);
		this.lvAddressbook = (ListView) view.findViewById(R.id.lv_addressbook);
		IndexScroller isIndex = (IndexScroller) view.findViewById(R.id.is_index);
		isIndex.setWords("★ABCDEFGHIJKLMNOPQRSTUVWXYZ#");
		isIndex.setOnTouchWordChangedListener(this.wordChangedListener);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		DatabaseUtils.getAddressbookList(this.mainActivity);
	}

	private IndexScroller.WordChanged wordChangedListener = new IndexScroller.WordChanged() {
		@Override
		public void onTouchWordChanged(String word) {

		}
	};

}
