package com.yangc.ichat.ui.fragment.me;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.yangc.ichat.R;
import com.yangc.ichat.database.bean.TIchatMe;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.DatabaseUtils;

public class MeDetailNicknameFragment extends Fragment {

	private EditText etMeDetailNickname;
	private String nickname;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_me_detail_nickname, container, false);
		((ImageView) view.findViewById(R.id.iv_me_detail_nickname_backspace)).setOnClickListener(this.backspaceListener);
		((Button) view.findViewById(R.id.btn_me_detail_nickname)).setOnClickListener(this.nicknameListener);
		this.etMeDetailNickname = (EditText) view.findViewById(R.id.et_me_detail_nickname);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.nickname = this.getArguments().getString("nickname");
		if (!TextUtils.isEmpty(this.nickname)) {
			this.etMeDetailNickname.setText(this.nickname);
		}
	}

	private void clickBackspace() {
		AndroidUtils.hideSoftInput(this.getActivity());
		this.getActivity().onBackPressed();
	}

	// 后退监听
	private View.OnClickListener backspaceListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			clickBackspace();
		}
	};

	// 昵称监听
	private View.OnClickListener nicknameListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String newNickname = etMeDetailNickname.getText().toString().trim();
			if (TextUtils.isEmpty(newNickname)) {
				AndroidUtils.alertToast(getActivity(), R.string.error_nickname_null);
				return;
			}

			if (!TextUtils.equals(newNickname, nickname)) {
				TIchatMe me = DatabaseUtils.getMe(getActivity());
				me.setNickname(newNickname);
				DatabaseUtils.updateMe(getActivity(), me);
			}
			clickBackspace();
		}
	};

}
