package com.yangc.ichat.volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

public class GsonObjectRequest<T> extends Request<T> {

	private final Gson gson;
	private final Map<String, String> params;
	private final Class<T> clazz;
	private final Listener<T> listener;

	public GsonObjectRequest(int method, String url, Map<String, String> params, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.gson = new Gson();
		this.params = params;
		this.clazz = clazz;
		this.listener = listener;
	}

	public GsonObjectRequest(String url, Map<String, String> params, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
		this(Method.GET, url, params, clazz, listener, errorListener);
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = new HashMap<String, String>();
		CookieHelper.addSessionCookie(headers);
		return headers;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return this.params;
	}

	@Override
	protected void deliverResponse(T response) {
		this.listener.onResponse(response);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		CookieHelper.saveSessionCookie(response.headers);
		String parsed;
		try {
			parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		Log.i("yangchen", parsed);
		T result = this.gson.fromJson(parsed, this.clazz);
		return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
	}

}
