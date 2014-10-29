package com.yangc.ichat.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.yangc.ichat.R;
import com.yangc.ichat.fragment.auth.LogoutFragment;
import com.yangc.ichat.fragment.auth.RegisterFragment;
import com.yangc.ichat.utils.VolleyUtils;

public class AuthActivity extends FragmentActivity {

	public static final String TAG = AuthActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_auth);

		this.addFragmentToStack(new LogoutFragment(), false);
	}

	@Override
	protected void onStop() {
		super.onStop();
		VolleyUtils.cancelAllRequest(TAG);
	}

	@Override
	public void onBackPressed() {
		Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.rl_auth);
		if (fragment instanceof RegisterFragment) {
			((RegisterFragment) fragment).clickBackspace();
		} else if (this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
			this.moveTaskToBack(true);
		} else {
			super.onBackPressed();
		}
	}

	public void addFragmentToStack(Fragment fragment, boolean isAddStack) {
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.rl_auth, fragment);
		if (isAddStack) {
			fragmentTransaction.addToBackStack(null);
		}
		fragmentTransaction.commit();
	}

}
