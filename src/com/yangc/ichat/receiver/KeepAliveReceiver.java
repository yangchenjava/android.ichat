package com.yangc.ichat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yangc.ichat.service.PushService;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;

public class KeepAliveReceiver extends BroadcastReceiver {

	private static final String TAG = KeepAliveReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (AndroidUtils.checkNetwork(context)) {
			Log.i(TAG, "KeepAliveReceiver");
			Intent i = new Intent(context, PushService.class);
			i.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_SEND_HEART);
			context.startService(i);
		}
	}

}
