package com.yangc.ichat.fragment.tab;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
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
import com.yangc.ichat.activity.MainActivity;
import com.yangc.ichat.database.bean.TIchatAddressbook;
import com.yangc.ichat.database.bean.TIchatHistory;
import com.yangc.ichat.fragment.tab.adapter.WechatFragmentAdapter;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.DatabaseUtils;

public class WechatFragment extends Fragment {

	private MainActivity mainActivity;

	private ListView lvWechat;
	private WechatFragmentAdapter adapter;

	private List<TIchatHistory> list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mainActivity = (MainActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_tab_wechat, container, false);
		this.lvWechat = (ListView) view.findViewById(R.id.lv_wechat);
		this.lvWechat.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.list = DatabaseUtils.getHistoryList(this.mainActivity);
		this.adapter = new WechatFragmentAdapter(this.mainActivity, this.lvWechat, this.list, this.itemListener, AndroidUtils.getScreenWidth(this.mainActivity));
		this.lvWechat.setAdapter(this.adapter);
	}

	private WechatFragmentAdapter.OnItemListener itemListener = new WechatFragmentAdapter.OnItemListener() {
		@Override
		public void onItemClick(int position) {
			TIchatHistory history = list.get(position);
		}

		@Override
		public void onItemLongClick(final int position) {
			TIchatAddressbook addressbook = DatabaseUtils.getAddressbookByUsername(mainActivity, list.get(position).getUsername());
			final AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).show();
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
		Dialog progressDialog = AndroidUtils.showProgressDialog(this.mainActivity, this.getResources().getString(R.string.text_load), true, true);
		DatabaseUtils.deleteHistory(this.mainActivity, this.list.get(position).getUsername());
		progressDialog.dismiss();
	}

}
