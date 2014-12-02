package com.yangc.ichat.activity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yangc.ichat.R;
import com.yangc.ichat.activity.adapter.ChatActivityAdapter;
import com.yangc.ichat.comm.bean.ChatBean;
import com.yangc.ichat.comm.bean.ResultBean;
import com.yangc.ichat.database.bean.TIchatAddressbook;
import com.yangc.ichat.database.bean.TIchatHistory;
import com.yangc.ichat.service.CallbackManager;
import com.yangc.ichat.service.PushService;
import com.yangc.ichat.utils.AndroidUtils;
import com.yangc.ichat.utils.Constants;
import com.yangc.ichat.utils.DatabaseUtils;

public class ChatActivity extends Activity implements CallbackManager.OnChatListener {

	private TextView tvChatNickname;
	private TextView tvChatPlus;
	private Button btnChatSend;
	private EditText etChatContent;
	private ListView lvChat;
	private ChatActivityAdapter adapter;

	private String username;
	private TIchatAddressbook addressbook;
	private List<TIchatHistory> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_chat);
		CallbackManager.registerChatListener(this);
		((ImageView) this.findViewById(R.id.iv_chat_backspace)).setOnClickListener(this.backspaceListener);
		this.tvChatNickname = (TextView) this.findViewById(R.id.tv_chat_nickname);
		((ImageView) this.findViewById(R.id.iv_title_bar_friend)).setOnClickListener(this.friendInfoListener);
		this.tvChatPlus = (TextView) this.findViewById(R.id.tv_chat_plus);
		this.btnChatSend = (Button) this.findViewById(R.id.btn_chat_send);
		this.btnChatSend.setOnClickListener(this.sendListener);
		this.etChatContent = (EditText) this.findViewById(R.id.et_chat_content);
		this.etChatContent.addTextChangedListener(this.textChangedListener);
		this.lvChat = (ListView) this.findViewById(R.id.lv_chat);
		this.lvChat.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true, this.scrollListener));
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.username = this.getIntent().getStringExtra("username");
		this.addressbook = DatabaseUtils.getAddressbookByUsername(this, this.username);
		this.tvChatNickname.setText(this.addressbook.getNickname());
		this.list = DatabaseUtils.getHistoryListByUsername_page(this, this.username, 0L);
		this.adapter = new ChatActivityAdapter(this, list, DatabaseUtils.getMe(this).getPhoto(), this.addressbook.getPhoto());
		this.lvChat.setAdapter(this.adapter);
	}

	@Override
	public void onBackPressed() {
		this.goToMain();
	}

	@Override
	public void onChatReceived(final TIchatHistory history) {
		if (this.list != null) {
			synchronized (this.list) {
				this.list.add(history);
				this.adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onResultReceived(ResultBean result) {
		// TODO
	}

	@Override
	public void onNetworkError() {
		AndroidUtils.alertToast(this, R.string.error_network);
	}

	private void goToMain() {
		MainActivity.CURRENT_TAB_ID = R.id.ll_tab_wechat;
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

	// 后退监听
	private View.OnClickListener backspaceListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			goToMain();
		}
	};

	// 好友信息监听
	private View.OnClickListener friendInfoListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(ChatActivity.this, FriendActivity.class);
			Bundle bundle = new Bundle(6);
			bundle.putString("nickname", addressbook.getNickname());
			bundle.putLong("sex", addressbook.getSex());
			bundle.putString("phone", addressbook.getPhone());
			bundle.putString("photo", addressbook.getPhoto());
			bundle.putString("signature", addressbook.getSignature());
			bundle.putString("username", addressbook.getUsername());
			intent.putExtra("addressbook", bundle);
			ChatActivity.this.startActivity(intent);
		}
	};

	// 编辑内容监听
	private TextWatcher textChangedListener = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (TextUtils.isEmpty(s)) {
				tvChatPlus.setVisibility(View.VISIBLE);
				btnChatSend.setVisibility(View.GONE);
			} else {
				tvChatPlus.setVisibility(View.GONE);
				btnChatSend.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	//  发送监听
	private View.OnClickListener sendListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ChatBean chat = new ChatBean();
			chat.setUuid(UUID.randomUUID().toString());
			chat.setFrom(Constants.USERNAME);
			chat.setTo(username);
			chat.setData(etChatContent.getText().toString());

			TIchatHistory history = new TIchatHistory();
			history.setUuid(chat.getUuid());
			history.setUsername(username);
			history.setChat(chat.getData());
			history.setChatStatus(1L);
			history.setTransmitStatus(0L);
			history.setDate(new Date());
			DatabaseUtils.saveHistory(ChatActivity.this, history);

			synchronized (list) {
				list.add(history);
				adapter.notifyDataSetChanged();
			}

			Intent intent = new Intent(ChatActivity.this, PushService.class);
			intent.putExtra(Constants.EXTRA_ACTION, Constants.ACTION_SEND_CHAT);
			intent.putExtra(Constants.EXTRA_CHAT, chat);
			ChatActivity.this.startService(intent);

			etChatContent.setText("");
		}
	};

	// listview滚动监听
	private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lvChat.getFirstVisiblePosition() == 0) {
				if (list != null && !list.isEmpty()) {
					Dialog progressDialog = AndroidUtils.showProgressDialog(ChatActivity.this, getResources().getString(R.string.text_load), true, true);
					synchronized (list) {
						List<TIchatHistory> historyList = DatabaseUtils.getHistoryListByUsername_page(ChatActivity.this, username, list.get(0).getId());
						if (historyList != null && !historyList.isEmpty()) {
							list.addAll(0, historyList);
							adapter.notifyDataSetChanged();
						}
					}
					progressDialog.dismiss();
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		}
	};

}
