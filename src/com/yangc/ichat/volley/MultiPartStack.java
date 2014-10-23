package com.yangc.ichat.volley;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;

public class MultiPartStack implements HttpStack {

	private static final String HEADER_CONTENT_TYPE = "Content-Type";

	@Override
	public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
		if (!(request instanceof MultiPartRequest)) {
			throw new IllegalStateException("please choose MultiPartRequest!!!");
		}
		MultiPartRequest<?> multiPartRequest = (MultiPartRequest<?>) request;
		Map<String, String> map = new HashMap<String, String>();
		map.putAll(multiPartRequest.getHeaders());
		map.putAll(additionalHeaders);

		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(multiPartRequest.getUrl());
		httpPost.setConfig(RequestConfig.custom().setConnectionRequestTimeout(multiPartRequest.getTimeoutMs()).setConnectTimeout(multiPartRequest.getTimeoutMs()).build());
		httpPost.addHeader(HEADER_CONTENT_TYPE, multiPartRequest.getBodyContentType());
		for (Entry<String, String> entry : map.entrySet()) {
			httpPost.setHeader(entry.getKey(), entry.getValue());
		}

		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		ContentType contentType = ContentType.create("text/plain", Consts.UTF_8);
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
		HttpResponse httpResponse = closeableHttpClient.execute(httpPost);
		closeableHttpClient.close();
		return httpResponse;
	}

}
