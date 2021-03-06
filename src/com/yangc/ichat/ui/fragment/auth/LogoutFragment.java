package com.yangc.ichat.ui.fragment.auth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yangc.ichat.R;
import com.yangc.ichat.ui.activity.AuthActivity;

public class LogoutFragment extends Fragment {

	private AuthActivity authActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.authActivity = (AuthActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_auth_logout, container, false);
		((Button) view.findViewById(R.id.btn_auth_login)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				authActivity.addFragmentToStack(Fragment.instantiate(authActivity, LoginFragment.class.getName()), true);
			}
		});
		((Button) view.findViewById(R.id.btn_auth_register)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				authActivity.addFragmentToStack(Fragment.instantiate(authActivity, RegisterFragment.class.getName()), true);
			}
		});
		return view;
	}

}
