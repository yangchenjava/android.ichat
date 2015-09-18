package com.yangc.ichat.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.yangc.ichat.R;
import com.yangc.ichat.ui.fragment.me.MeDetailFragment;
import com.yangc.ichat.utils.VolleyUtils;

public class MeActivity extends FragmentActivity {

	public static final String TAG = MeActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_me);

		this.addFragmentToStack(new MeDetailFragment(), null, false);
	}

	@Override
	protected void onDestroy() {
		VolleyUtils.cancelAllRequest(TAG);
		super.onDestroy();
	}

	public void addFragmentToStack(Fragment fragment, Bundle bundle, boolean isAddStack) {
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		if (bundle != null && !bundle.isEmpty()) {
			fragment.setArguments(bundle);
		}
		fragmentTransaction.replace(R.id.rl_me_detail, fragment);
		if (isAddStack) {
			fragmentTransaction.addToBackStack(null);
		}
		fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
		fragmentTransaction.commit();
	}

}
