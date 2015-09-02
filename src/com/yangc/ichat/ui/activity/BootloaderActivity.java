package com.yangc.ichat.ui.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yangc.ichat.R;
import com.yangc.ichat.bean.ResultBean;
import com.yangc.ichat.database.bean.TIchatMe;
import com.yangc.ichat.http.volley.GsonObjectRequest;
import com.yangc.ichat.http.volley.VolleyErrorHelper;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.utils.EmojiUtils;
import com.yangc.ichat.utils.VolleyUtils;

public class BootloaderActivity extends Activity {

	private static final String TAG = BootloaderActivity.class.getName();

	private Request<ResultBean> request;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_bootloader);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startup();
			}
		}, 2000);
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "destory");
		VolleyUtils.cancelAllRequest(TAG);
		super.onDestroy();
	}

	private void startup() {
		// 加载表情数据到内存
		EmojiUtils.loadEmoji(this);
		// 如果未登录,进入登录或注册页面,否则进入主页面
		if (TextUtils.isEmpty(Constants.USERNAME) || TextUtils.isEmpty(Constants.PASSWORD)) {
			this.startActivity(new Intent(this, AuthActivity.class));
			this.finish();
		} else {
			// 如果网络可用则同步用户信息
			TIchatMe me = DatabaseUtils.getMe(this);
			if (AndroidUtils.checkNetwork(this) && me != null) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("id", "" + me.getId());
				params.put("nickname", me.getNickname());
				params.put("sex", "" + me.getSex());
				params.put("phone", me.getPhone());
				params.put("signature", me.getSignature());
				this.request = new GsonObjectRequest<ResultBean>(Request.Method.POST, Constants.UPDATE_PERSON, params, ResultBean.class, listener, errorListener);
				VolleyUtils.addNormalRequest(this.request, TAG);
				this.startActivity(new Intent(this, MainActivity.class));
			} else {
				this.startActivity(new Intent(this, MainActivity.class));
				this.finish();
			}
		}
	}

	private Response.Listener<ResultBean> listener = new Response.Listener<ResultBean>() {
		@Override
		public void onResponse(ResultBean result) {
			if (!result.isSuccess()) {
				AndroidUtils.alertToast(BootloaderActivity.this, result.getMessage());
			}
			finish();
		}
	};

	private Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			if (error instanceof AuthFailureError) {
				VolleyErrorHelper.sessionTimeout(BootloaderActivity.this, request, TAG);
			} else {
				AndroidUtils.alertToast(BootloaderActivity.this, VolleyErrorHelper.getResId(error));
				Log.e(TAG, error.getMessage(), error.getCause());
				finish();
			}
		}
	};

}
