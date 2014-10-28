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
import android.view.inputmethod.InputMethodManager;
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
			View currentFocus = authActivity.getCurrentFocus();
			if (currentFocus != null) {
				InputMethodManager imm = (InputMethodManager) authActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			if (authActivity != null) {
				authActivity.getSupportFragmentManager().popBackStack();
			}
		}
	};

	// 登录监听
	private View.OnClickListener loginListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (authActivity != null) {
				username = etLoginUsername.getText().toString().trim();
				password = Md5Utils.getMD5(etLoginPassword.getText().toString().trim());
				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
					AndroidUtils.alertToast(authActivity, R.string.error_username_password_null);
				} else {
					progressDialog = ProgressDialog.show(authActivity, "", authActivity.getResources().getString(R.string.text_load), true, true);
					Map<String, String> params = new HashMap<String, String>(2);
					params.put("username", username);
					params.put("password", password);
					Request<ResultBean> request = new GsonObjectRequest<ResultBean>(Request.Method.POST, Constants.LOGIN, params, ResultBean.class, listener, errorListener);
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
				me.setUsername(username);
				me.setPassword(password);

				SharedPreferences.Editor editor = authActivity.getSharedPreferences(Constants.APP, Context.MODE_PRIVATE).edit();
				editor.putString("userId", "" + me.getUserId()).putString("username", username).putString("password", password).commit();
				Constants.USER_ID = "" + me.getUserId();
				Constants.USERNAME = username;
				Constants.PASSWORD = password;

				// 数据库操作
				DatabaseUtils.getDaoSession(authActivity).getTIchatMeDao().deleteAll();
				DatabaseUtils.getDaoSession(authActivity).getTIchatMeDao().insert(me);

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
			AndroidUtils.alertToast(authActivity, VolleyErrorHelper.getResId(error));
			Log.e(AuthActivity.TAG, error.getMessage(), error.getCause());
		}
	};

	private void cancelProgressDialog() {
		if (this.progressDialog != null) {
			this.progressDialog.cancel();
		}
	}

}
