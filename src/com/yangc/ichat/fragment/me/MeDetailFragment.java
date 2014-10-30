package com.yangc.ichat.fragment.me;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.yangc.ichat.R;
import com.yangc.ichat.activity.MeActivity;
import com.yangc.ichat.bean.ResultBean;
import com.yangc.ichat.database.bean.TIchatMe;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.BitmapUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;
import com.yangc.ichat.utils.JsonUtils;
import com.yangc.ichat.utils.VolleyUtils;
import com.yangc.ichat.volley.MultiPartRequest;
import com.yangc.ichat.volley.VolleyErrorHelper;

public class MeDetailFragment extends Fragment {

	private static final int PHOTO_CAMERA = 1; // 拍照
	private static final int PHOTO_LOCAL = 2; // 从相册中选择
	private static final int PHOTO_CUT = 3; // 结果
	private static final String PNG_TEMP = "temp.png";
	private static final String PNG_DEST = "me.png";

	private MeActivity meActivity;

	private ImageView ivMeDetailPhoto;
	private TextView tvMeDetailNickname;
	private TextView tvMeDetailPhone;
	private TextView tvMeDetailSex;
	private TextView tvMeDetailSignature;

	private TIchatMe me;
	private File photo;

	private ProgressDialog progressDialog;
	private Request<ResultBean> request;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.meActivity = (MeActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_me_detail, container, false);
		((ImageView) view.findViewById(R.id.iv_me_detail_backspace)).setOnClickListener(this.backspaceListener);
		((RelativeLayout) view.findViewById(R.id.rl_me_detail_photo)).setOnClickListener(this.photoListener);
		this.ivMeDetailPhoto = (ImageView) view.findViewById(R.id.iv_me_detail_photo);
		((LinearLayout) view.findViewById(R.id.ll_me_detail_nickname)).setOnClickListener(this.nicknameListener);
		this.tvMeDetailNickname = (TextView) view.findViewById(R.id.tv_me_detail_nickname);
		((LinearLayout) view.findViewById(R.id.ll_me_detail_phone)).setOnClickListener(this.phoneListener);
		this.tvMeDetailPhone = (TextView) view.findViewById(R.id.tv_me_detail_phone);
		((LinearLayout) view.findViewById(R.id.ll_me_detail_sex)).setOnClickListener(this.sexListener);
		this.tvMeDetailSex = (TextView) view.findViewById(R.id.tv_me_detail_sex);
		((LinearLayout) view.findViewById(R.id.ll_me_detail_signature)).setOnClickListener(this.signatureListener);
		this.tvMeDetailSignature = (TextView) view.findViewById(R.id.tv_me_detail_signature);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		String undefined = this.getResources().getString(R.string.text_undefined);
		this.me = DatabaseUtils.getMe(this.meActivity);
		this.tvMeDetailNickname.setText(TextUtils.isEmpty(this.me.getNickname()) ? undefined : this.me.getNickname());
		this.tvMeDetailPhone.setText(TextUtils.isEmpty(this.me.getPhone()) ? undefined : this.me.getPhone());
		this.tvMeDetailSex.setText(this.me.getSex() == 0 ? "女" : "男");
		this.tvMeDetailSignature.setText(TextUtils.isEmpty(this.me.getSignature()) ? undefined : this.me.getSignature());

