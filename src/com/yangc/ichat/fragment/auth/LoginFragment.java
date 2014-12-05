package com.yangc.ichat.fragment.auth;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yangc.ichat.R;
import com.yangc.ichat.activity.AuthActivity;
import com.yangc.ichat.activity.MainActivity;
import com.yangc.ichat.bean.ResultBean;
import com.yangc.ichat.database.bean.TIchatMe;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.utils.JsonUtils;
import com.yangc.ichat.utils.Md5Utils;
import com.yangc.ichat.utils.VolleyUtils;
import com.yangc.ichat.volley.GsonObjectRequest;
import com.yangc.ichat.volley.VolleyErrorHelper;

public class LoginFragment extends Fragment {

	private EditText etLoginUsername;
	private EditText etLoginPassword;

	private String username;
	private String password;

	private Dialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auth_login, container, false);
		((ImageView) view.findViewById(R.id.iv_login_backspace)).setOnClickListener(this.backspaceListener);
		this.etLoginUsername = (EditText) view.findViewById(R.id.et_login_username);
		this.etLoginPassword = (EditText) view.findViewById(R.id.et_login_password);
		((Button) view.findViewById(R.id.btn_login)).setOnClickListener(this.loginListener);
		return view;
	}

	// 后退监听
	private View.OnClickListener backspaceListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			AndroidUtils.hideSoftInput(getActivity());
			getActivity().getSupportFragmentManager().popBackStack();
		}
	};

	// 登录监听
	private View.OnClickListener loginListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (getActivity() != null) {
				username = etLoginUsername.getText().toString().trim();
				password = Md5Utils.getMD5(etLoginPassword.getText().toString().trim());
				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
					AndroidUtils.alertToast(getActivity(), R.string.error_username_password_null);
				} else {
					progressDialog = AndroidUtils.showProgressDialog(getActivity(), getResources().getString(R.string.text_load), true, true);
					Map<String, String> params = new HashMap<String, String>();
					params.put("username", username);
					params.put("password", password);
					Request<ResultBean> request = new GsonObjectRequest<ResultBean>(Request.Method.POST, Constants.LOGIN, params, ResultBean.class, listener, errorListener);
					request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
					VolleyUtils.addNormalRequest(request, AuthActivity.TAG);
				}
			}
		}
	};

	private Response.Listener<ResultBean> listener = new Response.Listener<ResultBean>() {
		@Override
		public void onResponse(ResultBean result) {
			if (result.isSuccess()) {
				TIchatMe me = JsonUtils.fromJson(result.getMessage(), TIchatMe.class);
				DatabaseUtils.saveMe(getActivity(), me, username, password);
				Constants.saveConstants(getActivity(), "" + me.getUserId(), username, password);

				cancelProgressDialog();
				getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
				getActivity().finish();
			} else {
				cancelProgressDialog();
				AndroidUtils.alertToast(getActivity(), result.getMessage());
			}
		}
	};

	private Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			cancelProgressDialog();
			AndroidUtils.alertToast(getActivity(), VolleyErrorHelper.getResId(error));
			Log.e(AuthActivity.TAG, error.getMessage(), error.getCause());
		}
	};

	private void cancelProgressDialog() {
		if (this.progressDialog != null) {
			this.progressDialog.dismiss();
		}
	}

}
