package com.yangc.ichat.widget.swipe;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class SwipeMenuItem {

	private Context mContext;
	private int id;
	private int width;
	private Drawable background;
	private Drawable icon;
	private String title;
	private int titleColor;
	private int titleSize;

	public SwipeMenuItem(Context mContext) {
		this.mContext = mContext;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public Drawable getBackground() {
		return background;
	}

	public void setBackground(Drawable background) {
		this.background = background;
	}

	public void setBackground(int resId) {
		this.background = this.mContext.getResources().getDrawable(resId);
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public void setIcon(int resId) {
		this.icon = this.mContext.getResources().getDrawable(resId);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(int resId) {
		this.title = this.mContext.getResources().getString(resId);
	}

	public int getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(int titleColor) {
		this.titleColor = titleColor;
	}

	public int getTitleSize() {
		return titleSize;
	}

	public void setTitleSize(int titleSize) {
		this.titleSize = titleSize;
	}

}
