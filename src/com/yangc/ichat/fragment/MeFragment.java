package com.yangc.ichat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.yangc.ichat.R;
import com.yangc.ichat.activity.AuthActivity;
import com.yangc.ichat.activity.MainActivity;
import com.yangc.ichat.bean.ResultBean;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.VolleyUtils;
import com.yangc.ichat.volley.GsonObjectRequest;
import com.yangc.ichat.volley.VolleyErrorHelper;

public class MeFragment extends Fragment {

	private MainActivity mainActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mainActivity = (MainActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_tab_me, container, false);
		((RelativeLayout) view.findViewById(R.id.rl_me_info)).setOnClickListener(this.meInfoListener);
		return view;
	}

	// 个人信息监听
	private View.OnClickListener meInfoListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Request<ResultBean> request = new GsonObjectRequest<ResultBean>(Request.Method.POST, Constants.TEST, null, ResultBean.class, listener, errorListener);
			VolleyUtils.addNormalRequest(request, MainActivity.TAG);
		}
	};

	private Response.Listener<ResultBean> listener = new Response.Listener<ResultBean>() {
		@Override
		public void onResponse(ResultBean result) {
			AndroidUtils.alertToast(mainActivity, result.getMessage());
		}
	};

	private ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			AndroidUtils.alertToast(mainActivity, VolleyErrorHelper.getResId(error));
			Log.e(AuthActivity.TAG, error.getMessage(), error.getCause());
		}
	};

}
