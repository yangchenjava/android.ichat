package com.yangc.ichat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yangc.ichat.R;

public class AddressbookFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tab_addressbook, container, false);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
