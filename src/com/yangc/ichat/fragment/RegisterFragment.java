package com.yangc.ichat.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.yangc.ichat.R;
import com.yangc.ichat.activity.AuthActivity;
import com.yangc.ichat.activity.MainActivity;
import com.yangc.ichat.bean.ResultBean;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.BitmapUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.Md5Utils;
import com.yangc.ichat.utils.VolleyUtils;
import com.yangc.ichat.volley.MultiPartRequest;
import com.yangc.ichat.volley.VolleyErrorHelper;

public class RegisterFragment extends Fragment {

	private static final int PHOTO_CAMERA = 1; // 拍照
	private static final int PHOTO_LOCAL = 2; // 从相册中选择
	private static final int PHOTO_CUT = 3; // 结果

	private AuthActivity authActivity;

	private TextView tvRegisterBackspace;
	private LinearLayout llRegister_1;
	private LinearLayout llRegister_2;

	private EditText etRegisterUsername;
	private EditText etRegisterPassword_1;
	private EditText etRegisterPassword_2;
	private ImageView etRegisterPhoto;
	private EditText etRegisterNickname;
	private RadioGroup rgRegisterSex;
	private EditText etRegisterPhone;
	private EditText etRegisterSignature;

	private String username;
	private String password;
	private File photo;

	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.authActivity = (AuthActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_auth_register, container, false);
		((ImageView) view.findViewById(R.id.iv_register_backspace)).setOnClickListener(this.backspaceListener);
		this.tvRegisterBackspace = (TextView) view.findViewById(R.id.tv_register_backspace);
		this.llRegister_1 = (LinearLayout) view.findViewById(R.id.ll_register_1);
		this.llRegister_2 = (LinearLayout) view.findViewById(R.id.ll_register_2);

		this.etRegisterUsername = (EditText) view.findViewById(R.id.et_register_username);
		this.etRegisterPassword_1 = (EditText) view.findViewById(R.id.et_register_password_1);
		this.etRegisterPassword_2 = (EditText) view.findViewById(R.id.et_register_password_2);
		((Button) view.findViewById(R.id.btn_register_next)).setOnClickListener(this.registerNextListener);

