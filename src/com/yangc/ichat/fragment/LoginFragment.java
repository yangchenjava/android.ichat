package com.yangc.ichat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yangc.ichat.R;
import com.yangc.ichat.activity.AuthActivity;

public class LoginFragment extends Fragment {

	private AuthActivity authActivity;

	public LoginFragment(AuthActivity authActivity) {
		this.authActivity = authActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auth_login, container, false);
		((ImageView) view.findViewById(R.id.iv_title_bar_backspace)).setOnClickListener(new View.OnClickListener() {
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
