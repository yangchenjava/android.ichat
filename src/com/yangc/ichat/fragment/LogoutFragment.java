package com.yangc.ichat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yangc.ichat.R;
import com.yangc.ichat.activity.AuthActivity;

public class LogoutFragment extends Fragment {

	private AuthActivity authActivity;

	public LogoutFragment(AuthActivity authActivity) {
		this.authActivity = authActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auth_logout, container, false);
		((Button) view.findViewById(R.id.btn_auth_login)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				authActivity.addFragmentToStack(new LoginFragment(authActivity), true);
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
