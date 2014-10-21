package com.yangc.ichat.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.yangc.ichat.R;
import com.yangc.ichat.fragment.LogoutFragment;
import com.yangc.ichat.fragment.RegisterFragment;

public class AuthActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_auth);

		this.addFragmentToStack(new LogoutFragment(), false);
	}

	public void addFragmentToStack(Fragment fragment, boolean isAddStack) {
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.rl_auth, fragment);
		if (isAddStack) {
			fragmentTransaction.addToBackStack(null);
		}
		fragmentTransaction.commit();
	}

	@Override
	public void onBackPressed() {
		Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.rl_auth);
		if (fragment instanceof RegisterFragment) {
			((RegisterFragment) fragment).clickBackspace();
		} else {
			super.onBackPressed();
		}
	}

}
