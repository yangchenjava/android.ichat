package com.yangc.ichat.ui.fragment.friend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yangc.ichat.R;
import com.yangc.ichat.ui.activity.ChatActivity;
import com.yangc.ichat.ui.activity.FriendActivity;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.UILUtils;

public class FriendInfoFragment extends Fragment {

	private FriendActivity friendActivity;
	private DisplayImageOptions options;

	private ImageView ivFriendInfoPhoto;
	private TextView tvFriendInfoNickname;
	private TextView tvFriendInfoUsername;
	private ImageView ivFriendInfoSex;
	private TextView tvFriendInfoPhone;
	private TextView tvFriendInfoSignature;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.friendActivity = (FriendActivity) this.getActivity();
		this.options = UILUtils.getDisplayImageOptions();
		View view = inflater.inflate(R.layout.fragment_friend_info, container, false);
		((ImageView) view.findViewById(R.id.iv_friend_info_backspace)).setOnClickListener(this.backspaceListener);
		this.ivFriendInfoPhoto = (ImageView) view.findViewById(R.id.iv_friend_info_photo);
		this.ivFriendInfoPhoto.setOnTouchListener(this.photoTouchListener);
		this.ivFriendInfoPhoto.setOnClickListener(this.photoClickListener);
		this.tvFriendInfoNickname = (TextView) view.findViewById(R.id.tv_friend_info_nickname);
		this.tvFriendInfoUsername = (TextView) view.findViewById(R.id.tv_friend_info_username);
		this.ivFriendInfoSex = (ImageView) view.findViewById(R.id.iv_friend_info_sex);
		this.tvFriendInfoPhone = (TextView) view.findViewById(R.id.tv_friend_info_phone);
		this.tvFriendInfoSignature = (TextView) view.findViewById(R.id.tv_friend_info_signature);
		((Button) view.findViewById(R.id.btn_friend_info_send)).setOnClickListener(this.sendClickListener);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		String nickname = this.getArguments().getString("nickname");
		Long sex = this.getArguments().getLong("sex");
		String phone = this.getArguments().getString("phone");
		String photo = this.getArguments().getString("photo");
		String signature = this.getArguments().getString("signature");
		String username = this.getArguments().getString("username");

		if (TextUtils.isEmpty(photo)) {
			this.ivFriendInfoPhoto.setImageResource(R.drawable.me_info);
		} else {
			ImageLoader.getInstance().displayImage(Constants.SERVER_URL + photo, this.ivFriendInfoPhoto, this.options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					ivFriendInfoPhoto.setBackgroundResource(R.drawable.shape_bkg_photo);
				}
			});
		}
		this.tvFriendInfoNickname.setText(nickname);
		this.tvFriendInfoUsername.setText(this.getResources().getString(R.string.friend_info_username) + username);
		this.ivFriendInfoSex.setImageResource(sex == 0 ? R.drawable.sex_female : R.drawable.sex_male);
		if (TextUtils.isEmpty(phone)) {
			this.tvFriendInfoPhone.setText(R.string.friend_info_undefined);
		} else {
			this.tvFriendInfoPhone.setText(phone);
		}
		if (TextUtils.isEmpty(signature)) {
			this.tvFriendInfoSignature.setText(R.string.friend_info_undefined);
		} else {
			this.tvFriendInfoSignature.setText(signature);
		}
	}

	// 后退监听
	private View.OnClickListener backspaceListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			friendActivity.onBackPressed();
		}
	};

	// imageview点击效果
	private View.OnTouchListener photoTouchListener = new View.OnTouchListener() {
		private boolean isInside;
		private float x;
		private float y;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v instanceof ImageView) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					((ImageView) v).setColorFilter(Color.parseColor("#88888D"), PorterDuff.Mode.MULTIPLY);
					this.isInside = true;
					this.x = event.getX();
					this.y = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					if (isInside) {
						((ImageView) v).clearColorFilter();
						v.performClick();
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (Math.abs(this.x - event.getX()) > 5 || Math.abs(this.y - event.getY()) > 5) {
						((ImageView) v).clearColorFilter();
						this.isInside = false;
					}
					break;
				}
				return true;
			}
			return false;
		}
	};

	// 头像大图查看
	private View.OnClickListener photoClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String photo = getArguments().getString("photo");
			if (!TextUtils.isEmpty(photo)) {
				Bundle bundle = new Bundle(1);
				bundle.putString("photo", photo);
				friendActivity.addFragmentToStack(new FriendInfoPhotoFragment(), bundle, true);
			}
		}
	};

	// 发送消息监听
	private View.OnClickListener sendClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(friendActivity, ChatActivity.class);
			intent.putExtra("username", getArguments().getString("username"));
			friendActivity.startActivity(intent);
			friendActivity.finish();
		}
	};

}