		this.etRegisterPhoto = (ImageView) view.findViewById(R.id.iv_register_photo);
		this.etRegisterPhoto.setOnClickListener(this.photoListener);
		this.etRegisterNickname = (EditText) view.findViewById(R.id.et_register_nickname);
		this.rgRegisterSex = (RadioGroup) view.findViewById(R.id.rg_register_sex);
		this.etRegisterPhone = (EditText) view.findViewById(R.id.et_register_phone);
		this.etRegisterSignature = (EditText) view.findViewById(R.id.et_register_signature);
		((Button) view.findViewById(R.id.btn_register)).setOnClickListener(this.registerListener);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (this.photo != null && this.photo.getName().equals("me.png")) {
			this.etRegisterPhoto.setImageBitmap(BitmapUtils.getBitmap(this.photo.getAbsolutePath()));
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_CAMERA:
			if (data != null) {
				this.startImageZoom(Uri.fromFile(this.photo));
			} else if (this.photo != null) {
				this.distoryPhoto();
			}
			break;
		case PHOTO_LOCAL:
			if (data != null) {
				this.startImageZoom(data.getData());
			}
			break;
		case PHOTO_CUT:
			if (data != null) {
				this.setImageToView(data);
			} else if (this.photo != null) {
				this.distoryPhoto();
			}
			break;
		}
	}

	private void initPhoto(String fileName) {
		this.photo = new File(AndroidUtils.getCacheDir(authActivity, Constants.DEFAULT_CACHE_DIR), fileName);
		try {
			this.photo.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void distoryPhoto() {
		if (this.photo != null) {
			this.photo.delete();
			this.photo = null;
		}
	}

	private void startImageZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是剪裁图片的宽高
		intent.putExtra("outputX", 512);
		intent.putExtra("outputY", 512);
		intent.putExtra("return-data", true);
		this.startActivityForResult(intent, PHOTO_CUT);
	}

	// 将进行剪裁后的图片显示到UI界面上
	private void setImageToView(Intent data) {
		Bundle bundle = data.getExtras();
		if (bundle != null) {
			Bitmap bitmap = bundle.getParcelable("data");
			FileOutputStream fos = null;
			try {
				this.distoryPhoto();
				this.initPhoto("me.png");
				fos = new FileOutputStream(this.photo);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fos != null) {
						fos.close();
						fos = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void clickBackspace() {
		View currentFocus = this.authActivity.getCurrentFocus();
		if (currentFocus != null) {
			InputMethodManager imm = (InputMethodManager) this.authActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		int visibility = this.llRegister_1.getVisibility();
		if (visibility == View.VISIBLE) {
			if (this.authActivity != null) {
				this.distoryPhoto();
				this.authActivity.getSupportFragmentManager().popBackStack();
			}
		} else if (visibility == View.GONE) {
			this.llRegister_1.setVisibility(View.VISIBLE);
			this.llRegister_2.setVisibility(View.GONE);
			this.tvRegisterBackspace.setText(R.string.fragment_auth_logout);
		}
	}

	// 后退监听
	private View.OnClickListener backspaceListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			clickBackspace();
		}
	};

	// 注册下一步监听
	private View.OnClickListener registerNextListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 验证用户名
			username = etRegisterUsername.getText().toString().trim();
			if (!username.matches("^[\\w\\-\\/\\.]{8,15}$")) {
				AndroidUtils.alertToast(authActivity, R.string.error_username_validate);
				return;
			}

			// 验证密码
			password = etRegisterPassword_1.getText().toString().trim();
			String pwd = etRegisterPassword_2.getText().toString().trim();
			if (!password.matches("^[\\w\\-]{6,15}$") || !pwd.matches("^[\\w\\-]{6,15}$")) {
				AndroidUtils.alertToast(authActivity, R.string.error_password_validate);
				return;
			} else if (!TextUtils.equals(password, pwd)) {
				AndroidUtils.alertToast(authActivity, R.string.error_password_twice_validate);
				return;
			}
			password = Md5Utils.getMD5(password);

			llRegister_1.setVisibility(View.GONE);
			llRegister_2.setVisibility(View.VISIBLE);
			tvRegisterBackspace.setText(R.string.register_previous_button);
		}
	};

	// 头像监听
	private View.OnClickListener photoListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			final AlertDialog alertDialog = new AlertDialog.Builder(authActivity).show();
			alertDialog.setCanceledOnTouchOutside(true);
			Window window = alertDialog.getWindow();
			window.setContentView(R.layout.dialog_select);
			((TextView) window.findViewById(R.id.tv_dialog_select_title)).setText(R.string.dialog_title_photo);
			// 打开相机
			TextView tvDialogSelectFirst = (TextView) window.findViewById(R.id.tv_dialog_select_first);
			tvDialogSelectFirst.setText(R.string.dialog_camera);
			tvDialogSelectFirst.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialog.cancel();
					// 调用系统的拍照功能
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					initPhoto("temp.png");
					// 指定调用相机拍照后照片的储存路径
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
					startActivityForResult(intent, PHOTO_CAMERA);
				}
			});
			// 打开相册
			TextView tvDialogSelectSecond = (TextView) window.findViewById(R.id.tv_dialog_select_second);
			tvDialogSelectSecond.setText(R.string.dialog_local);
			tvDialogSelectSecond.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialog.cancel();
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
					startActivityForResult(intent, PHOTO_LOCAL);
				}
			});
		}
	};

	// 注册监听
	private View.OnClickListener registerListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 验证昵称
			String nickname = etRegisterNickname.getText().toString().trim();
			if (TextUtils.isEmpty(nickname)) {
				AndroidUtils.alertToast(authActivity, R.string.error_nickname_null);
				return;
			}

			// 验证手机
			String phone = etRegisterPhone.getText().toString().trim();
			if (!TextUtils.isEmpty(phone) && !phone.matches("^1[3-8]{1}\\d{9}$")) {
				AndroidUtils.alertToast(authActivity, R.string.error_phone_validate);
				return;
			}

			progressDialog = ProgressDialog.show(authActivity, "", authActivity.getResources().getString(R.string.text_load), true, true);
			Map<String, Object> params = new HashMap<String, Object>(7);
			params.put("username", username);
			params.put("password", password);
			params.put("name", nickname);
			params.put("sex", rgRegisterSex.getCheckedRadioButtonId() == R.id.rb_register_sex_female ? 0 : 1);
			params.put("phone", phone);
			params.put("signature", etRegisterSignature.getText().toString());
			if (photo != null) {
				params.put("photo", photo);
			}
			Request<ResultBean> request = new MultiPartRequest<ResultBean>(Constants.REGISTER, params, ResultBean.class, listener, errorListener);
			VolleyUtils.addMultiPartRequest(request, AuthActivity.TAG);
		}
	};

	private Response.Listener<ResultBean> listener = new Response.Listener<ResultBean>() {
		@Override
		public void onResponse(ResultBean result) {
			if (result.isSuccess()) {
				SharedPreferences.Editor editor = authActivity.getSharedPreferences(Constants.APP, Context.MODE_PRIVATE).edit();
				editor.putString("userId", result.getMessage()).putString("username", username).putString("password", password).commit();
				Constants.USER_ID = result.getMessage();
				Constants.USERNAME = username;
				Constants.PASSWORD = password;
				// TODO 数据库操作
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
