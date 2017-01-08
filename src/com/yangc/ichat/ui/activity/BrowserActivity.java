package com.yangc.ichat.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yangc.ichat.R;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.zxing.Intents;

public class BrowserActivity extends Activity {

	private static final String TAG = BrowserActivity.class.getSimpleName();

	private TextView tvBrowserTitle;
	private WebView wvBrowser;
	private ProgressBar pbBrowser;
	private LinearLayout llBrowser;
	private TextView tvBrowserContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_browser);

		((ImageView) this.findViewById(R.id.iv_browser_backspace)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		this.tvBrowserTitle = (TextView) this.findViewById(R.id.tv_browser_title);
		this.wvBrowser = (WebView) this.findViewById(R.id.wv_browser);
		this.pbBrowser = (ProgressBar) this.findViewById(R.id.pb_browser);
		this.llBrowser = (LinearLayout) this.findViewById(R.id.ll_browser);
		this.tvBrowserContent = (TextView) this.findViewById(R.id.tv_browser_content);

		Intent intent = this.getIntent();
		String resultFormat = intent.getStringExtra(Intents.Scan.RESULT_FORMAT), result = intent.getStringExtra(Intents.Scan.RESULT);
		Log.i(TAG, resultFormat);
		Log.i(TAG, result);
		if (result != null && result.matches("^[a-zA-z]+:\\/\\/[^\\s]*$")) {
			this.loadUrl(result);
			this.llBrowser.setVisibility(View.GONE);
		} else {
			this.tvBrowserTitle.setText(R.string.browser_title);
			this.wvBrowser.setVisibility(View.GONE);
			this.pbBrowser.setVisibility(View.GONE);
			this.tvBrowserContent.setText(result);
		}
		if (result == null) AndroidUtils.alertToast(this, R.string.scan_not_found);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void loadUrl(String url) {
		WebSettings webSettings = this.wvBrowser.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSupportZoom(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		this.wvBrowser.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Log.i(TAG, "url=" + url);
				Log.i(TAG, "userAgent=" + userAgent);
				Log.i(TAG, "contentDisposition=" + contentDisposition);
				Log.i(TAG, "mimetype=" + mimetype);
				Log.i(TAG, "contentLength=" + contentLength);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			}
		});
		this.wvBrowser.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					pbBrowser.setVisibility(View.GONE);
				} else {
					pbBrowser.setVisibility(View.VISIBLE);
					Log.i(TAG, "progress=" + newProgress);
					pbBrowser.setProgress(newProgress);
				}
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				tvBrowserTitle.setText(title);
			}
		});
		this.wvBrowser.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		this.wvBrowser.loadUrl(url);
	}

}
