package com.yangc.ichat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangc.ichat.R;
import com.yangc.ichat.activity.AuthActivity;

public class RegisterFragment extends Fragment {

	private AuthActivity authActivity;

	private TextView tvRegisterBackspace;
	private LinearLayout llRegister_1;
	private LinearLayout llRegister_2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.authActivity = (AuthActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_auth_register, container, false);
		((ImageView) view.findViewById(R.id.iv_register_backspace)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickBackspace();
			}
		});
		this.tvRegisterBackspace = (TextView) view.findViewById(R.id.tv_register_backspace);
		this.llRegister_1 = (LinearLayout) view.findViewById(R.id.ll_register_1);
		this.llRegister_2 = (LinearLayout) view.findViewById(R.id.ll_register_2);

		((Button) view.findViewById(R.id.btn_register_next)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				llRegister_1.setVisibility(View.GONE);
				llRegister_2.setVisibility(View.VISIBLE);
				tvRegisterBackspace.setText(R.string.register_previous_button);
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

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

}
