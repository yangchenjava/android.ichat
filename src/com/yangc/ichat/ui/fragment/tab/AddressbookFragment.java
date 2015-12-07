package com.yangc.ichat.ui.fragment.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yangc.ichat.R;
import com.yangc.ichat.bean.ResultBean;
import com.yangc.ichat.database.bean.TIchatAddressbook;
import com.yangc.ichat.http.volley.GsonArrayRequest;
import com.yangc.ichat.http.volley.GsonObjectRequest;
import com.yangc.ichat.http.volley.VolleyErrorHelper;
import com.yangc.ichat.ui.activity.FriendActivity;
import com.yangc.ichat.ui.activity.MainActivity;
import com.yangc.ichat.ui.component.recyclerview.HorizontalDividerItemDecoration;
import com.yangc.ichat.ui.component.recyclerview.PauseOnScrollListener;
import com.yangc.ichat.ui.fragment.tab.adapter.AddressbookFragmentAdapter;
import com.yangc.ichat.ui.widget.IndexScroller;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.utils.VolleyUtils;

public class AddressbookFragment extends Fragment {

	// private ListView lvAddressbook;
	private RecyclerView rvAddressbook;
	private TextView tvIndexWord;
	// private TextView tvAddressbookItemTotal;
	private RecyclerView.ItemDecoration itemDecoration;
	private AddressbookFragmentAdapter adapter;

	private PauseOnScrollListener mPauseOnScrollListener;

	private List<TIchatAddressbook> list;
	private Map<String, Integer> map;

	private Request<List<TIchatAddressbook>> syncNetworkData;
	private Request<ResultBean> removeData;

