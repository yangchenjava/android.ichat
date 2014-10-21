package com.yangc.ichat.utils;

import android.content.Context;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class HttpUtils {

	public static String test(Context context) {
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		requestQueue.add(new StringRequest(Method.POST, "", new Response.Listener<String>() {
			@Override
			public void onResponse(String paramT) {

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError paramVolleyError) {

			}
		}));
		requestQueue.start();
		return null;
	}

}
