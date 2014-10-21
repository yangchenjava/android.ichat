package com.yangc.ichat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yangc.ichat.R;

public class LoginFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auth_login, container, false);
		((ImageView) view.findViewById(R.id.iv_login_backspace)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentActivity fragmentActivity = getActivity();
				if (fragmentActivity != null) {
					fragmentActivity.getSupportFragmentManager().popBackStack();
				}
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
