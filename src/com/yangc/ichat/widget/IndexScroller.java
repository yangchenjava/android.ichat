package com.yangc.ichat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class IndexScroller extends View {

	private WordChanged onTouchWordChangedListener;
	private char[] words = { '★', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '#' };
	private int choose = -1;
	private Paint paint = new Paint();
	private boolean showBackground;

	public interface WordChanged {
		public void onTouchWordChanged(String word);
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

	public void setOnTouchWordChangedListener(WordChanged onTouchWordChangedListener) {
		this.onTouchWordChangedListener = onTouchWordChangedListener;
	}

	public void setWords(String words) {
		this.words = words.toCharArray();
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
				this.paint.setColor(Color.parseColor("#40474F"));
			}
			this.paint.setTypeface(Typeface.DEFAULT_BOLD);
			// 消除字体锯齿
			this.paint.setAntiAlias(true);
			this.paint.setTextSize(18);
			if (i == this.choose) {
				this.paint.setColor(Color.parseColor("#3399FF"));
				this.paint.setFakeBoldText(true);
			}
			float xPos = width / 2 - this.paint.measureText(String.valueOf(this.words[i])) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(String.valueOf(this.words[i]), xPos, yPos, this.paint);
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
					this.onTouchWordChangedListener.onTouchWordChanged(String.valueOf(this.words[c]));
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
					this.choose = -1;
					this.invalidate();
				} else if (c >= 0 && c < this.words.length && this.showBackground) {
					this.onTouchWordChangedListener.onTouchWordChanged(String.valueOf(this.words[c]));
					this.choose = c;
					this.invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			this.showBackground = false;
			this.choose = -1;
			this.invalidate();
			break;
		}
		return true;
	}

}
