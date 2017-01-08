package com.yangc.ichat.ui.fragment.auth;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yangc.ichat.R;
import com.yangc.ichat.bean.ResultBean;
import com.yangc.ichat.database.bean.TIchatMe;
import com.yangc.ichat.http.volley.MultiPartRequest;
import com.yangc.ichat.http.volley.VolleyErrorHelper;
import com.yangc.ichat.ui.activity.AuthActivity;
import com.yangc.ichat.ui.activity.MainActivity;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.BitmapUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.utils.JsonUtils;
import com.yangc.ichat.utils.Md5Utils;
import com.yangc.ichat.utils.VolleyUtils;

public class RegisterFragment extends Fragment {

	private static final int PHOTO_CAMERA = 1; // 拍照
	private static final int PHOTO_LOCAL = 2; // 从相册中选择
	private static final int PHOTO_CUT = 3; // 结果
	private static final String PNG_TEMP = "temp.png";
	private static final String PNG_DEST = "me.png";

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
	private File photoFile;

	private Dialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
		// 从相机页面回到当前页面时
		if (this.photoFile != null && this.photoFile.getName().equals(PNG_DEST)) {
			this.etRegisterPhoto.setImageBitmap(BitmapFactory.decodeFile(this.photoFile.getAbsolutePath()));
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_CAMERA:
			if (resultCode == Activity.RESULT_OK && data != null) {
				this.startImageZoom(Uri.fromFile(this.photoFile));
			} else if (this.photoFile != null) {
				this.destoryPhotoFile();
			}
			break;
		case PHOTO_LOCAL:
			if (resultCode == Activity.RESULT_OK && data != null) {
				this.startImageZoom(data.getData());
			}
			break;
		case PHOTO_CUT:
			if (resultCode == Activity.RESULT_OK && data != null) {
				this.setImageToView(data);
			} else if (this.photoFile != null) {
				this.destoryPhotoFile();
			}
			break;
		}
	}

	private void initPhotoFile(String fileName) {
		this.photoFile = new File(AndroidUtils.getCacheDir(this.getActivity(), Constants.CACHE_PORTRAIT), fileName);
		try {
			this.photoFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void destoryPhotoFile() {
		if (this.photoFile != null) {
			this.photoFile.delete();
			this.photoFile = null;
		}
	}

	private void startImageZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", true);
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
			this.destoryPhotoFile();
			this.initPhotoFile(PNG_DEST);
			BitmapUtils.writeBitmapToFile(bitmap, this.photoFile);
		}
	}

	public void clickBackspace() {
		AndroidUtils.hideSoftInput(this.getActivity());
		int visibility = this.llRegister_1.getVisibility();
		if (visibility == View.VISIBLE) {
			this.destoryPhotoFile();
			this.getActivity().getSupportFragmentManager().popBackStack();
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
				AndroidUtils.alertToast(getActivity(), R.string.error_username_validate);
				return;
			}

			// 验证密码
			password = etRegisterPassword_1.getText().toString().trim();
			String pwd = etRegisterPassword_2.getText().toString().trim();
			if (!password.matches("^[\\w\\-]{6,15}$") || !pwd.matches("^[\\w\\-]{6,15}$")) {
				AndroidUtils.alertToast(getActivity(), R.string.error_password_validate);
				return;
			} else if (!TextUtils.equals(password, pwd)) {
				AndroidUtils.alertToast(getActivity(), R.string.error_password_twice_validate);
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
			final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).show();
			alertDialog.setCanceledOnTouchOutside(true);
			Window window = alertDialog.getWindow();
			window.setContentView(R.layout.dialog_select);
			((TextView) window.findViewById(R.id.tv_dialog_select_title)).setText(R.string.dialog_title_photo);
			// 打开相机
			((TextView) window.findViewById(R.id.tv_dialog_select_first)).setText(R.string.dialog_camera);
			((RelativeLayout) window.findViewById(R.id.rl_dialog_select_first)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
					// 调用系统的拍照功能
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					initPhotoFile(PNG_TEMP);
					// 指定调用相机拍照后照片的储存路径
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
					startActivityForResult(intent, PHOTO_CAMERA);
				}
			});
			// 打开相册
			((TextView) window.findViewById(R.id.tv_dialog_select_second)).setText(R.string.dialog_local);
			((RelativeLayout) window.findViewById(R.id.rl_dialog_select_second)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
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
				AndroidUtils.alertToast(getActivity(), R.string.error_nickname_null);
				return;
			}

			// 验证手机
			String phone = etRegisterPhone.getText().toString().trim();
			if (!TextUtils.isEmpty(phone) && !phone.matches("^1[3-8]{1}\\d{9}$")) {
				AndroidUtils.alertToast(getActivity(), R.string.error_phone_validate);
				return;
			}

			progressDialog = AndroidUtils.showProgressDialog(getActivity(), getResources().getString(R.string.text_load), true, true);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("username", username);
			params.put("password", password);
			params.put("nickname", nickname);
			params.put("sex", rgRegisterSex.getCheckedRadioButtonId() == R.id.rb_register_sex_female ? 0 : 1);
			params.put("phone", phone);
			params.put("signature", etRegisterSignature.getText().toString());
			if (photoFile != null) {
				params.put("photo", photoFile);
			}
			Request<ResultBean> request = new MultiPartRequest<ResultBean>(Constants.REGISTER, params, ResultBean.class, listener, errorListener);
			request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			VolleyUtils.addMultiPartRequest(request, AuthActivity.TAG);
		}
	};

	private Response.Listener<ResultBean> listener = new Response.Listener<ResultBean>() {
		@Override
		public void onResponse(ResultBean result) {
			if (result.isSuccess()) {
				destoryPhotoFile();
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
