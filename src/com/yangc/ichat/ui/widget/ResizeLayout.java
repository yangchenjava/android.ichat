package com.yangc.ichat.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ResizeLayout extends LinearLayout {

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

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		if (this.onResizeListener != null) {
			this.onResizeListener.onResize(w, h, oldw, oldh);
		}
	}

}
