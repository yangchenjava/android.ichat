package com.yangc.ichat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yangc.ichat.R;
import com.yangc.ichat.activity.AuthActivity;

public class LogoutFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auth_logout, container, false);
		((Button) view.findViewById(R.id.btn_auth_login)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentActivity fragmentActivity = getActivity();
				if (fragmentActivity != null) {
					((AuthActivity) fragmentActivity).addFragmentToStack(new LoginFragment(), true);
				}
			}
		});
		((Button) view.findViewById(R.id.btn_auth_register)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentActivity fragmentActivity = getActivity();
				if (fragmentActivity != null) {
					((AuthActivity) fragmentActivity).addFragmentToStack(new RegisterFragment(), true);
				}
			}
		});
		return view;
	}

}