		if (this.photo != null && this.photo.getName().equals(PNG_DEST)) {
			progressDialog = ProgressDialog.show(this.meActivity, "", getResources().getString(R.string.text_load), true);
			this.ivMeDetailPhoto.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(BitmapFactory.decodeFile(this.photo.getAbsolutePath())));
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("id", this.me.getId());
			params.put("photo", photo);
			this.request = new MultiPartRequest<ResultBean>(Constants.UPDATE_PERSON_PHOTO, params, ResultBean.class, listener, errorListener);
			VolleyUtils.addMultiPartRequest(this.request, MeActivity.TAG);
		} else {
			ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					ivMeDetailPhoto.setImageResource(R.drawable.me_info);
				}

				@Override
				public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
					if (response.getBitmap() != null) {
						ivMeDetailPhoto.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(response.getBitmap()));
					} else {
						ivMeDetailPhoto.setImageResource(R.drawable.me_info);
					}
				}
			};
			VolleyUtils.getImageLoader().get(Constants.SERVER_URL + me.getPhoto(), imageListener);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_CAMERA:
			if (data != null) {
				this.startImageZoom(Uri.fromFile(this.photo));
			} else if (this.photo != null) {
				this.destoryPhoto();
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
				this.destoryPhoto();
			}
			break;
		}
	}

	private void initPhoto(String fileName) {
		this.photo = new File(AndroidUtils.getCacheDir(this.meActivity, Constants.CACHE_PORTRAIT), fileName);
		try {
			this.photo.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void destoryPhoto() {
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
			this.destoryPhoto();
			this.initPhoto(PNG_DEST);
			BitmapUtils.writeBitmapToFile(bitmap, this.photo);
		}
	}

	private Response.Listener<ResultBean> listener = new Response.Listener<ResultBean>() {
		@Override
		public void onResponse(ResultBean result) {
			cancelProgressDialog();
			if (result.isSuccess()) {
				destoryPhoto();
				me.setPhoto(JsonUtils.fromJson(result.getMessage(), TIchatMe.class).getPhoto());
				me.setPhotoName(me.getPhoto().substring(me.getPhoto().lastIndexOf("/") + 1));
				DatabaseUtils.updateMe(meActivity, me);
			} else {
				AndroidUtils.alertToast(meActivity, result.getMessage());
			}
		}
	};

	private Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			if (error instanceof AuthFailureError) {
				VolleyErrorHelper.sessionTimeout(meActivity, request, MeActivity.TAG);
			} else {
				cancelProgressDialog();
				AndroidUtils.alertToast(meActivity, VolleyErrorHelper.getResId(error));
				Log.e(MeActivity.TAG, error.getMessage(), error.getCause());
			}
		}
	};

	private void cancelProgressDialog() {
		if (this.progressDialog != null) {
			this.progressDialog.cancel();
		}
	}

	// 后退监听
	private View.OnClickListener backspaceListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			meActivity.onBackPressed();
		}
	};

	// 头像监听
	private View.OnClickListener photoListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			final AlertDialog alertDialog = new AlertDialog.Builder(meActivity).show();
			alertDialog.setCanceledOnTouchOutside(true);
			Window window = alertDialog.getWindow();
			window.setContentView(R.layout.dialog_select);
			((TextView) window.findViewById(R.id.tv_dialog_select_title)).setText(R.string.dialog_title_photo);
			// 打开相机
			((TextView) window.findViewById(R.id.tv_dialog_select_first)).setText(R.string.dialog_camera);
			((RelativeLayout) window.findViewById(R.id.rl_dialog_select_first)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialog.cancel();
					// 调用系统的拍照功能
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					initPhoto(PNG_TEMP);
					// 指定调用相机拍照后照片的储存路径
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
					startActivityForResult(intent, PHOTO_CAMERA);
				}
			});
			// 打开相册
			((TextView) window.findViewById(R.id.tv_dialog_select_second)).setText(R.string.dialog_local);
			((RelativeLayout) window.findViewById(R.id.rl_dialog_select_second)).setOnClickListener(new View.OnClickListener() {
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

	// 昵称监听
	private View.OnClickListener nicknameListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle(1);
			bundle.putString("nickname", me.getNickname());
			meActivity.addFragmentToStack(new MeDetailNicknameFragment(), bundle, true);
		}
	};

	// 电话监听
	private View.OnClickListener phoneListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle(1);
			bundle.putString("phone", me.getPhone());
			meActivity.addFragmentToStack(new MeDetailPhoneFragment(), bundle, true);
		}
	};

	// 性别监听
	private View.OnClickListener sexListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			final AlertDialog alertDialog = new AlertDialog.Builder(meActivity).show();
			alertDialog.setCanceledOnTouchOutside(true);
			Window window = alertDialog.getWindow();
			window.setContentView(R.layout.dialog_select);
			((TextView) window.findViewById(R.id.tv_dialog_select_title)).setText(R.string.dialog_title_sex);
			final ImageView ivDialogSelectFirst = (ImageView) window.findViewById(R.id.iv_dialog_select_first);
			final ImageView ivDialogSelectSecond = (ImageView) window.findViewById(R.id.iv_dialog_select_second);
			if (me.getSex() == 1) {
				ivDialogSelectFirst.setVisibility(View.VISIBLE);
			} else {
				ivDialogSelectSecond.setVisibility(View.VISIBLE);
			}
			// 男
			((TextView) window.findViewById(R.id.tv_dialog_select_first)).setText(R.string.dialog_male);
			((RelativeLayout) window.findViewById(R.id.rl_dialog_select_first)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (me.getSex() != 1) {
						ivDialogSelectFirst.setVisibility(View.VISIBLE);
						ivDialogSelectSecond.setVisibility(View.GONE);
						TIchatMe me = DatabaseUtils.getMe(meActivity);
						me.setSex(1L);
						DatabaseUtils.updateMe(meActivity, me);
					}
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							alertDialog.cancel();
						}
					}, 300);
				}
			});
			// 女
			((TextView) window.findViewById(R.id.tv_dialog_select_second)).setText(R.string.dialog_female);
			((RelativeLayout) window.findViewById(R.id.rl_dialog_select_second)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (me.getSex() != 0) {
						ivDialogSelectFirst.setVisibility(View.GONE);
						ivDialogSelectSecond.setVisibility(View.VISIBLE);
						TIchatMe me = DatabaseUtils.getMe(meActivity);
						me.setSex(0L);
						DatabaseUtils.updateMe(meActivity, me);
					}
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							alertDialog.cancel();
						}
					}, 300);
				}
			});
		}
	};

	// 个性签名监听
	private View.OnClickListener signatureListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle(1);
			bundle.putString("signature", me.getSignature());
			meActivity.addFragmentToStack(new MeDetailSignatureFragment(), bundle, true);
		}
	};

}
