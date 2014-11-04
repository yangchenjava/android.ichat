package com.yangc.ichat.fragment.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yangc.ichat.R;
import com.yangc.ichat.activity.FriendActivity;
import com.yangc.ichat.activity.MainActivity;
import com.yangc.ichat.database.bean.TIchatAddressbook;
import com.yangc.ichat.fragment.tab.adapter.AddressbookFragmentAdapter;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.utils.VolleyUtils;
import com.yangc.ichat.volley.GsonArrayRequest;
import com.yangc.ichat.volley.VolleyErrorHelper;
import com.yangc.ichat.widget.IndexScroller;

public class AddressbookFragment extends Fragment {

	private MainActivity mainActivity;

	private ListView lvAddressbook;
	private TextView tvIndexWord;
	private AddressbookFragmentAdapter adapter;

	private List<TIchatAddressbook> list;
	private Map<String, Integer> map;

	private Request<List<TIchatAddressbook>> request;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mainActivity = (MainActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_tab_addressbook, container, false);
		this.lvAddressbook = (ListView) view.findViewById(R.id.lv_addressbook);
		this.lvAddressbook.setOnItemClickListener(this.itemClickListener);
		this.lvAddressbook.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
		IndexScroller isIndex = (IndexScroller) view.findViewById(R.id.is_index);
		isIndex.setOnTouchWordChangedListener(this.touchWordChangedListener);
		this.tvIndexWord = (TextView) view.findViewById(R.id.tv_index_word);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.list = new ArrayList<TIchatAddressbook>();
		this.map = new HashMap<String, Integer>();
		List<TIchatAddressbook> addressbookList = DatabaseUtils.getAddressbookList(this.mainActivity);
		if (AndroidUtils.checkNetwork(this.mainActivity) && addressbookList.isEmpty()) {
			this.requestNetworkData();
		} else {
			this.loadData(addressbookList);
			this.requestNetworkData();
		}

		this.adapter = new AddressbookFragmentAdapter(this.mainActivity, list);
		this.lvAddressbook.setAdapter(this.adapter);
	}

	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			TIchatAddressbook addressbook = list.get(position);
			if (addressbook.getId() != null) {
				Intent intent = new Intent(mainActivity, FriendActivity.class);
				Bundle bundle = new Bundle(6);
				bundle.putString("nickname", addressbook.getNickname());
				bundle.putLong("sex", addressbook.getSex());
				bundle.putString("phone", addressbook.getPhone());
				bundle.putString("photo", addressbook.getPhoto());
				bundle.putString("signature", addressbook.getSignature());
				bundle.putString("username", addressbook.getUsername());
				intent.putExtra("addressbook", bundle);
				mainActivity.startActivity(intent);
			}
		}
	};

	private IndexScroller.OnTouchWordChangedListener touchWordChangedListener = new IndexScroller.OnTouchWordChangedListener() {
		@Override
		public void onTouchWordChanged(String word) {
			if (map != null && map.get(word) != null) {
				lvAddressbook.setSelection(map.get(word));
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
		TIchatAddressbook total = new TIchatAddressbook();
		total.setNickname(addressbookList.size() + this.getResources().getString(R.string.addressbook_total));
		this.list.add(total);
	}

	private void requestNetworkData() {
		if (!Constants.IS_REFRESH_ADDRESSBOOK) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("userId", Constants.USER_ID);
			this.request = new GsonArrayRequest<List<TIchatAddressbook>>(Request.Method.POST, Constants.FRIENDS, params, new TypeToken<List<TIchatAddressbook>>() {
			}, listener, errorListener);
			VolleyUtils.addNormalRequest(this.request, MainActivity.TAG);

			Constants.IS_REFRESH_ADDRESSBOOK = true;
		}
	}

	private Response.Listener<List<TIchatAddressbook>> listener = new Response.Listener<List<TIchatAddressbook>>() {
		@Override
		public void onResponse(List<TIchatAddressbook> addressbookList) {
			loadData(addressbookList);
			adapter.notifyDataSetChanged();
			DatabaseUtils.saveOrUpdateAddressbook(mainActivity, addressbookList);
		}
	};

	private Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			if (error instanceof AuthFailureError) {
				VolleyErrorHelper.sessionTimeout(mainActivity, request, MainActivity.TAG);
			} else {
				AndroidUtils.alertToast(mainActivity, VolleyErrorHelper.getResId(error));
				Log.e(MainActivity.TAG, error.getMessage(), error.getCause());
			}
		}
	};

}
