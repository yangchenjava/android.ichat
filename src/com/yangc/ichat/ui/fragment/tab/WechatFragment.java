package com.yangc.ichat.ui.fragment.tab;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yangc.ichat.R;
import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.database.bean.TIchatAddressbook;
import com.yangc.ichat.database.bean.TIchatHistory;
import com.yangc.ichat.ui.activity.ChatActivity;
import com.yangc.ichat.ui.component.recyclerview.HorizontalDividerItemDecoration;
import com.yangc.ichat.ui.component.recyclerview.PauseOnScrollListener;
import com.yangc.ichat.ui.fragment.tab.adapter.WechatFragmentAdapter;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;

import de.greenrobot.event.EventBus;

public class WechatFragment extends Fragment {

	// private ListView lvWechat;
	private RecyclerView rvWechat;
	private WechatFragmentAdapter adapter;

	private PauseOnScrollListener mPauseOnScrollListener;

	private List<TIchatHistory> list;

	@Override
	@SuppressLint("NewApi")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tab_wechat, container, false);
		// this.lvWechat = (ListView) view.findViewById(R.id.lv_wechat);
		// this.lvWechat.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
		this.rvWechat = (RecyclerView) view.findViewById(R.id.rv_wechat);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			this.rvWechat.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		this.rvWechat.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this.getActivity()).colorResId(R.color.dividing_line).showLastDivider().build());
		this.rvWechat.setLayoutManager(new LinearLayoutManager(this.getActivity()));
		this.rvWechat.setItemAnimator(new DefaultItemAnimator());
		this.mPauseOnScrollListener = new PauseOnScrollListener(ImageLoader.getInstance(), false, true);
		this.rvWechat.addOnScrollListener(this.mPauseOnScrollListener);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// CallbackManager.registerChatListener(this);
		EventBus.getDefault().register(this);

		this.list = DatabaseUtils.getHistoryList(this.getActivity());
		this.adapter = new WechatFragmentAdapter(this.getActivity(), this.rvWechat, this.list, this.itemListener, AndroidUtils.getScreenWidth(this.getActivity()));
		this.rvWechat.setAdapter(this.adapter);
	}

	@Override
	public void onPause() {
		super.onPause();
		// CallbackManager.unregisterChatListener(this);
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		this.rvWechat.removeOnScrollListener(this.mPauseOnScrollListener);
	}

	// @Override
	// public void onChatReceived(TIchatHistory history) {
	// this.list.clear();
	// this.list.addAll(DatabaseUtils.getHistoryList(this.getActivity()));
	// this.adapter.notifyDataSetChanged();
	// }
	//
	// @Override
	// public void onResultReceived(ResultBean result) {
	// this.list.clear();
	// this.list.addAll(DatabaseUtils.getHistoryList(this.getActivity()));
	// this.adapter.notifyDataSetChanged();
	// }
	//
	// @Override
	// public void onNetworkError() {
	// Activity activity = this.getActivity();
	// if (activity != null) {
	// AndroidUtils.alertToast(activity, R.string.error_network);
	// }
	// }

	public void onEventMainThread(TIchatHistory history) {
		this.list.clear();
		this.list.addAll(DatabaseUtils.getHistoryList(this.getActivity()));
		this.adapter.notifyDataSetChanged();
	}

	public void onEventMainThread(ResultBean result) {
		this.list.clear();
		this.list.addAll(DatabaseUtils.getHistoryList(this.getActivity()));
		this.adapter.notifyDataSetChanged();
	}

	public void onEventMainThread(String what) {
		Activity activity = this.getActivity();
		if (activity != null) {
			AndroidUtils.alertToast(activity, R.string.error_network);
		}
	}

	private WechatFragmentAdapter.OnItemListener itemListener = new WechatFragmentAdapter.OnItemListener() {
		@Override
		public void onItemClick(int position) {
			TIchatHistory history = list.get(position);
			Intent intent = new Intent(getActivity(), ChatActivity.class);
			intent.putExtra("username", history.getUsername());
			getActivity().startActivity(intent);
		}

		@Override
		public void onItemLongClick(final int position) {
			TIchatAddressbook addressbook = DatabaseUtils.getAddressbookByUsername(getActivity(), list.get(position).getUsername());
			final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).show();
			alertDialog.setCanceledOnTouchOutside(true);
			Window window = alertDialog.getWindow();
			window.setContentView(R.layout.dialog_select);
			((TextView) window.findViewById(R.id.tv_dialog_select_title)).setText(TextUtils.isEmpty(addressbook.getNickname()) ? getResources().getString(R.string.dialog_remove_history) : addressbook
					.getNickname());
			((TextView) window.findViewById(R.id.tv_dialog_select_first)).setText(R.string.dialog_remove_history);
			((RelativeLayout) window.findViewById(R.id.rl_dialog_select_first)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
					removeData(position);
				}
			});
			((TextView) window.findViewById(R.id.tv_dialog_select_line)).setVisibility(View.GONE);
			((RelativeLayout) window.findViewById(R.id.rl_dialog_select_second)).setVisibility(View.GONE);
		}

		@Override
		public void onRemoveClick(int position) {
			removeData(position);
		}
	};

	private void removeData(int position) {
		final Dialog progressDialog = AndroidUtils.showProgressDialog(this.getActivity(), this.getResources().getString(R.string.text_load), true, true);
		String username = this.list.get(position).getUsername();
		DatabaseUtils.deleteHistory(this.getActivity(), username);
		// 删除该会话下的语音文件
		new Thread(new DeleteDirectory(username)).start();
		this.list.clear();
		this.list.addAll(DatabaseUtils.getHistoryList(this.getActivity()));
		this.adapter.notifyDataSetChanged();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				progressDialog.dismiss();
			}
		}, 200);
	}

	private class DeleteDirectory implements Runnable {
		private String username;

		private DeleteDirectory(String username) {
			this.username = username;
		}

		@Override
		public void run() {
			File dir = AndroidUtils.getStorageDir(getActivity(), Constants.APP + "/" + Constants.CACHE_VOICE + "/" + this.username);
			if (dir.exists()) {
				if (dir.isDirectory()) {
					for (File file : dir.listFiles()) {
						file.delete();
					}
				}
				dir.delete();
			}
		}
	}

}
