package com.yangc.ichat.http.volley;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.yangc.ichat.R;
import com.yangc.ichat.bean.ResultBean;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.VolleyUtils;

public class VolleyErrorHelper {

	public static int getResId(VolleyError error) {
		if (error instanceof TimeoutError) {
			return R.string.error_timeout;
		} else if (error instanceof AuthFailureError || error instanceof ServerError) {
			NetworkResponse networkResponse = error.networkResponse;
			if (networkResponse != null) {
				switch (networkResponse.statusCode) {
				case 401:
					return R.string.error_unauthorized;
				case 404:
					return R.string.error_notfound;
				case 500:
					return R.string.error_server;
				}
			}
		}
		return R.string.error_network;
	}

	/**
	 * @功能: session超时,重新登录获取session
	 * @作者: yangc
	 * @创建日期: 2014年10月24日 下午3:23:30
	 * @param context
	 * @param request
	 * @param tag
	 */
	public static void sessionTimeout(final Context context, final Request<?> request, final String tag) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", Constants.USERNAME);
		params.put("password", Constants.PASSWORD);
		VolleyUtils.addNormalRequest(new GsonObjectRequest<ResultBean>(Request.Method.POST, Constants.LOGIN, params, ResultBean.class, new Response.Listener<ResultBean>() {
			@Override
			public void onResponse(ResultBean result) {
				if (result.isSuccess()) {
					VolleyUtils.addNormalRequest(request, tag);
				} else {
					AndroidUtils.alertToast(context, result.getMessage());
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				AndroidUtils.alertToast(context, VolleyErrorHelper.getResId(error));
				Log.e(tag, error.getMessage(), error.getCause());
			}
		}), tag);
	}

}
