package com.yangc.ichat.fragment.tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yangc.ichat.R;
import com.yangc.ichat.activity.MainActivity;

public class WechatFragment extends Fragment {

	private MainActivity mainActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mainActivity = (MainActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_tab_wechat, container, false);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
