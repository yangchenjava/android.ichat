package com.yangc.ichat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ResizeLayout extends RelativeLayout {

	private OnResizeListener onResizeListener;

	public interface OnResizeListener {
		void onResize(int w, int h, int oldw, int oldh);
	}

	public void setOnResizeListener(OnResizeListener onResizeListener) {
		this.onResizeListener = onResizeListener;
	}

	public ResizeLayout(Context context) {
		super(context);
	}

	public ResizeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResizeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		if (this.onResizeListener != null) {
			this.onResizeListener.onResize(w, h, oldw, oldh);
		}
	}

}
