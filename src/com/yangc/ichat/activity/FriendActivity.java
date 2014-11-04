package com.yangc.ichat.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.yangc.ichat.R;
import com.yangc.ichat.fragment.friend.FriendInfoFragment;

public class FriendActivity extends FragmentActivity {

	public static final String TAG = FriendActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_friend);

		this.addFragmentToStack(new FriendInfoFragment(), this.getIntent().getBundleExtra("addressbook"), false);
	}

	public void addFragmentToStack(Fragment fragment, Bundle bundle, boolean isAddStack) {
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		if (bundle != null && !bundle.isEmpty()) {
			fragment.setArguments(bundle);
		}
		fragmentTransaction.replace(R.id.rl_friend, fragment);
		if (isAddStack) {
			fragmentTransaction.addToBackStack(null);
		}
		fragmentTransaction.commit();
	}

}
