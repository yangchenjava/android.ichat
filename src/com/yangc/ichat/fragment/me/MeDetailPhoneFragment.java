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
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.DatabaseUtils;

public class MeDetailPhoneFragment extends Fragment {

	private MeActivity meActivity;

	private EditText etMeDetailPhone;
	private String phone;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.meActivity = (MeActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_me_detail_phone, container, false);
		((ImageView) view.findViewById(R.id.iv_me_detail_phone_backspace)).setOnClickListener(this.backspaceListener);
		((Button) view.findViewById(R.id.btn_me_detail_phone)).setOnClickListener(this.phoneListener);
		this.etMeDetailPhone = (EditText) view.findViewById(R.id.et_me_detail_phone);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.phone = this.getArguments().getString("phone");
		if (!TextUtils.isEmpty(this.phone)) {
			this.etMeDetailPhone.setText(this.phone);
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

	// 电话监听
	private View.OnClickListener phoneListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String newPhone = etMeDetailPhone.getText().toString().trim();
			if (!TextUtils.isEmpty(newPhone) && !newPhone.matches("^1[3-8]{1}\\d{9}$")) {
				AndroidUtils.alertToast(meActivity, R.string.error_phone_validate);
				return;
			}

			if (!TextUtils.equals(newPhone, phone)) {
				TIchatMe me = DatabaseUtils.getMe(meActivity);
				me.setPhone(newPhone);
				DatabaseUtils.updateMe(meActivity, me);
			}
			clickBackspace();
		}
	};

}