	@Override
	@SuppressLint("NewApi")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tab_addressbook, container, false);
		// this.lvAddressbook = (ListView) view.findViewById(R.id.lv_addressbook);
		// this.lvAddressbook.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
		this.rvAddressbook = (RecyclerView) view.findViewById(R.id.rv_addressbook);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			this.rvAddressbook.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		this.rvAddressbook.setLayoutManager(new LinearLayoutManager(this.getActivity()));
		this.rvAddressbook.setItemAnimator(new DefaultItemAnimator());
		this.mPauseOnScrollListener = new PauseOnScrollListener(ImageLoader.getInstance(), false, true);
		this.rvAddressbook.addOnScrollListener(this.mPauseOnScrollListener);
		IndexScroller isIndex = (IndexScroller) view.findViewById(R.id.is_index);
		isIndex.setOnTouchWordChangedListener(this.touchWordChangedListener);
		this.tvIndexWord = (TextView) view.findViewById(R.id.tv_index_word);

		// View footer = inflater.inflate(R.layout.fragment_tab_addressbook_total, this.lvAddressbook, false);
		// this.tvAddressbookItemTotal = (TextView) footer.findViewById(R.id.tv_addressbook_item_total);
		// this.lvAddressbook.addFooterView(footer);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.list = new ArrayList<TIchatAddressbook>();
		this.map = new HashMap<String, Integer>();
		List<TIchatAddressbook> addressbookList = DatabaseUtils.getAddressbookList(this.getActivity());
		if (AndroidUtils.checkNetwork(this.getActivity()) && addressbookList.isEmpty()) {
			this.syncNetworkData();
		} else {
			this.loadData(addressbookList);
			this.syncNetworkData();
		}

		this.adapter = new AddressbookFragmentAdapter(this.rvAddressbook, list, this.itemListener, AndroidUtils.getScreenWidth(this.getActivity()));
		this.rvAddressbook.setAdapter(this.adapter);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		this.rvAddressbook.removeOnScrollListener(this.mPauseOnScrollListener);
	}

	private AddressbookFragmentAdapter.OnItemListener itemListener = new AddressbookFragmentAdapter.OnItemListener() {
		@Override
		public void onItemClick(int position) {
			TIchatAddressbook addressbook = list.get(position);
			if (addressbook.getId() != null) {
				Intent intent = new Intent(getActivity(), FriendActivity.class);
				Bundle bundle = new Bundle(6);
				bundle.putString("nickname", addressbook.getNickname());
				bundle.putLong("sex", addressbook.getSex());
				bundle.putString("phone", addressbook.getPhone());
				bundle.putString("photo", addressbook.getPhoto());
				bundle.putString("signature", addressbook.getSignature());
				bundle.putString("username", addressbook.getUsername());
				intent.putExtra("addressbook", bundle);
				getActivity().startActivity(intent);
			}
		}

		@Override
		public void onItemLongClick(final int position) {
			final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).show();
			alertDialog.setCanceledOnTouchOutside(true);
			Window window = alertDialog.getWindow();
			window.setContentView(R.layout.dialog_select);
			((TextView) window.findViewById(R.id.tv_dialog_select_title)).setText(list.get(position).getNickname());
			((TextView) window.findViewById(R.id.tv_dialog_select_first)).setText(R.string.dialog_remove_addressbook);
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
		Dialog progressDialog = AndroidUtils.showProgressDialog(this.getActivity(), this.getResources().getString(R.string.text_load), true, true);

		Long friendId = this.list.get(position).getUserId();
		DatabaseUtils.deleteAddressbook_logic(this.getActivity(), friendId);
		this.loadData(DatabaseUtils.getAddressbookList(this.getActivity()));
		this.adapter.notifyDataSetChanged();

		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Constants.USER_ID);
		params.put("friendId", "" + friendId);
		this.removeData = new GsonObjectRequest<ResultBean>(Request.Method.POST, Constants.DELETE_FRIEND, params, ResultBean.class, removeDataListener, removeDataErrorListener);
		VolleyUtils.addNormalRequest(this.removeData, MainActivity.TAG);

		progressDialog.dismiss();
	}

	private IndexScroller.OnTouchWordChangedListener touchWordChangedListener = new IndexScroller.OnTouchWordChangedListener() {
		@Override
		public void onTouchWordChanged(String word) {
			if (map != null && map.get(word) != null) {
				// lvAddressbook.setSelection(map.get(word));
				rvAddressbook.scrollToPosition(map.get(word));
			}
			tvIndexWord.setText(word);
			tvIndexWord.setVisibility(View.VISIBLE);
		}

		@Override
		public void onTouchWordLeft() {
			tvIndexWord.setVisibility(View.GONE);
		}
	};

	private void loadData(List<TIchatAddressbook> addressbookList) {
		this.list.clear();
		this.map.clear();
		List<TIchatAddressbook> temp = new ArrayList<TIchatAddressbook>();
		for (TIchatAddressbook addressbook : addressbookList) {
			String sortKey = addressbook.getSpell().substring(0, 1).toUpperCase(Locale.getDefault());
			if (sortKey.matches("^[A-Z]$")) {
				if (!this.map.containsKey(sortKey)) {
					this.map.put(sortKey, this.list.size());
					TIchatAddressbook word = new TIchatAddressbook();
					word.setNickname(sortKey);
					this.list.add(word);
				}
				this.list.add(addressbook);
			} else {
				temp.add(addressbook);
			}
		}
		if (!temp.isEmpty()) {
			this.map.put("#", this.list.size());
			TIchatAddressbook word = new TIchatAddressbook();
			word.setNickname("#");
			this.list.add(word);
			this.list.addAll(temp);
		}

		// this.tvAddressbookItemTotal.setText(addressbookList.size() + this.getResources().getString(R.string.addressbook_total));
		TIchatAddressbook total = new TIchatAddressbook();
		total.setNickname(addressbookList.size() + this.getResources().getString(R.string.addressbook_total));
		this.list.add(total);

		// 设置RecyclerView的分割线
		List<Integer> positionWithoutDivider = new ArrayList<Integer>();
		for (Map.Entry<String, Integer> entry : this.map.entrySet()) {
			positionWithoutDivider.add(entry.getValue() - 1);
			positionWithoutDivider.add(entry.getValue());
		}
		if (this.itemDecoration != null) this.rvAddressbook.removeItemDecoration(itemDecoration);
		this.itemDecoration = new HorizontalDividerItemDecoration.Builder(this.getActivity()).colorResId(R.color.dividing_line).margin(AndroidUtils.dp2px(this.getActivity(), 10))
				.positionWithoutDivider(positionWithoutDivider).build();
		this.rvAddressbook.addItemDecoration(this.itemDecoration);
	}

	private void syncNetworkData() {
		if (!Constants.IS_REFRESH_ADDRESSBOOK) {
			StringBuilder friendIds = new StringBuilder();
			for (TIchatAddressbook addressbook : DatabaseUtils.getAddressbookListByDelete(this.getActivity())) {
				friendIds.append(addressbook.getId()).append(",");
			}

			Map<String, String> params = new HashMap<String, String>();
			params.put("userId", Constants.USER_ID);
			params.put("friendIds", friendIds.toString());
			this.syncNetworkData = new GsonArrayRequest<List<TIchatAddressbook>>(Request.Method.POST, Constants.FRIENDS, params, new TypeToken<List<TIchatAddressbook>>() {
			}, this.requestNetworkDataListener, this.requestNetworkDataErrorListener);
			VolleyUtils.addNormalRequest(this.syncNetworkData, MainActivity.TAG);
		}
	}

	private Response.Listener<List<TIchatAddressbook>> requestNetworkDataListener = new Response.Listener<List<TIchatAddressbook>>() {
		@Override
		public void onResponse(final List<TIchatAddressbook> addressbookList) {
			if (addressbookList != null) {
				loadData(addressbookList);
				adapter.notifyDataSetChanged();
				DatabaseUtils.saveOrUpdateAddressbook(getActivity(), addressbookList);
			}
			Constants.IS_REFRESH_ADDRESSBOOK = true;
		}
	};

	private Response.ErrorListener requestNetworkDataErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			if (error instanceof AuthFailureError) {
				VolleyErrorHelper.sessionTimeout(getActivity(), syncNetworkData, MainActivity.TAG);
			} else {
				// AndroidUtils.alertToast(getActivity(), VolleyErrorHelper.getResId(error));
				Log.e(MainActivity.TAG, error.getMessage(), error.getCause());
			}
		}
	};

	private Response.Listener<ResultBean> removeDataListener = new Response.Listener<ResultBean>() {
		@Override
		public void onResponse(final ResultBean result) {
			if (result.isSuccess()) {
				DatabaseUtils.deleteAddressbook_physical(getActivity(), Long.parseLong(result.getMessage()));
				loadData(DatabaseUtils.getAddressbookList(getActivity()));
				adapter.notifyDataSetChanged();
			} else {
				AndroidUtils.alertToast(getActivity(), result.getMessage());
			}
		}
	};

	private Response.ErrorListener removeDataErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			if (error instanceof AuthFailureError) {
				VolleyErrorHelper.sessionTimeout(getActivity(), removeData, MainActivity.TAG);
			} else {
				AndroidUtils.alertToast(getActivity(), VolleyErrorHelper.getResId(error));
				Log.e(MainActivity.TAG, error.getMessage(), error.getCause());
			}
		}
	};

}
