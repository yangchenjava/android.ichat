package com.yangc.ichat.fragment.me;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.yangc.ichat.R;
import com.yangc.ichat.activity.MeActivity;
import com.yangc.ichat.database.bean.TIchatMe;
import com.yangc.ichat.utils.DatabaseUtils;

public class MeDetailSignatureFragment extends Fragment {

	private MeActivity meActivity;

	private EditText etMeDetailSignature;
	private String signature;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.meActivity = (MeActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_me_detail_signature, container, false);
		((ImageView) view.findViewById(R.id.iv_me_detail_signature_backspace)).setOnClickListener(this.backspaceListener);
		((Button) view.findViewById(R.id.btn_me_detail_signature)).setOnClickListener(this.signatureListener);
		this.etMeDetailSignature = (EditText) view.findViewById(R.id.et_me_detail_signature);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.signature = this.getArguments().getString("signature");
		if (!TextUtils.isEmpty(this.signature)) {
			this.etMeDetailSignature.setText(this.signature);
		}
	}

	private void clickBackspace() {
		View currentFocus = this.meActivity.getCurrentFocus();
		if (currentFocus != null) {
			InputMethodManager imm = (InputMethodManager) this.meActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		this.meActivity.onBackPressed();
	}

	// 后退监听
	private View.OnClickListener backspaceListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			clickBackspace();
		}
	};

	// 个性签名监听
	private View.OnClickListener signatureListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String newSignature = etMeDetailSignature.getText().toString().trim();
			if (!TextUtils.equals(newSignature, signature)) {
				TIchatMe me = DatabaseUtils.getMe(meActivity);
				me.setSignature(newSignature);
				DatabaseUtils.updateMe(meActivity, me);
			}
			clickBackspace();
		}
	};

}
