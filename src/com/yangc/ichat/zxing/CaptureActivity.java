/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yangc.ichat.zxing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.yangc.ichat.R;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.QRCodeUtils;
import com.yangc.ichat.zxing.camera.CameraManager;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a viewfinder to help the user place the barcode correctly, shows feedback as the image processing is
 * happening, and then overlays the results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	private static final int PHOTO_LOCAL = 1;

	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType, Object> decodeHints;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;
	private AmbientLightManager ambientLightManager;

	private int colorScanTabNormal;
	private int colorScanTabSelect;

	private PopupWindow mPopupWindow;
	private ImageView ivCaptureMore;

	private LinearLayout llCaptureTabQrcode;
	private LinearLayout llCaptureTabCover;
	private LinearLayout llCaptureTabStreetscape;
	private LinearLayout llCaptureTabTranslation;

	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_capture);

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		ambientLightManager = new AmbientLightManager(this);

		this.colorScanTabNormal = this.getResources().getColor(R.color.scan_tab_normal);
		this.colorScanTabSelect = this.getResources().getColor(R.color.scan_tab_select);

		this.initPopupWindow();

		((ImageView) this.findViewById(R.id.iv_capture_backspace)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		this.ivCaptureMore = (ImageView) this.findViewById(R.id.iv_capture_more);
		this.ivCaptureMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopupWindow();
			}
		});

		this.llCaptureTabQrcode = (LinearLayout) this.findViewById(R.id.ll_capture_tab_qrcode);
		this.llCaptureTabCover = (LinearLayout) this.findViewById(R.id.ll_capture_tab_cover);
		this.llCaptureTabStreetscape = (LinearLayout) this.findViewById(R.id.ll_capture_tab_streetscape);
		this.llCaptureTabTranslation = (LinearLayout) this.findViewById(R.id.ll_capture_tab_translation);

		this.llCaptureTabQrcode.setOnClickListener(this.clickListener);
		this.llCaptureTabCover.setOnClickListener(this.clickListener);
		this.llCaptureTabStreetscape.setOnClickListener(this.clickListener);
		this.llCaptureTabTranslation.setOnClickListener(this.clickListener);
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void onResume() {
		super.onResume();

		// CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
		// want to open the camera driver and measure the screen size if we're going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the wrong size and partially
		// off screen.
		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.vv_capture);
		viewfinderView.setCameraManager(cameraManager);

		handler = null;

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.sv_capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		beepManager.updatePrefs();
		ambientLightManager.start(cameraManager);

		inactivityTimer.onResume();

		Intent intent = getIntent();

		decodeFormats = null;
		characterSet = null;

		if (intent != null) {
			String action = intent.getAction();

			if (Intents.Scan.ACTION.equals(action)) {
				// Scan the formats the intent requested, and return the result to the calling activity.
				decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
				decodeHints = DecodeHintManager.parseDecodeHints(intent);

				if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
					int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
					int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
					if (width > 0 && height > 0) {
						cameraManager.setManualFramingRect(width, height);
					}
				}

				String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
				if (customPromptMessage != null) {
					viewfinderView.setScanPrompt(customPromptMessage);
				}
			}

			characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
		}

		this.switchTab(R.id.ll_capture_tab_qrcode);
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		ambientLightManager.stop();
		beepManager.close();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.sv_capture_preview);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_LOCAL && resultCode == Activity.RESULT_OK && data != null) {
			Dialog progressDialog = AndroidUtils.showProgressDialog(this, getResources().getString(R.string.text_load), true, true);
			String result = null;
			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
				result = QRCodeUtils.decode(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Intent intent = new Intent(getIntent().getAction());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			if (!TextUtils.isEmpty(result)) {
				intent.putExtra(Intents.Scan.RESULT, result);
				intent.putExtra(Intents.Scan.RESULT_FORMAT, BarcodeFormat.QR_CODE.toString());
			}
			sendReplyMessage(R.id.return_scan_result, intent);
			progressDialog.dismiss();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// if (sign && handler != null) {
			// sign = false;
			// handler.sendEmptyMessage(R.id.restart_preview);
			// } else {
			setResult(RESULT_CANCELED);
			finish();
			// }
			return true;
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_CAMERA:
			// Handle these events so they don't launch the Camera app
			return true;
			// Use volume up/down to turn on light
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.setTorch(true);
			return true;
		case KeyEvent.KEYCODE_MENU:
			return this.showPopupWindow();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show the results.
	 *
	 * @param rawResult The contents of the barcode.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		inactivityTimer.onActivity();

		// Then not from history, so beep/vibrate and we have an image to draw on
		beepManager.playBeepSoundAndVibrate();

		// viewfinderView.drawResultBitmap(barcode);
		handleDecodeExternally(rawResult);
	}

	// Briefly show the contents of the barcode, then handle the result outside Barcode Scanner.
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void handleDecodeExternally(Result rawResult) {
		// Hand back whatever action they requested - this can be changed to Intents.Scan.ACTION when
		// the deprecated intent is retired.
		Intent intent = new Intent(getIntent().getAction());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
		intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
		byte[] rawBytes = rawResult.getRawBytes();
		if (rawBytes != null && rawBytes.length > 0) {
			intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
		}
		Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
		if (metadata != null) {
			if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
				intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION, metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
			}
			Number orientation = (Number) metadata.get(ResultMetadataType.ORIENTATION);
			if (orientation != null) {
				intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
			}
			String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
			if (ecLevel != null) {
				intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
			}
			Iterable<byte[]> byteSegments = (Iterable<byte[]>) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
			if (byteSegments != null) {
				int i = 0;
				for (byte[] byteSegment : byteSegments) {
					intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
					i++;
				}
			}
		}
		sendReplyMessage(R.id.return_scan_result, intent);
	}

	private void sendReplyMessage(int id, Object arg) {
		if (handler != null) {
			Message message = Message.obtain(handler, id, arg);
			handler.sendMessage(message);
		}
	}

	private void initCamera(final SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						cameraManager.openDriver(surfaceHolder);
					} catch (IOException ioe) {
						Log.w(TAG, ioe);
					}
				}
			}).start();
			// Creating the handler starts the preview, which can also throw a RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
			}
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.lang.RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
		}
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	/** ========================================== custom ====================================================== */

	private void initPopupWindow() {
		// PopupWindow
		View popupWindowView = View.inflate(this, R.layout.popup_window_capture, null);
		popupWindowView.setFocusable(true);
		popupWindowView.setFocusableInTouchMode(true);
		popupWindowView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
					closePopupWindow();
				}
				return false;
			}
		});
		popupWindowView.findViewById(R.id.tv_popup_window_capture_file).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closePopupWindow();
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, PHOTO_LOCAL);
			}
		});

		this.mPopupWindow = new PopupWindow(popupWindowView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		// 设置点击返回键和PopupWindow以外的地方,退出
		this.mPopupWindow.setTouchable(true);
		this.mPopupWindow.setOutsideTouchable(true);
		this.mPopupWindow.setBackgroundDrawable(new BitmapDrawable(this.getResources(), (Bitmap) null));
	}

	private boolean showPopupWindow() {
		if (this.mPopupWindow != null && !this.mPopupWindow.isShowing()) {
			this.mPopupWindow.showAsDropDown(this.ivCaptureMore);
			return true;
		}
		return false;
	}

	private boolean closePopupWindow() {
		if (this.mPopupWindow != null && this.mPopupWindow.isShowing()) {
			this.mPopupWindow.dismiss();
			return true;
		}
		return false;
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switchTab(v.getId());
		}
	};

	private void switchTab(int tabId) {
		ImageView ivCaptureTabQrcode = (ImageView) this.llCaptureTabQrcode.findViewById(R.id.iv_capture_tab_qrcode);
		TextView tvCaptureTabQrcode = (TextView) this.llCaptureTabQrcode.findViewById(R.id.tv_capture_tab_qrcode);
		ImageView ivCaptureTabCover = (ImageView) this.llCaptureTabCover.findViewById(R.id.iv_capture_tab_cover);
		TextView tvCaptureTabCover = (TextView) this.llCaptureTabCover.findViewById(R.id.tv_capture_tab_cover);
		ImageView ivCaptureTabStreetscape = (ImageView) this.llCaptureTabStreetscape.findViewById(R.id.iv_capture_tab_streetscape);
		TextView tvCaptureTabStreetscape = (TextView) this.llCaptureTabStreetscape.findViewById(R.id.tv_capture_tab_streetscape);
		ImageView ivCaptureTabTranslation = (ImageView) this.llCaptureTabTranslation.findViewById(R.id.iv_capture_tab_translation);
		TextView tvCaptureTabTranslation = (TextView) this.llCaptureTabTranslation.findViewById(R.id.tv_capture_tab_translation);

		// reset
		ivCaptureTabQrcode.setImageResource(R.drawable.scan_tab_qrcode_normal);
		tvCaptureTabQrcode.setTextColor(this.colorScanTabNormal);
		ivCaptureTabCover.setImageResource(R.drawable.scan_tab_cover_normal);
		tvCaptureTabCover.setTextColor(this.colorScanTabNormal);
		ivCaptureTabStreetscape.setImageResource(R.drawable.scan_tab_streetscape_normal);
		tvCaptureTabStreetscape.setTextColor(this.colorScanTabNormal);
		ivCaptureTabTranslation.setImageResource(R.drawable.scan_tab_translation_normal);
		tvCaptureTabTranslation.setTextColor(this.colorScanTabNormal);

		switch (tabId) {
		case R.id.ll_capture_tab_qrcode:
			ivCaptureTabQrcode.setImageResource(R.drawable.scan_tab_qrcode_select);
			tvCaptureTabQrcode.setTextColor(this.colorScanTabSelect);
			break;
		case R.id.ll_capture_tab_cover:
			ivCaptureTabCover.setImageResource(R.drawable.scan_tab_cover_select);
			tvCaptureTabCover.setTextColor(this.colorScanTabSelect);
			break;
		case R.id.ll_capture_tab_streetscape:
			ivCaptureTabStreetscape.setImageResource(R.drawable.scan_tab_streetscape_select);
			tvCaptureTabStreetscape.setTextColor(this.colorScanTabSelect);
			break;
		case R.id.ll_capture_tab_translation:
			ivCaptureTabTranslation.setImageResource(R.drawable.scan_tab_translation_select);
			tvCaptureTabTranslation.setTextColor(this.colorScanTabSelect);
			break;
		}
	}

}
