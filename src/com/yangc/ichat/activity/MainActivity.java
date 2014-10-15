package com.yangc.ichat.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yangc.ichat.R;

public class MainActivity extends FragmentActivity {

	private int colorTabNormal;
	private int colorTabSelect;

	private View rootView;
	private PopupWindow pw;
	private long popupWindowShowTime;

	private LinearLayout llTabWechat;
	private LinearLayout llTabAddressbook;
	private LinearLayout llTabFind;
	private LinearLayout llTabMe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		this.colorTabNormal = this.getResources().getColor(R.color.tab_normal);
		this.colorTabSelect = this.getResources().getColor(R.color.tab_select);

		this.initPopupWindow();

		((ImageView) this.findViewById(R.id.iv_title_bar_plus)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopupWindow();
			}
		});

		this.llTabWechat = (LinearLayout) this.findViewById(R.id.ll_tab_wechat);
		this.llTabAddressbook = (LinearLayout) this.findViewById(R.id.ll_tab_addressbook);
		this.llTabFind = (LinearLayout) this.findViewById(R.id.ll_tab_find);
		this.llTabMe = (LinearLayout) this.findViewById(R.id.ll_tab_me);

		this.llTabWechat.setOnClickListener(new ClickListener());
		this.llTabAddressbook.setOnClickListener(new ClickListener());
		this.llTabFind.setOnClickListener(new ClickListener());
		this.llTabMe.setOnClickListener(new ClickListener());
	}

	private void initPopupWindow() {
		// PopupWindow
		this.rootView = this.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
		// PopupWindow的子view
		View popupWindowView = this.getLayoutInflater().inflate(R.layout.popup_window, null);
		popupWindowView.setFocusableInTouchMode(true);
		popupWindowView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU && pw.isShowing() && System.currentTimeMillis() - popupWindowShowTime > 500) {
					pw.dismiss();
					return true;
				}
				return false;
			}
		});
		this.pw = new PopupWindow(popupWindowView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		// 设置点击返回键和PopupWindow以外的地方,退出
		this.pw.setBackgroundDrawable(new BitmapDrawable(this.getResources(), (Bitmap) null));
	}

	private void showPopupWindow() {
		// PopupWindow的父view
		this.pw.showAtLocation(this.rootView, Gravity.TOP | Gravity.RIGHT, 0, 120);
		this.pw.update();
	}

	private void resetView() {
		((ImageView) this.llTabWechat.findViewById(R.id.iv_tab_wechat)).setImageResource(R.drawable.tab_wechat_normal);
		((TextView) this.llTabWechat.findViewById(R.id.tv_tab_wechat)).setTextColor(this.colorTabNormal);
		((ImageView) this.llTabAddressbook.findViewById(R.id.iv_tab_addressbook)).setImageResource(R.drawable.tab_addressbook_normal);
		((TextView) this.llTabAddressbook.findViewById(R.id.tv_tab_addressbook)).setTextColor(this.colorTabNormal);
		((ImageView) this.llTabFind.findViewById(R.id.iv_tab_find)).setImageResource(R.drawable.tab_find_normal);
		((TextView) this.llTabFind.findViewById(R.id.tv_tab_find)).setTextColor(this.colorTabNormal);
		((ImageView) this.llTabMe.findViewById(R.id.iv_tab_me)).setImageResource(R.drawable.tab_me_normal);
		((TextView) this.llTabMe.findViewById(R.id.tv_tab_me)).setTextColor(this.colorTabNormal);
	}

	private class ClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			resetView();
			switch (v.getId()) {
			case R.id.ll_tab_wechat:
				((ImageView) llTabWechat.findViewById(R.id.iv_tab_wechat)).setImageResource(R.drawable.tab_wechat_select);
				((TextView) llTabWechat.findViewById(R.id.tv_tab_wechat)).setTextColor(colorTabSelect);
				break;
			case R.id.ll_tab_addressbook:
				((ImageView) llTabAddressbook.findViewById(R.id.iv_tab_addressbook)).setImageResource(R.drawable.tab_addressbook_select);
				((TextView) llTabAddressbook.findViewById(R.id.tv_tab_addressbook)).setTextColor(colorTabSelect);
				break;
			case R.id.ll_tab_find:
				((ImageView) llTabFind.findViewById(R.id.iv_tab_find)).setImageResource(R.drawable.tab_find_select);
				((TextView) llTabFind.findViewById(R.id.tv_tab_find)).setTextColor(colorTabSelect);
				break;
			case R.id.ll_tab_me:
				((ImageView) llTabMe.findViewById(R.id.iv_tab_me)).setImageResource(R.drawable.tab_me_select);
				((TextView) llTabMe.findViewById(R.id.tv_tab_me)).setTextColor(colorTabSelect);
				break;
			}
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (!this.pw.isShowing()) {
			this.showPopupWindow();
			this.popupWindowShowTime = System.currentTimeMillis();
		}
		return true;
	}

}
