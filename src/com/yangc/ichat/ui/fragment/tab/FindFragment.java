package com.yangc.ichat.ui.fragment.tab;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yangc.ichat.R;
import com.yangc.ichat.ui.activity.BrowserActivity;
import com.yangc.ichat.ui.activity.ShakeActivity;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.zxing.CaptureActivity;

public class FindFragment extends Fragment {

	private static final int REQUEST_CODE = 1;

	private Dialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tab_find, container, false);
		((LinearLayout) view.findViewById(R.id.ll_find_scan)).setOnClickListener(this.findScanListener);
		((LinearLayout) view.findViewById(R.id.ll_find_shake)).setOnClickListener(this.findShakeListener);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (this.progressDialog != null) {
			this.progressDialog.dismiss();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
			this.startActivity(data.setClass(this.getActivity(), BrowserActivity.class));
		}
	}

	private View.OnClickListener findScanListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			progressDialog = AndroidUtils.showProgressDialog(getActivity(), getActivity().getResources().getString(R.string.text_load), false, false);
			startActivityForResult(new Intent(getActivity(), CaptureActivity.class), REQUEST_CODE);
		}
	};

	private View.OnClickListener findShakeListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			startActivity(new Intent(getActivity(), ShakeActivity.class));
		}
	};

}
