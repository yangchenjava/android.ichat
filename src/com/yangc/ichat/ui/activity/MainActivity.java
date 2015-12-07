package com.yangc.ichat.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SparseArrayCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yangc.ichat.R;
import com.yangc.ichat.service.PushService;
import com.yangc.ichat.ui.fragment.tab.AddressbookFragment;
import com.yangc.ichat.ui.fragment.tab.FindFragment;
import com.yangc.ichat.ui.fragment.tab.MeFragment;
import com.yangc.ichat.ui.fragment.tab.WechatFragment;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.utils.VolleyUtils;
import com.yangc.ichat.zxing.CaptureActivity;

public class MainActivity extends FragmentActivity {

	public static final String TAG = MainActivity.class.getSimpleName();

	public static int CURRENT_TAB_ID;

	private static final int REQUEST_CODE = 1;

	private int colorTabNormal;
	private int colorTabSelect;

	private PopupWindow mPopupWindow;
	private ImageView ivTitleBarPlus;

	private LinearLayout llTabWechat;
	private LinearLayout llTabAddressbook;
	private LinearLayout llTabFind;
	private LinearLayout llTabMe;

	private SparseArrayCompat<Fragment> fragments;

	private Dialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		// 启动后台TCP连接
		this.startService(new Intent(this, PushService.class));

		this.colorTabNormal = this.getResources().getColor(R.color.tab_normal);
		this.colorTabSelect = this.getResources().getColor(R.color.tab_select);

		this.initPopupWindow();

		this.ivTitleBarPlus = (ImageView) this.findViewById(R.id.iv_title_bar_plus);
		this.ivTitleBarPlus.setOnClickListener(new View.OnClickListener() {
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

		this.fragments = new SparseArrayCompat<Fragment>(4);
		this.fragments.put(R.id.ll_tab_wechat, new WechatFragment());
		this.fragments.put(R.id.ll_tab_addressbook, new AddressbookFragment());
		this.fragments.put(R.id.ll_tab_find, new FindFragment());
		this.fragments.put(R.id.ll_tab_me, new MeFragment());
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.showView(CURRENT_TAB_ID == 0 ? DatabaseUtils.getAddressbookCount(this) == 0 ? R.id.ll_tab_addressbook : R.id.ll_tab_wechat : CURRENT_TAB_ID);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (this.progressDialog != null) {
			this.progressDialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		VolleyUtils.cancelAllRequest(TAG);
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
			this.startActivity(data.setClass(this, BrowserActivity.class));
		}
	}

	@Override
	public void onBackPressed() {
		this.moveTaskToBack(true);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
			return this.showPopupWindow();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initPopupWindow() {
		// PopupWindow
		View popupWindowView = View.inflate(this, R.layout.popup_window, null);
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
		popupWindowView.findViewById(R.id.ll_popup_window_scan).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closePopupWindow();
				progressDialog = AndroidUtils.showProgressDialog(MainActivity.this, getResources().getString(R.string.text_load), false, false);
				startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_CODE);
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
			this.mPopupWindow.showAsDropDown(this.ivTitleBarPlus);
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

	public void showView(int tabId) {
		// resetView
		((ImageView) this.llTabWechat.findViewById(R.id.iv_tab_wechat)).setImageResource(R.drawable.tab_wechat_normal);
		((TextView) this.llTabWechat.findViewById(R.id.tv_tab_wechat)).setTextColor(this.colorTabNormal);
		((ImageView) this.llTabAddressbook.findViewById(R.id.iv_tab_addressbook)).setImageResource(R.drawable.tab_addressbook_normal);
		((TextView) this.llTabAddressbook.findViewById(R.id.tv_tab_addressbook)).setTextColor(this.colorTabNormal);
		((ImageView) this.llTabFind.findViewById(R.id.iv_tab_find)).setImageResource(R.drawable.tab_find_normal);
		((TextView) this.llTabFind.findViewById(R.id.tv_tab_find)).setTextColor(this.colorTabNormal);
		((ImageView) this.llTabMe.findViewById(R.id.iv_tab_me)).setImageResource(R.drawable.tab_me_normal);
		((TextView) this.llTabMe.findViewById(R.id.tv_tab_me)).setTextColor(this.colorTabNormal);

		// initView
		switch (tabId) {
		case R.id.ll_tab_wechat:
			((ImageView) this.llTabWechat.findViewById(R.id.iv_tab_wechat)).setImageResource(R.drawable.tab_wechat_select);
			((TextView) this.llTabWechat.findViewById(R.id.tv_tab_wechat)).setTextColor(this.colorTabSelect);
			break;
		case R.id.ll_tab_addressbook:
			((ImageView) this.llTabAddressbook.findViewById(R.id.iv_tab_addressbook)).setImageResource(R.drawable.tab_addressbook_select);
			((TextView) this.llTabAddressbook.findViewById(R.id.tv_tab_addressbook)).setTextColor(this.colorTabSelect);
			break;
		case R.id.ll_tab_find:
			((ImageView) this.llTabFind.findViewById(R.id.iv_tab_find)).setImageResource(R.drawable.tab_find_select);
			((TextView) this.llTabFind.findViewById(R.id.tv_tab_find)).setTextColor(this.colorTabSelect);
			break;
		case R.id.ll_tab_me:
			((ImageView) this.llTabMe.findViewById(R.id.iv_tab_me)).setImageResource(R.drawable.tab_me_select);
			((TextView) this.llTabMe.findViewById(R.id.tv_tab_me)).setTextColor(this.colorTabSelect);
			break;
		}
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		if (this.getSupportFragmentManager().getFragments() != null) {
			for (Fragment fragment : this.getSupportFragmentManager().getFragments()) {
				fragmentTransaction.hide(fragment);
			}
		}
		if (this.fragments.get(tabId).isAdded()) {
			fragmentTransaction.show(this.fragments.get(tabId));
		} else {
			fragmentTransaction.add(R.id.rl_main, this.fragments.get(tabId), "" + tabId);
		}
		fragmentTransaction.commit();
		CURRENT_TAB_ID = tabId;
	}

	private class ClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			showView(v.getId());
		}
	}

}
