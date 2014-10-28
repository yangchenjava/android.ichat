package com.yangc.ichat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yangc.ichat.R;
import com.yangc.ichat.activity.MeActivity;

public class MeDetailFragment extends Fragment {

	private MeActivity meActivity;

	private ImageView ivMeDetailPhoto;
	private TextView tvMeDetailNickname;
	private TextView tvMeDetailPhone;
	private TextView tvMeDetailSex;
	private TextView tvMeDetailSignature;

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
	}

	// 后退监听
	private View.OnClickListener backspaceListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (meActivity != null) {
				meActivity.onBackPressed();
			}
		}
	};

	// 头像监听
	private View.OnClickListener photoListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

		}
	};

	// 昵称监听
	private View.OnClickListener nicknameListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

		}
	};

	// 电话监听
	private View.OnClickListener phoneListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

		}
	};

	// 性别监听
	private View.OnClickListener sexListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

		}
	};

	// 个性签名监听
	private View.OnClickListener signatureListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

		}
	};

}
