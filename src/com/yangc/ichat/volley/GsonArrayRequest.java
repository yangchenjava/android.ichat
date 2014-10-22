package com.yangc.ichat.volley;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonArrayRequest<T> extends Request<T> {

	private final Gson gson;
	private Map<String, String> params;
	private final TypeToken<T> typeToken;
	private final Listener<T> listener;

	public GsonArrayRequest(int method, String url, Map<String, String> params, TypeToken<T> typeToken, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.gson = new Gson();
		this.params = params;
		this.typeToken = typeToken;
		this.listener = listener;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return this.params;
	}

	public GsonArrayRequest(String url, Map<String, String> params, TypeToken<T> typeToken, Listener<T> listener, ErrorListener errorListener) {
		this(Method.GET, url, params, typeToken, listener, errorListener);
	}

	@Override
	protected void deliverResponse(T response) {
		this.listener.onResponse(response);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		T result = this.gson.fromJson(parsed, this.typeToken.getType());
		return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
	}

}
