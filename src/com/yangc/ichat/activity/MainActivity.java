package com.yangc.ichat.activity;

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
import com.yangc.ichat.fragment.tab.AddressbookFragment;
import com.yangc.ichat.fragment.tab.FindFragment;
import com.yangc.ichat.fragment.tab.MeFragment;
import com.yangc.ichat.fragment.tab.WechatFragment;
import com.yangc.ichat.utils.VolleyUtils;

public class MainActivity extends FragmentActivity {

	public static final String TAG = MainActivity.class.getName();

	private static int CURRENT_TAB_ID;

	private int colorTabNormal;
	private int colorTabSelect;

	private PopupWindow mPopupWindow;
	private ImageView ivTitleBarPlus;

	private LinearLayout llTabWechat;
	private LinearLayout llTabAddressbook;
	private LinearLayout llTabFind;
	private LinearLayout llTabMe;

	private SparseArrayCompat<Fragment> fragments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

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
		this.resetView();
		this.initView(CURRENT_TAB_ID == 0 ? R.id.ll_tab_wechat : CURRENT_TAB_ID);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VolleyUtils.cancelAllRequest(TAG);
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
		View popupWindowView = this.getLayoutInflater().inflate(R.layout.popup_window, null);
		this.mPopupWindow = new PopupWindow(popupWindowView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		// 设置点击返回键和PopupWindow以外的地方,退出
		this.mPopupWindow.setTouchable(true);
		this.mPopupWindow.setOutsideTouchable(true);
		this.mPopupWindow.setBackgroundDrawable(new BitmapDrawable(this.getResources(), (Bitmap) null));

		this.mPopupWindow.getContentView().setFocusable(true);
		this.mPopupWindow.getContentView().setFocusableInTouchMode(true);
		this.mPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
					if (mPopupWindow != null && mPopupWindow.isShowing()) {
						mPopupWindow.dismiss();
						return true;
					}
				}
				return false;
			}
		});
	}

	private boolean showPopupWindow() {
		if (this.mPopupWindow != null && !this.mPopupWindow.isShowing()) {
			this.mPopupWindow.showAsDropDown(this.ivTitleBarPlus);
			return true;
		}
		return false;
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

	private void initView(int tabId) {
		CURRENT_TAB_ID = tabId;
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
		fragmentTransaction.replace(R.id.rl_main, this.fragments.get(tabId));
		fragmentTransaction.commit();
	}

	private class ClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			resetView();
			initView(v.getId());
		}
	}

}
