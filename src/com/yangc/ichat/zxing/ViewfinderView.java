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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.yangc.ichat.R;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.zxing.camera.CameraManager;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

	private static final long ANIMATION_DELAY = 80; // 页面刷新间隔
	private static final int CURRENT_POINT_OPACITY = 0xFF; // 显示图片时的背景色

	private final int CORNER_WIDTH; // 四个绿色边角对应的宽度
	private final int CORNER_HEIGHT; // 四个绿色边角对应的长度

	private final int MIDDLE_LINE_CORNER; // 扫描框中的中间线四边的角度
	private final int MIDDLE_LINE_WIDTH; // 扫描框中的中间线的宽度
	private final int MIDDLE_LINE_PADDING; // 扫描框中的中间线的与扫描框左右的间隙
	private final int SPEEN_DISTANCE; // 中间那条线每次刷新移动的距离

	private final int PROMPT_FONT_SIZE; // 扫描框下面提示的字体大小
	private final int PROMPT_PADDING_TOP; // 扫描框下面提示的上边距

	private boolean isFirst; // 是否为第一次渲染页面
	private int slideTop; // 中间滑动线的最顶端位置

	private CameraManager cameraManager;
	private String scanPrompt;
	private final Paint paint;
	private final int scanMaskColor;
	private final int scanResultColor;
	private final int scanLineLeftRightColor;
	private final int scanLineCenterColor;
	private Bitmap resultBitmap;

	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Initialize these once for performance rather than calling them every time in onDraw().
		CORNER_WIDTH = AndroidUtils.dp2px(context, 4f);
		CORNER_HEIGHT = AndroidUtils.dp2px(context, 18f);

		MIDDLE_LINE_CORNER = AndroidUtils.dp2px(context, 30f);
		MIDDLE_LINE_WIDTH = AndroidUtils.dp2px(context, 4f);
		MIDDLE_LINE_PADDING = AndroidUtils.dp2px(context, 6f);
		SPEEN_DISTANCE = AndroidUtils.dp2px(context, 3f);

		PROMPT_FONT_SIZE = AndroidUtils.sp2px(context, 16f);
		PROMPT_PADDING_TOP = AndroidUtils.dp2px(context, 30f);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Resources resources = getResources();
		scanPrompt = resources.getString(R.string.scan_prompt);
		scanMaskColor = resources.getColor(R.color.scan_mask);
		scanResultColor = resources.getColor(R.color.scan_result);
		scanLineLeftRightColor = resources.getColor(R.color.scan_line_left_right);
		scanLineCenterColor = resources.getColor(R.color.scan_line_center);
	}

	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	public void setScanPrompt(String scanPrompt) {
		this.scanPrompt = scanPrompt;
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		if (cameraManager == null) {
			return; // not ready yet, early draw before done configuring
		}
		// 中间的扫描框, CameraManager里面修改扫描框的大小
		Rect frame = cameraManager.getFramingRect();
		Rect previewFrame = cameraManager.getFramingRectInPreview();
		if (frame == null || previewFrame == null) {
			return;
		}
		// 初始化中间线滑动的最上边
		if (!isFirst) {
			slideTop = frame.top;
			isFirst = true;
		}
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor(resultBitmap != null ? scanResultColor : scanMaskColor);
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(CURRENT_POINT_OPACITY);
			canvas.drawBitmap(resultBitmap, null, frame, paint);
		} else {
			// 绘制四角
			paint.setColor(scanLineCenterColor);
			canvas.drawRect(frame.left, frame.top, frame.left + CORNER_HEIGHT, frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top + CORNER_HEIGHT, paint);
			canvas.drawRect(frame.right - CORNER_HEIGHT, frame.top, frame.right, frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top + CORNER_HEIGHT, paint);
			canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left + CORNER_HEIGHT, frame.bottom, paint);
			canvas.drawRect(frame.left, frame.bottom - CORNER_HEIGHT, frame.left + CORNER_WIDTH, frame.bottom, paint);
			canvas.drawRect(frame.right - CORNER_HEIGHT, frame.bottom - CORNER_WIDTH, frame.right, frame.bottom, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - CORNER_HEIGHT, frame.right, frame.bottom, paint);

			// 绘制中间的线, 每次刷新页面, 中间的线往下移动SPEEN_DISTANCE
			slideTop += SPEEN_DISTANCE;
			if (slideTop >= frame.bottom) {
				slideTop = frame.top;
			}
			// 绘制中间线
			// canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH / 2, frame.right - MIDDLE_LINE_PADDING, slideTop + MIDDLE_LINE_WIDTH / 2, paint);
			Rect rect = new Rect();
			rect.set(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH / 2, frame.right - MIDDLE_LINE_PADDING, slideTop + MIDDLE_LINE_WIDTH / 2);
			GradientDrawable draw = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] { scanLineLeftRightColor, scanLineCenterColor, scanLineLeftRightColor });
			draw.setCornerRadii(new float[] { MIDDLE_LINE_CORNER, MIDDLE_LINE_CORNER, MIDDLE_LINE_CORNER, MIDDLE_LINE_CORNER, MIDDLE_LINE_CORNER, MIDDLE_LINE_CORNER, MIDDLE_LINE_CORNER,
					MIDDLE_LINE_CORNER });
			draw.setGradientType(GradientDrawable.LINEAR_GRADIENT);
			draw.setShape(GradientDrawable.RECTANGLE);
			draw.setBounds(rect);
			draw.draw(canvas);

			// 绘制提示信息
			paint.setColor(Color.WHITE);
			paint.setTextSize(PROMPT_FONT_SIZE);
			paint.setAlpha(0xC0);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			canvas.drawText(scanPrompt, (width - paint.measureText(scanPrompt)) / 2, (float) (frame.bottom + PROMPT_PADDING_TOP), paint);

			// Request another update at the animation interval
			// not the entire viewfinder mask.
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
		}
	}

	public void drawViewfinder() {
		Bitmap resultBitmap = this.resultBitmap;
		this.resultBitmap = null;
		if (resultBitmap != null) {
			resultBitmap.recycle();
		}
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live scanning display.
	 *
	 * @param barcode An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

}
