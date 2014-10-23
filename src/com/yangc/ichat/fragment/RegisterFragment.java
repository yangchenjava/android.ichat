package com.yangc.ichat.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yangc.ichat.R;
import com.yangc.ichat.activity.AuthActivity;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Md5Utils;

public class RegisterFragment extends Fragment {

	private AuthActivity authActivity;

	private TextView tvRegisterBackspace;
	private LinearLayout llRegister_1;
	private LinearLayout llRegister_2;

	private EditText etRegisterUsername;
	private EditText etRegisterPassword_1;
	private EditText etRegisterPassword_2;
	private EditText etRegisterNickname;
	private RadioGroup rgRegisterSex;
	private EditText etRegisterPhone;
	private EditText etRegisterSignature;

	private String username;
	private String password;

	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.authActivity = (AuthActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_auth_register, container, false);
		((ImageView) view.findViewById(R.id.iv_register_backspace)).setOnClickListener(this.backspaceListener);
		this.tvRegisterBackspace = (TextView) view.findViewById(R.id.tv_register_backspace);
		this.llRegister_1 = (LinearLayout) view.findViewById(R.id.ll_register_1);
		this.llRegister_2 = (LinearLayout) view.findViewById(R.id.ll_register_2);

		this.etRegisterUsername = (EditText) view.findViewById(R.id.et_register_username);
		this.etRegisterPassword_1 = (EditText) view.findViewById(R.id.et_register_password_1);
		this.etRegisterPassword_2 = (EditText) view.findViewById(R.id.et_register_password_2);
		((Button) view.findViewById(R.id.btn_register_next)).setOnClickListener(this.registerNextListener);

		((ImageView) view.findViewById(R.id.iv_register_photo)).setOnClickListener(this.photoListener);
		this.etRegisterNickname = (EditText) view.findViewById(R.id.et_register_nickname);
		this.rgRegisterSex = (RadioGroup) view.findViewById(R.id.rg_register_sex);
		this.etRegisterPhone = (EditText) view.findViewById(R.id.et_register_phone);
		this.etRegisterSignature = (EditText) view.findViewById(R.id.et_register_signature);
		((Button) view.findViewById(R.id.btn_register)).setOnClickListener(this.registerListener);
		return view;
	}

	// 后退监听
	private View.OnClickListener backspaceListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			clickBackspace();
		}
	};

	public void clickBackspace() {
		int visibility = this.llRegister_1.getVisibility();
		if (visibility == View.VISIBLE) {
			if (this.authActivity != null) {
				this.authActivity.getSupportFragmentManager().popBackStack();
			}
		} else if (visibility == View.GONE) {
			this.llRegister_1.setVisibility(View.VISIBLE);
			this.llRegister_2.setVisibility(View.GONE);
			this.tvRegisterBackspace.setText(R.string.fragment_auth_logout);
		}
	}

	// 注册下一步监听
	private View.OnClickListener registerNextListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 验证用户名
			username = etRegisterUsername.getText().toString().trim();
			if (!username.matches("^[\\w\\-\\/\\.]{8,15}$")) {
				AndroidUtils.alertToast(authActivity, R.string.error_username_validate);
				return;
			}

			// 验证密码
			password = etRegisterPassword_1.getText().toString().trim();
			String pwd = etRegisterPassword_2.getText().toString().trim();
			if (!password.matches("^[\\w\\-]{6,15}$") || !pwd.matches("^[\\w\\-]{6,15}$")) {
				AndroidUtils.alertToast(authActivity, R.string.error_password_validate);
				return;
			} else if (!TextUtils.equals(password, pwd)) {
				AndroidUtils.alertToast(authActivity, R.string.error_password_twice_validate);
				return;
			}
			password = Md5Utils.getMD5(password);

			llRegister_1.setVisibility(View.GONE);
			llRegister_2.setVisibility(View.VISIBLE);
			tvRegisterBackspace.setText(R.string.register_previous_button);
		}
	};

	// 头像监听
	private View.OnClickListener photoListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

		}
	};

	// 注册监听
	private View.OnClickListener registerListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 验证昵称
			String nickname = etRegisterNickname.getText().toString().trim();
			if (TextUtils.isEmpty(nickname)) {
				AndroidUtils.alertToast(authActivity, R.string.error_nickname_null);
				return;
			}

			// 验证手机
			String phone = etRegisterPhone.getText().toString().trim();
			if (!TextUtils.isEmpty(phone) && !phone.matches("^1[3-8]{1}\\d{9}$")) {
				AndroidUtils.alertToast(authActivity, R.string.error_phone_validate);
				return;
			}
		}
	};

}
