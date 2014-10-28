package com.yangc.ichat.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.yangc.ichat.R;
import com.yangc.ichat.fragment.MeDetailFragment;
import com.yangc.ichat.utils.VolleyUtils;

public class MeActivity extends FragmentActivity {

	public static final String TAG = MeActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_me);

		this.addFragmentToStack(new MeDetailFragment(), false);
	}

	@Override
	protected void onStop() {
		super.onStop();
		VolleyUtils.cancelAllRequest(TAG);
	}

	public void addFragmentToStack(Fragment fragment, boolean isAddStack) {
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.rl_me_detail, fragment);
		if (isAddStack) {
			fragmentTransaction.addToBackStack(null);
		}
		fragmentTransaction.commit();
	}

}
