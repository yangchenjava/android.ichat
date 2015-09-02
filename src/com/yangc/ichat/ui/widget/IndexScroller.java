package com.yangc.ichat.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yangc.ichat.utils.AndroidUtils;

public class IndexScroller extends View {

	private OnTouchWordChangedListener onTouchWordChangedListener;
	private String[] words = { "★", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };
	private int choose = -1;
	private Paint paint = new Paint();
	private boolean showBackground;

	public interface OnTouchWordChangedListener {
		public void onTouchWordChanged(String word);

		public void onTouchWordLeft();
	}

	public IndexScroller(Context context) {
		super(context);
	}

	public IndexScroller(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndexScroller(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnTouchWordChangedListener(OnTouchWordChangedListener onTouchWordChangedListener) {
		this.onTouchWordChangedListener = onTouchWordChangedListener;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (this.showBackground) {
			canvas.drawColor(Color.parseColor("#40000000"));
		}

		int height = this.getHeight();
		int width = this.getWidth();
		int singleHeight = height / this.words.length;
		for (int i = 0; i < this.words.length; i++) {
			if (this.showBackground) {
				this.paint.setColor(Color.WHITE);
			} else {
				this.paint.setColor(Color.parseColor("#88888D"));
			}
			// this.paint.setTypeface(Typeface.DEFAULT_BOLD);
			// 消除字体锯齿
			this.paint.setAntiAlias(true);
			this.paint.setTextSize(AndroidUtils.sp2px(this.getContext(), 12));
			if (i == this.choose) {
				this.paint.setColor(Color.parseColor("#38C03F"));
				this.paint.setFakeBoldText(true);
			}
			float xPos = width / 2 - this.paint.measureText(this.words[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(this.words[i], xPos, yPos, this.paint);
			this.paint.reset();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		// event.getY() / this.getHeight() == c / words.length
		int c = (int) (event.getY() / this.getHeight() * this.words.length);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			this.showBackground = true;
			if (this.choose != c && this.onTouchWordChangedListener != null) {
				if (c >= 0 && c < this.words.length) {
					this.onTouchWordChangedListener.onTouchWordChanged(this.words[c]);
					this.choose = c;
					this.invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (this.choose != c && this.onTouchWordChangedListener != null) {
				// 手指滑动右侧边的区域, 超过指定区域, 功能失效
				if (event.getX() <= -20) {
					this.showBackground = false;
					this.onTouchWordChangedListener.onTouchWordLeft();
					this.choose = -1;
					this.invalidate();
				} else if (c >= 0 && c < this.words.length && this.showBackground) {
					this.onTouchWordChangedListener.onTouchWordChanged(this.words[c]);
					this.choose = c;
					this.invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			this.showBackground = false;
			this.onTouchWordChangedListener.onTouchWordLeft();
			this.choose = -1;
			this.invalidate();
			break;
		}
		return true;
	}

}
