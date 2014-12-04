package com.yangc.ichat.fragment.tab;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yangc.ichat.R;
import com.yangc.ichat.activity.ChatActivity;
import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.database.bean.TIchatAddressbook;
import com.yangc.ichat.database.bean.TIchatHistory;
import com.yangc.ichat.fragment.tab.adapter.WechatFragmentAdapter;
import com.yangc.ichat.service.CallbackManager;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.DatabaseUtils;

public class WechatFragment extends Fragment implements CallbackManager.OnChatListener {

	private ListView lvWechat;
	private WechatFragmentAdapter adapter;

	private List<TIchatHistory> list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		CallbackManager.registerChatListener(this);

		View view = inflater.inflate(R.layout.fragment_tab_wechat, container, false);
		this.lvWechat = (ListView) view.findViewById(R.id.lv_wechat);
		this.lvWechat.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.list = DatabaseUtils.getHistoryList(this.getActivity());
		this.adapter = new WechatFragmentAdapter(this.getActivity(), this.lvWechat, this.list, this.itemListener, AndroidUtils.getScreenWidth(this.getActivity()));
		this.lvWechat.setAdapter(this.adapter);
	}

	@Override
	public void onChatReceived(TIchatHistory history) {
		this.list.clear();
		this.list.addAll(DatabaseUtils.getHistoryList(this.getActivity()));
		this.adapter.notifyDataSetChanged();
	}

	@Override
	public void onResultReceived(ResultBean result) {
		// TODO
	}

	@Override
	public void onNetworkError() {
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
		DatabaseUtils.deleteHistory(this.getActivity(), this.list.get(position).getUsername());
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

}
