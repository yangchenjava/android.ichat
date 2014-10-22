package com.yangc.ichat.fragment;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.yangc.ichat.utils.Md5Utils;
import com.yangc.ichat.utils.VolleyUtils;
import com.yangc.ichat.volley.GsonObjectRequest;

public class LoginFragment extends Fragment {

	private AuthActivity authActivity;

	private EditText etLoginUsername;
	private EditText etLoginPassword;

	private String username;
	private String password;

	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.authActivity = (AuthActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_auth_login, container, false);
		((ImageView) view.findViewById(R.id.iv_login_backspace)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (authActivity != null) {
					authActivity.getSupportFragmentManager().popBackStack();
				}
			}
		});
		this.etLoginUsername = (EditText) view.findViewById(R.id.et_login_username);
		this.etLoginPassword = (EditText) view.findViewById(R.id.et_login_password);
		((Button) view.findViewById(R.id.btn_login)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (authActivity != null) {
					username = etLoginUsername.getText().toString();
					password = Md5Utils.getMD5(etLoginPassword.getText().toString());
					if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
						AndroidUtils.alertToast(authActivity, R.string.error_username_password_null);
					} else {
						progressDialog = ProgressDialog.show(authActivity, "", authActivity.getResources().getString(R.string.text_load), true);
						Map<String, String> params = new HashMap<String, String>(2);
						params.put("username", username);
						params.put("password", password);
						Request<ResultBean> request = new GsonObjectRequest<ResultBean>(Request.Method.POST, Constants.LOGIN, params, ResultBean.class, listener, errorListener);
						VolleyUtils.addRequest(request, AuthActivity.TAG);
					}
				}
			}
		});
		return view;
	}

	private Response.Listener<ResultBean> listener = new Response.Listener<ResultBean>() {
		@Override
		public void onResponse(ResultBean result) {
			if (result.isSuccess()) {
				SharedPreferences.Editor editor = authActivity.getSharedPreferences(Constants.APP, Context.MODE_PRIVATE).edit();
				editor.putString("username", username).putString("password", password).commit();
				cancelProgressDialog();
				authActivity.startActivity(new Intent(authActivity, MainActivity.class));
				authActivity.finish();
			} else {
				cancelProgressDialog();
				AndroidUtils.alertToast(authActivity, result.getMessage());
			}
		}
	};

	private ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			cancelProgressDialog();
			AndroidUtils.alertToast(authActivity, R.string.error_network);
			Log.e(AuthActivity.TAG, error.getMessage(), error.getCause());
		}
	};

	private void cancelProgressDialog() {
		if (this.progressDialog != null) {
			this.progressDialog.cancel();
		}
	}

}
