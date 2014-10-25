package com.yangc.ichat.volley;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;

public class MultiPartStack implements HttpStack {

	@Override
	public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
		if (!(request instanceof MultiPartRequest)) {
			throw new IllegalStateException("please choose MultiPartRequest!!!");
		}
		MultiPartRequest<?> multiPartRequest = (MultiPartRequest<?>) request;
		Map<String, String> headers = new HashMap<String, String>();
		headers.putAll(multiPartRequest.getHeaders());
		headers.putAll(additionalHeaders);

		HttpPost httpPost = new HttpPost(multiPartRequest.getUrl());
		HttpParams httpParams = httpPost.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, multiPartRequest.getTimeoutMs());
		HttpConnectionParams.setSoTimeout(httpParams, multiPartRequest.getTimeoutMs());
		for (Entry<String, String> entry : headers.entrySet()) {
			httpPost.setHeader(entry.getKey(), entry.getValue());
		}

		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
		Map<String, Object> paramsMap = multiPartRequest.getMultiPartParams();
		if (paramsMap != null && !paramsMap.isEmpty()) {
			for (Entry<String, Object> entry : paramsMap.entrySet()) {
				if (entry.getValue() instanceof File) {
					multipartEntityBuilder.addBinaryBody(entry.getKey(), (File) entry.getValue());
				} else {
					multipartEntityBuilder.addTextBody(entry.getKey(), entry.getValue().toString(), contentType);
				}
			}
		}
		httpPost.setEntity(multipartEntityBuilder.build());

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpResponse httpResponse = httpClient.execute(httpPost);
		// 获取sessionid
		for (Cookie cookie : httpClient.getCookieStore().getCookies()) {
			if (TextUtils.equals(cookie.getName(), CookieHelper.SESSION_COOKIE)) {
				CookieHelper.SESSION_ID = cookie.getValue();
				break;
			}
		}
		return httpResponse;
	}

}
