package com.yangc.ichat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yangc.ichat.R;
import com.yangc.ichat.activity.AuthActivity;

public class RegisterFragment extends Fragment {

	private AuthActivity authActivity;

	public RegisterFragment(AuthActivity authActivity) {
		this.authActivity = authActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auth_register, container, false);
		((ImageView) view.findViewById(R.id.iv_register_backspace)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				authActivity.getSupportFragmentManager().popBackStack();
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
