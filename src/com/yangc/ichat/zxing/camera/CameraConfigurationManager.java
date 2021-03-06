/*
 * Copyright (C) 2010 ZXing authors
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

package com.yangc.ichat.zxing.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.yangc.ichat.utils.PreferenceUtils;
import com.yangc.ichat.zxing.PreferencesActivity;

/**
 * A class which deals with reading, parsing, and setting the camera parameters which are used to configure the camera hardware.
 */
@SuppressWarnings("deprecation")
public final class CameraConfigurationManager {

	private static final String TAG = CameraConfigurationManager.class.getSimpleName();

	private final Context context;
	private Point screenResolution;
	private Point cameraResolution;

	CameraConfigurationManager(Context context) {
		this.context = context;
	}

	/**
	 * Reads, one time, values from the camera that are needed by the app.
	 */
	@SuppressLint("NewApi")
	void initFromCameraParameters(Camera camera) {
		camera.setDisplayOrientation(90); // 转为竖屏
		Camera.Parameters parameters = camera.getParameters();
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point theScreenResolution = new Point();
			display.getSize(theScreenResolution);
			screenResolution = theScreenResolution;
		} else {
			screenResolution = new Point(display.getWidth(), display.getHeight());
		}
		Log.i(TAG, "Screen resolution: " + screenResolution);

		// 转为竖屏
		Point screenResolutionForCamera = new Point();
		screenResolutionForCamera.x = screenResolution.x;
		screenResolutionForCamera.y = screenResolution.y;
		// preview size is always something like 480*320, other 320*480
		if (screenResolution.x < screenResolution.y) {
			screenResolutionForCamera.x = screenResolution.y;
			screenResolutionForCamera.y = screenResolution.x;
		}
		cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolutionForCamera);
		// cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolution);
		Log.i(TAG, "Camera resolution: " + cameraResolution);
	}

	void setDesiredCameraParameters(Camera camera, boolean safeMode) {
		Camera.Parameters parameters = camera.getParameters();

		if (parameters == null) {
			Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
			return;
		}

		Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

		if (safeMode) {
			Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
		}

		initializeTorch(parameters, safeMode);

		CameraConfigurationUtils.setFocus(parameters, PreferenceUtils.getBoolean(context, PreferencesActivity.KEY_AUTO_FOCUS, true),
				PreferenceUtils.getBoolean(context, PreferencesActivity.KEY_DISABLE_CONTINUOUS_FOCUS, true), safeMode);

		if (!safeMode) {
			if (PreferenceUtils.getBoolean(context, PreferencesActivity.KEY_INVERT_SCAN, false)) {
				CameraConfigurationUtils.setInvertColor(parameters);
			}

			if (!PreferenceUtils.getBoolean(context, PreferencesActivity.KEY_DISABLE_BARCODE_SCENE_MODE, true)) {
				CameraConfigurationUtils.setBarcodeSceneMode(parameters);
			}

			if (!PreferenceUtils.getBoolean(context, PreferencesActivity.KEY_DISABLE_METERING, true)) {
				CameraConfigurationUtils.setVideoStabilization(parameters);
				CameraConfigurationUtils.setFocusArea(parameters);
				CameraConfigurationUtils.setMetering(parameters);
			}
		}

		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);

		Log.i(TAG, "Final camera parameters: " + parameters.flatten());

		camera.setParameters(parameters);

		Camera.Parameters afterParameters = camera.getParameters();
		Camera.Size afterSize = afterParameters.getPreviewSize();
		if (afterSize != null && (cameraResolution.x != afterSize.width || cameraResolution.y != afterSize.height)) {
			Log.w(TAG, "Camera said it supported preview size " + cameraResolution.x + 'x' + cameraResolution.y + ", but after setting it, preview size is " + afterSize.width + 'x' + afterSize.height);
			cameraResolution.x = afterSize.width;
			cameraResolution.y = afterSize.height;
		}
	}

	Point getCameraResolution() {
		return cameraResolution;
	}

	Point getScreenResolution() {
		return screenResolution;
	}

	boolean getTorchState(Camera camera) {
		if (camera != null) {
			Camera.Parameters parameters = camera.getParameters();
			if (parameters != null) {
				String flashMode = parameters.getFlashMode();
				return flashMode != null && (Camera.Parameters.FLASH_MODE_ON.equals(flashMode) || Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode));
			}
		}
		return false;
	}

	void setTorch(Camera camera, boolean newSetting) {
		Camera.Parameters parameters = camera.getParameters();
		doSetTorch(parameters, newSetting, false);
		camera.setParameters(parameters);
	}

	private void initializeTorch(Camera.Parameters parameters, boolean safeMode) {
		boolean currentSetting = FrontLightMode.readPref(context) == FrontLightMode.ON;
		doSetTorch(parameters, currentSetting, safeMode);
	}

	private void doSetTorch(Camera.Parameters parameters, boolean newSetting, boolean safeMode) {
		CameraConfigurationUtils.setTorch(parameters, newSetting);
		if (!safeMode && !PreferenceUtils.getBoolean(context, PreferencesActivity.KEY_DISABLE_EXPOSURE, true)) {
			CameraConfigurationUtils.setBestExposure(parameters, newSetting);
		}
	}

}
