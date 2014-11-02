package com.yangc.ichat.fragment.tab;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yangc.ichat.R;
import com.yangc.ichat.activity.MainActivity;
import com.yangc.ichat.activity.MeActivity;
import com.yangc.ichat.database.bean.TIchatMe;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.utils.UILUtils;
import com.yangc.ichat.utils.VolleyUtils;
import com.yangc.ichat.volley.GsonObjectRequest;
import com.yangc.ichat.volley.VolleyErrorHelper;

public class MeFragment extends Fragment {

	private MainActivity mainActivity;
	private DisplayImageOptions options;

	private ImageView ivMeInfoPhoto;
	private TextView tvMeInfoNickname;
	private TextView tvMeInfoUsername;

	private Request<TIchatMe> request;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mainActivity = (MainActivity) this.getActivity();
		this.options = UILUtils.getDisplayImageOptions();
		View view = inflater.inflate(R.layout.fragment_tab_me, container, false);
		((RelativeLayout) view.findViewById(R.id.rl_me_info)).setOnClickListener(this.meInfoListener);
		this.ivMeInfoPhoto = (ImageView) view.findViewById(R.id.iv_me_info_photo);
		this.tvMeInfoNickname = (TextView) view.findViewById(R.id.tv_me_info_nickname);
		this.tvMeInfoUsername = (TextView) view.findViewById(R.id.tv_me_info_username);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		TIchatMe me = DatabaseUtils.getMe(this.mainActivity);
		// 如果数据库没有数据,则请求,如果有数据但是文件不存在,则请求
		if (me == null) {
			this.ivMeInfoPhoto.setImageResource(R.drawable.me_info);

			Map<String, String> params = new HashMap<String, String>();
			params.put("userId", Constants.USER_ID);
			this.request = new GsonObjectRequest<TIchatMe>(Request.Method.POST, Constants.USER_INFO, params, TIchatMe.class, listener, errorListener);
			VolleyUtils.addNormalRequest(request, MainActivity.TAG);
		} else {
			this.initMeInfo(me);
		}
	}

	private void initMeInfo(TIchatMe me) {
		if (TextUtils.isEmpty(me.getPhoto())) {
			this.ivMeInfoPhoto.setImageResource(R.drawable.me_info);
		} else {
			ImageLoader.getInstance().displayImage(Constants.SERVER_URL + me.getPhoto(), this.ivMeInfoPhoto, this.options);
		}
		this.tvMeInfoNickname.setText(me.getNickname());
		this.tvMeInfoUsername.setText(this.getResources().getString(R.string.me_info) + me.getUsername());
	}

	// 个人信息监听
	private View.OnClickListener meInfoListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mainActivity.startActivity(new Intent(mainActivity, MeActivity.class));
		}
	};

	private Response.Listener<TIchatMe> listener = new Response.Listener<TIchatMe>() {
		@Override
		public void onResponse(TIchatMe me) {
			DatabaseUtils.saveMe(mainActivity, me, Constants.USERNAME, Constants.PASSWORD);
			initMeInfo(me);
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
