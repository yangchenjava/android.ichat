package com.yangc.ichat.volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

public class MultiPartRequest<T> extends Request<T> {

	private final Gson gson;
	private final Map<String, Object> params;
	private final Class<T> clazz;
	private final Listener<T> listener;

	public MultiPartRequest(String url, Map<String, Object> params, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
		super(Request.Method.POST, url, errorListener);
		this.gson = new Gson();
		this.params = params;
		this.clazz = clazz;
		this.listener = listener;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = new HashMap<String, String>();
		CookieHelper.addSessionCookie(headers);
		return headers;
	}

	public Map<String, Object> getMultiPartParams() throws AuthFailureError {
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
		T result = this.gson.fromJson(parsed, this.clazz);
		return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
	}

}
